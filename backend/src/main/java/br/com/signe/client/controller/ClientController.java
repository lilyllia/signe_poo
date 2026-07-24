package br.com.signe.client.controller;

import br.com.signe.client.domain.AnamnesisRecord;
import br.com.signe.client.domain.Client;
import br.com.signe.client.dto.AllergyRequest;
import br.com.signe.client.dto.ClientProfileDTO;
import br.com.signe.client.dto.RegisterClientRequest;
import br.com.signe.client.dto.UpdateContactRequest;
import br.com.signe.client.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// classe que controla os pedidos da api e retorna no formato json
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // ==========================================
    // 1. CRIAR CLIENTE (POST)
    // URL: /api/clients
    // ==========================================
    @PostMapping
    public ResponseEntity<Client> registerClient(@RequestBody RegisterClientRequest request) {
        Client newClient = clientService.registerNewClient(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phoneNumber(),
                request.dateOfBirth()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(newClient);
    }

    // ==========================================
    // 1.2 CRIAR FICHA DE ANAMNESE (POST)
    // URL: /api/clients
    // ==========================================
    @PostMapping("/{clientId}/anamnesis")
    public ResponseEntity<AnamnesisRecord> createAnamnesis(@PathVariable UUID clientId) {
        AnamnesisRecord newRecord = clientService.createAnamnesisRecord(clientId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRecord);
    }

    // ==========================================
    // 2. VISUALIZAR PERFIL DO CLIENTE (GET)
    // URL: /api/clients/{clientId}/profile
    // ==========================================
    @GetMapping("/{clientId}/profile")
    public ResponseEntity<ClientProfileDTO> getClientProfile(@PathVariable UUID clientId) {
        ClientProfileDTO profileDTO = clientService.getFullClientProfile(clientId);
        return ResponseEntity.ok(profileDTO);
    }

    // ==========================================
    // 2.1 LISTAR CLIENTES (GET)
    // URL: /api/clients
    // ==========================================
    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientService.getAllActiveClients();
        return ResponseEntity.ok(clients);
    }

    // ==========================================
    // 3. ATUALIZAR DADOS DE CONTATO (PUT)
    // URL: /api/clients/{clientId}/contact
    // ==========================================
    @PutMapping("/{clientId}/contact")
    public ResponseEntity<Client> updateContactInfo(
            @PathVariable UUID clientId,
            @RequestBody UpdateContactRequest request) {

        Client updatedClient = clientService.updateClientContactInfo(clientId, request.email(), request.phone());
        return ResponseEntity.ok(updatedClient);
    }

    // ==========================================
    // 4. ADICIONAR ALERGIAS NA FICHA DE ANAMNESE (PUT or PATCH)
    // URL: /api/clients/{clientId}/anamnesis/allergy
    // ==========================================
    @PutMapping("/{clientId}/anamnesis/allergy")
    public ResponseEntity<AnamnesisRecord> addAllergy(
            @PathVariable UUID clientId,
            @RequestBody AllergyRequest request) {

        AnamnesisRecord updatedRecord = clientService.addClientAllergy(clientId, request.allergy());
        return ResponseEntity.ok(updatedRecord);
    }

    // ==========================================
    // 5. SOFT DELETE DO CLIENTE (DELETE)
    // URL: /api/clients/{clientId}
    // ==========================================
    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID clientId) {
        // é um soft delete, mas usamos o método http delete mesmo
        clientService.deleteClient(clientId);

        // pra ter uma resposta se deu certo
        return ResponseEntity.noContent().build();
    }
}
