package br.com.signe.client.dto;

import java.time.LocalDate;

//PUT /api/clients/{id}/profile
public record UpdateProfileRequest(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDate dateOfBirth
) {}