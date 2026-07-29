package br.com.signe.finance;

import br.com.signe.schedule.Scheduling;
import br.com.signe.schedule.SchedulingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SchedulingRepository schedulingRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          SchedulingRepository schedulingRepository) {
        this.paymentRepository = paymentRepository;
        this.schedulingRepository = schedulingRepository;
    }


    // CREATE - gera o pagamento a partir de um agendamento existente

    @Transactional
    public Payment createPaymentForScheduling(UUID schedulingId, PaymentMethod paymentMethod) {

        Scheduling scheduling = schedulingRepository.findById(schedulingId)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));

        Optional<Payment> existingPayment = paymentRepository.findBySchedulingId(schedulingId);
        if (existingPayment.isPresent()) {
            throw new IllegalStateException("Já existe um pagamento gerado para este agendamento.");
        }

        double amount = scheduling.getProcedure().getCost();

        Payment payment = new Payment(scheduling, amount, paymentMethod);

        return paymentRepository.save(payment);
    }


    // UPDATE - transições de estado (delegadas à entidade)

    @Transactional
    public Payment pay(UUID paymentId) {
        Payment payment = findByIdOrThrow(paymentId);
        payment.pay();
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment cancel(UUID paymentId) {
        Payment payment = findByIdOrThrow(paymentId);
        payment.cancel();
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment refund(UUID paymentId) {
        Payment payment = findByIdOrThrow(paymentId);
        payment.refund();
        return paymentRepository.save(payment);
    }

    // READ

    public Payment findByIdOrThrow(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado."));
    }

    public Optional<Payment> findByScheduling(UUID schedulingId) {
        return paymentRepository.findBySchedulingId(schedulingId);
    }

    public List<Payment> listByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    public List<Payment> listByPaymentMethod(PaymentMethod paymentMethod) {
        return paymentRepository.findByPaymentMethod(paymentMethod);
    }

    public List<Payment> listAll() {
        return paymentRepository.findAll();
    }

    public List<Payment> listCompletedByPeriod(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findByStatusAndPaymentDateBetween(PaymentStatus.COMPLETED, start, end);
    }
}