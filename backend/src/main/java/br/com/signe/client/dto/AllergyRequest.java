package br.com.signe.client.dto;

// Used by PUT /api/clients/{id}/anamnesis/allergy
public record AllergyRequest(
        String allergy
) {
}
