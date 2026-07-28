package br.com.signe.schedule;

import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        String start,
        String finish,
        String status,
        ClientSummary client,
        ProcedureSummary procedure
) {
    public record ClientSummary(UUID id, String firstName, String lastName) {}
    public record ProcedureSummary(UUID id, String name) {}
}