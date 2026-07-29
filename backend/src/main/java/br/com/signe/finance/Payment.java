package br.com.signe.finance;

import br.com.signe.schedule.Scheduling;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private UUID id;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToOne(optional = false)
    @JoinColumn(name = "scheduling_id", nullable = false, unique = true)
    private Scheduling scheduling;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    protected Payment() {
    }

    public Payment(Scheduling scheduling, double amount, PaymentMethod paymentMethod) {
        this.scheduling = scheduling;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
    }

    public UUID getId() {
        return id;
    }

    public Scheduling getScheduling() {
        return scheduling;
    }

    public void setScheduling(Scheduling scheduling) {
        this.scheduling = scheduling;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void pay() {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Somente pagamentos pendentes podem ser pagos.");
        }
        status = PaymentStatus.COMPLETED;
        paymentDate = LocalDateTime.now();
    }

    public void cancel() {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Somente pagamentos pendentes podem ser cancelados.");
        }
        status = PaymentStatus.FAILED;
    }

    public void refund() {
        if (status != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Somente pagamentos concluídos podem ser reembolsados.");
        }
        status = PaymentStatus.REFUNDED;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", scheduling=" + (scheduling != null ? scheduling.getSchedulingId() : null) +
                ", amount=" + amount +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                ", paymentDate=" + paymentDate +
                '}';
    }
}