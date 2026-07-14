package br.com.signe.client;

import java.time.LocalDate;
import java.util.UUID;

// o tipo record já vem com constrututor, getter e funções de equals() e hashcode()
public record ClientProfileDTO(
        UUID clientId,
        String fullName,
        String email,
        String phoneNumber,
        LocalDate dateOfBirth,

        // se o cliente nao tiver ficha, vai enviar null
        AnamnesisRecord anamnesisRecord
) {}
