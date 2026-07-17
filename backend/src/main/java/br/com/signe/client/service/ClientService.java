package br.com.signe.client.service;

import br.com.signe.client.domain.AnamnesisRecord;
import br.com.signe.client.domain.Client;
import br.com.signe.client.dto.ClientProfileDTO;
import br.com.signe.client.repository.AnamnesisRecordRepository;
import br.com.signe.client.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    public Client registerNewClient(String firstName, String lastName, String email, String phone, LocalDate dob) {

        // 1.1 verifica a regra de negócio do email
        Optional<Client> existingClient = clientRepository.findByEmail(email);
        if (existingClient.isPresent()) {
            throw new IllegalArgumentException("Esse email já está cadastrado! Por favor, use outro email.");
        }

        // 1.2 cria o objeto do cliente (ainda não salvo no banco)
        Client newClient = new Client(firstName, lastName, email, phone, dob);

        // 1.3 salva no banco
        return clientRepository.save(newClient);
    }

    // =================================
    // READ - 2 buscar dados do cliente
    // =================================
    public ClientProfileDTO getFullClientProfile(UUID clientId) {

        // 2.1 busca no repositorio de clientes o cliente pelo id. se não encontrar, lança uma exceção.
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado!"));

        // 2.2 agora vai no repositório de fichas buscar a do cliente específico
        AnamnesisRecord anamnesisRecord = anamnesisRecordRepository.findByClientId(clientId).orElse(null);

        // 2.3 retornar o DTO com os dados do cliente e da ficha de anamnese

        return new ClientProfileDTO(
                client.getId(),
                client.getFullName(),
                client.getEmail(),
                client.getPhoneNumber(),
                client.getDateOfBirth(),
                anamnesisRecord // se n tiver dados vai ser null
        );
    }

    // =================================
    // UPDATE - 3 atualizar dados do cliente
    // =================================
    @Transactional
    public Client updateClientContactInfo(UUID clientId, String newEmail, String newPhone) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado!"));

        // If they are changing the email, make sure the new one isn't taken by someone else
        if (!client.getEmail().equals(newEmail)) {
            Optional<Client> existingEmail = clientRepository.findByEmail(newEmail);
            if (existingEmail.isPresent()) {
                throw new IllegalArgumentException("Esse email já está cadastrado! Por favor, use outro email.");
            }
        }

        client.updateContactInfo(newEmail, newPhone);
        return clientRepository.save(client);
    }

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

    // ==========================================
    // 4. EXCLUIR CLIENTE (SOFT DELETE)
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