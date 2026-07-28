package br.com.signe.finance;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestParam Long schedulingId,
            @RequestParam PaymentMethod paymentMethod) {
        try {
            Payment payment = paymentService.createPaymentForScheduling(schedulingId, paymentMethod);
            return ResponseEntity.status(HttpStatus.CREATED).body(payment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    public List<Payment> listAll() {
        return paymentService.listAll();
    }

    @GetMapping("/status/{status}")
    public List<Payment> listByStatus(@PathVariable PaymentStatus status) {
        return paymentService.listByStatus(status);
    }

    @GetMapping("/method/{paymentMethod}")
    public List<Payment> listByPaymentMethod(@PathVariable PaymentMethod paymentMethod) {
        return paymentService.listByPaymentMethod(paymentMethod);
    }

    @GetMapping("/scheduling/{schedulingId}")
    public ResponseEntity<Payment> findByScheduling(@PathVariable Long schedulingId) {
        return paymentService.findByScheduling(schedulingId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<?> pay(@PathVariable UUID id) {
        return handleTransition(() -> paymentService.pay(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable UUID id) {
        return handleTransition(() -> paymentService.cancel(id));
    }

    @PutMapping("/{id}/refund")
    public ResponseEntity<?> refund(@PathVariable UUID id) {
        return handleTransition(() -> paymentService.refund(id));
    }

    private ResponseEntity<?> handleTransition(Supplier<Payment> action) {
        try {
            return ResponseEntity.ok(action.get());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        }
    }
}