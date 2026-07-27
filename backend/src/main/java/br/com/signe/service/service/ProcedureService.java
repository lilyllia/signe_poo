package br.com.signe.service.service;

import br.com.signe.service.domain.ProcedureCategory;
import br.com.signe.service.domain.Procedure;
import br.com.signe.service.repository.ProcedureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProcedureService {

    private final ProcedureRepository procedureRepository;

    public ProcedureService(ProcedureRepository procedureRepository) {
        this.procedureRepository = procedureRepository;
    }

    public List<Procedure> getAllActiveProcedures() {
        return procedureRepository.findAll().stream()
                .filter(Procedure::isActive)
                .toList();
    }

    @Transactional
    public Procedure addProcedure(Procedure procedure) {
        return procedureRepository.save(procedure);
    }

    @Transactional
    public Procedure updateProcedureDetails(UUID procedureId, String name, ProcedureCategory category, String rawMaterial, int averageDuration) {
        Procedure procedure = procedureRepository.findById(procedureId)
                .orElseThrow(() -> new IllegalArgumentException("Procedimento não encontrado."));

        procedure.updateDetails(name, category, rawMaterial, averageDuration);
        return procedureRepository.save(procedure);
    }

    @Transactional
    public Procedure updateProcedureCost(UUID procedureId, double newCost) {
        Procedure procedure = procedureRepository.findById(procedureId)
                .orElseThrow(() -> new IllegalArgumentException("Procedimento não encontrado."));

        procedure.updateCost(newCost);
        return procedureRepository.save(procedure);
    }

    @Transactional
    public void deleteProcedure(UUID procedureId) {
        Procedure procedure = procedureRepository.findById(procedureId)
                .orElseThrow(() -> new IllegalArgumentException("Procedimento não encontrado."));

        procedure.deactivate();
        procedureRepository.save(procedure);
    }
}