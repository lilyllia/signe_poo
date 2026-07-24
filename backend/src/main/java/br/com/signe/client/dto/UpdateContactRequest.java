package br.com.signe.client.dto;

// Used by PUT /api/clients/{id}/contact
public record UpdateContactRequest(
        String email,
        String phone
) {
}
