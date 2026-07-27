package br.com.signe.service.controller;

import br.com.signe.service.domain.ProcedureCategory;
import br.com.signe.service.domain.Procedure;
import br.com.signe.service.service.ProcedureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/procedures")
public class ProcedureController {

    private final ProcedureService procedureService;

    public ProcedureController(ProcedureService procedureService) {
        this.procedureService = procedureService;
    }

    @GetMapping
    public ResponseEntity<List<Procedure>> getAllProcedures() {
        return ResponseEntity.ok(procedureService.getAllActiveProcedures());
    }

    @PostMapping
    public ResponseEntity<Procedure> createProcedure(@RequestBody CreateProcedureRequest request) {
        Procedure newProcedure = new Procedure(
                request.name(),
                request.category(),
                request.cost(),
                request.rawMaterial(),
                request.averageDuration()
        );
        Procedure savedProcedure = procedureService.addProcedure(newProcedure);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProcedure);
    }

    @PutMapping("/{id}/details")
    public ResponseEntity<Procedure> updateProcedureDetails(
            @PathVariable UUID id,
            @RequestBody UpdateProcedureDetailsRequest request) {
        Procedure updatedProcedure = procedureService.updateProcedureDetails(
                id, request.name(), request.category(), request.rawMaterial(), request.averageDuration());
        return ResponseEntity.ok(updatedProcedure);
    }

    @PutMapping("/{id}/cost")
    public ResponseEntity<Procedure> updateProcedureCost(
            @PathVariable UUID id,
            @RequestBody UpdateProcedureCostRequest request) {
        Procedure updatedProcedure = procedureService.updateProcedureCost(id, request.newCost());
        return ResponseEntity.ok(updatedProcedure);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProcedure(@PathVariable UUID id) {
        procedureService.deleteProcedure(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequests(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    record CreateProcedureRequest(String name, ProcedureCategory category, double cost, String rawMaterial, int averageDuration) {}
    record UpdateProcedureDetailsRequest(String name, ProcedureCategory category, String rawMaterial, int averageDuration) {}
    record UpdateProcedureCostRequest(double newCost) {}
}