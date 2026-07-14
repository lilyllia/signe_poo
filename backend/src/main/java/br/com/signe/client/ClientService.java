package br.com.signe.client;

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

    // 1. registrar cliente novo
    @Transactional // IMPORTANTE: garante que, se houver algum erro no meio do processo, o banco de dados não vai salvar nada (rollback)
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

    // 2. retornar os dados completos do cliente
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
}
