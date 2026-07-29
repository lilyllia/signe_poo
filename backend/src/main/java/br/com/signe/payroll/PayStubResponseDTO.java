package br.com.signe.payroll;

import java.time.LocalDateTime;
import java.util.List;

public record PayStubResponseDTO(
        Long id,
        String specialistName,
        int referenceMonth,
        int referenceYear,
        List<ProcedureValueDTO> services,
        double productivity,
        double totalSalary,
        LocalDateTime generatedAt
) {
    public static PayStubResponseDTO fromEntity(PayStub payStub) {
        List<ProcedureValueDTO> serviceDTOs = payStub.getServices().stream()
                .map(pv -> new ProcedureValueDTO(pv.getProcedureName(), pv.getValue()))
                .toList();

        return new PayStubResponseDTO(
                payStub.getId(),
                payStub.getSpecialist().getName(),
                payStub.getReferenceMonth(),
                payStub.getReferenceYear(),
                serviceDTOs,
                payStub.getProductivity(),
                payStub.getTotalSalary(),
                payStub.getGeneratedAt()
        );
    }
}