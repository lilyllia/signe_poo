package br.com.signe;

import br.com.signe.client.domain.AnamnesisRecord;
import br.com.signe.client.domain.Client;
import br.com.signe.client.domain.HairProfile;
import br.com.signe.client.domain.enums.*;
import br.com.signe.client.dto.ClientProfileDTO;
import br.com.signe.client.repository.AnamnesisRecordRepository;
import br.com.signe.client.repository.ClientRepository;
import br.com.signe.client.service.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// iniciar a base de dados antes de cada teste e limpar depois de cada teste
@SpringBootTest
@Transactional
class ClientTests {

	// usa o service e repository para testar a lógica de negócio e a persistência
	@Autowired
	private ClientService clientService;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private AnamnesisRecordRepository anamnesisRecordRepository;

	@Test
	void shouldSuccessfullyRegisterNewClient() {
		Client newClient = clientService.registerNewClient(
				"Maria",
				"da Silva",
				"maria.dasilva@email.com",
				"9988-7766",
				LocalDate.of(1995, 8, 20)
		);

		assertNotNull(newClient.getId(), "O cliente deve ter um ID após ser salvo no banco de dados");
		assertEquals("Maria", newClient.getFirstName());

		assertTrue(clientRepository.findByEmail("maria.dasilva@email.com").isPresent());
	}

	@Test
	void shouldThrowExceptionWhenRegisteringDuplicateEmail() {
		clientService.registerNewClient(
				"João", "Souza", "joao@email.com", "(88)99910-1112", LocalDate.of(1990, 1, 1)
		);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			clientService.registerNewClient(
					"João", "Clone", "joao@email.com", "(88)99910-1112", LocalDate.of(1992, 2, 2)
			);
		});

		assertEquals("Esse email já está cadastrado! Por favor, use outro email.", exception.getMessage());
	}

	@Test
	void shouldReturnFullProfileWithAnamnesisRecord() {
		// 1. Arrange: Register a mock client
		Client newClient = clientService.registerNewClient(
				"Leila", "Cabeleleila", "leila@email.com", "555-7777", LocalDate.of(1993, 3, 10)
		);

		// 2. Arrange: Create the HairProfile and AnamnesisRecord, linking it via the newClient.getId()
		HairProfile hairProfile = new HairProfile(
				HairShape.CURLY,
				HairPorosity.HIGH,
				HairThickness.FINE,
				HairLength.MEDIUM,
				true, // chemicallyTreated
				false // damaged
		);

		AnamnesisRecord mockupRecord = new AnamnesisRecord(
				newClient.getId(),
				SkinType.COMBINATION,
				hairProfile,
				"Realizou um big chop há 2 anos."
		);

		// Save the mockup record directly to the database for the test
		anamnesisRecordRepository.save(mockupRecord);

		// 3. Act: Ask your Service to fetch the FULL profile!
		ClientProfileDTO fullProfile = clientService.getFullClientProfile(newClient.getId());

		// 4. Assert: Check if the Service successfully found and combined the data
		assertNotNull(fullProfile, "A DTO do perfil completo não deveria ser nula.");
		assertEquals("Leila Cabeleleila", fullProfile.fullName(), "Dados do cliente não batem com o esperado.");

		// The ultimate test: Did it attach the Anamnesis Record?
		assertNotNull(fullProfile.anamnesisRecord(), "A ficha de anamnese deveria estar presente no DTO.");
		assertEquals(SkinType.COMBINATION, fullProfile.anamnesisRecord().getSkinType());
		assertEquals(HairShape.CURLY, fullProfile.anamnesisRecord().getHairProfile().getShape());
		assertEquals("Realizou um big chop há 2 anos.", fullProfile.anamnesisRecord().getProgressNotes());
	}
}