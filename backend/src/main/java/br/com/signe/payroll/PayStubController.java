package br.com.signe.payroll;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payroll")
public class PayStubController {

    private final PayStubService payStubService;

    public PayStubController(PayStubService payStubService) {
        this.payStubService = payStubService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(
            @RequestParam UUID specialistId,
            @RequestParam int month,
            @RequestParam int year) {
        try {
            PayStub payStub = payStubService.generatePayStub(specialistId, month, year);
            return ResponseEntity.status(HttpStatus.CREATED).body(PayStubResponseDTO.fromEntity(payStub));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/specialist/{specialistId}")
    public List<PayStubResponseDTO> listBySpecialist(@PathVariable UUID specialistId) {
        return payStubService.listBySpecialist(specialistId).stream()
                .map(PayStubResponseDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            PayStub payStub = payStubService.findByIdOrThrow(id);
            return ResponseEntity.ok(PayStubResponseDTO.fromEntity(payStub));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }
}