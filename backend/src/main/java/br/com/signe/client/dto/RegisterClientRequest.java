package br.com.signe.client.dto;

import java.time.LocalDate;

// Used by POST /api/clients
public record RegisterClientRequest(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDate dateOfBirth
) {
}
