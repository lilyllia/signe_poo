package br.com.signe.client.service;

import br.com.signe.client.domain.AnamnesisRecord;
import br.com.signe.client.domain.Client;
import br.com.signe.client.domain.HairProfile;
import br.com.signe.client.dto.ClientProfileDTO;
import br.com.signe.client.dto.UpdateAnamnesisRequest;
import br.com.signe.client.repository.AnamnesisRecordRepository;
import br.com.signe.client.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final AnamnesisRecordRepository anamnesisRecordRepository;

    public ClientService(ClientRepository clientRepository, AnamnesisRecordRepository anamnesisRecordRepository) {
        this.clientRepository = clientRepository;
        this.anamnesisRecordRepository = anamnesisRecordRepository;
    }

    // ================== métodos do serviço ==================

    // =================================
    // CREATE - 1 registrar novo cliente
    // =================================
    @Transactional
    // IMPORTANTE: garante que, se houver algum erro no meio do processo, o banco de dados não vai salvar nada (rollback)
    public Client registerNewClient(String firstName, String lastName, String email, String phoneNumber, LocalDate dob) {

        // 1.0 verifica campos obrigatórios
        if (firstName == null || firstName.trim().isEmpty() ||
                lastName == null || lastName.trim().isEmpty() ||
                phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome, sobrenome e telefone são obrigatórios para o cadastro inicial.");
        }

        // 1.2 cria o objeto do cliente (ainda não salvo no banco)
        Client newClient = new Client(firstName, lastName, email, phoneNumber, dob);

        // 1.3 salva no banco
        return clientRepository.save(newClient);
    }
    // criar ficha de anamnese vazia pro novo cliente
    @Transactional
    public AnamnesisRecord createAnamnesisRecord(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado!"));

        if (!client.isActive()) {
            throw new IllegalArgumentException("Não é possível criar ficha para cliente inativo.");
        }

        Optional<AnamnesisRecord> existingRecord = anamnesisRecordRepository.findByClientId(clientId);
        if (existingRecord.isPresent()) {
            throw new IllegalArgumentException("Este cliente já possui uma ficha de anamnese.");
        }

        AnamnesisRecord newRecord = new AnamnesisRecord(clientId);
        return anamnesisRecordRepository.save(newRecord);
    }

    // =================================
    // READ - 2 buscar dados do cliente
    // =================================
    public List<Client> getAllActiveClients() {
        return clientRepository.findAll().stream()
                .filter(Client::isActive)
                .toList();
    }

    public ClientProfileDTO getFullClientProfile(UUID clientId) {

        // 2.1 busca no repositorio de clientes o cliente pelo id. se não encontrar, lança uma exceção.
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado!"));

        if (!client.isActive()) {
            throw new IllegalArgumentException("Esse cliente foi desativado do sistema.");
        }

        // 2.2 agora vai no repositório de fichas buscar a do cliente específico
        AnamnesisRecord anamnesisRecord = anamnesisRecordRepository.findByClientId(clientId).orElse(null);

        // 2.3 retornar o DTO com os dados do cliente e da ficha de anamnese

        return new ClientProfileDTO(
                client.getId(),
                client.getFirstName(),
                client.getLastName(),
                client.getEmail(),
                client.getPhoneNumber(),
                client.getDateOfBirth(),
                anamnesisRecord // se n tiver dados vai ser null
        );
    }

    // =================================
    // UPDATE - 3 atualizar dados básicos do cliente
    // =================================

    //atualizar perfil
    @Transactional
    public Client updateClientProfile(UUID clientId, String firstName, String lastName, String newEmail, String newPhone, LocalDate dob) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado!"));

        if (newEmail != null && !newEmail.trim().isEmpty() && !newEmail.equals(client.getEmail())) {
            Optional<Client> existingEmail = clientRepository.findByEmail(newEmail);
            if (existingEmail.isPresent()) {
                throw new IllegalArgumentException("Esse email já está cadastrado! Por favor, use outro email.");
            }
        }

        client.updateProfile(firstName, lastName, newEmail, newPhone, dob);
        return clientRepository.save(client);
    }

    //adicionar alergia
    @Transactional
    public AnamnesisRecord addClientAllergy(UUID clientId, String allergy) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado!"));

        if (!client.isActive()) {
            throw new IllegalArgumentException("Não é possível atualizar a anamnese de um cliente removido.");
        }

        AnamnesisRecord record = anamnesisRecordRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Ficha de anamnese não encontrada para este cliente. Por favor, crie uma primeiro."));

        record.addAllergy(allergy);

        return anamnesisRecordRepository.save(record);
    }

    //atualizar ficha
    @Transactional
    public AnamnesisRecord updateAnamnesisRecord(UUID clientId, UpdateAnamnesisRequest req) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado!"));

        if (!client.isActive()) {
            throw new IllegalArgumentException("Não é possível atualizar a anamnese de um cliente removido.");
        }

        AnamnesisRecord record = anamnesisRecordRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Ficha de anamnese não encontrada. Crie uma ficha em branco primeiro."));

        // Cria um novo objeto Embutido (HairProfile) com os dados recebidos
        HairProfile newHairProfile = new HairProfile(
                req.hairShape(),
                req.hairPorosity(),
                req.hairThickness(),
                req.hairLength(),
                req.chemicallyTreated(),
                req.damaged()
        );

        // Usa o novo método que criamos na entidade para atualizar tudo!
        record.updateDetails(req.skinType(), newHairProfile, req.progressNotes());

        return anamnesisRecordRepository.save(record);
    }

    // ==========================================
    //DELETE - 4 excluir cliente (soft delete)
    // ==========================================
    @Transactional
    public void deleteClient(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found!"));

        client.deactivate();
        clientRepository.save(client);

        anamnesisRecordRepository.findByClientId(clientId).ifPresent(record -> {
            record.deactivate();
            anamnesisRecordRepository.save(record);
        });
    }
}