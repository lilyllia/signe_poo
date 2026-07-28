package br.com.signe.finance;

import br.com.signe.schedule.Scheduling;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private UUID id;

    @OneToOne(optional = false)
    private Scheduling scheduling;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime paymentDate;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
        this.status = status;    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void pay() {

        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Somente pagamentos pendentes podem ser pagos.");
        }

        status = PaymentStatus.COMPLETED;
        paymentDate = LocalDateTime.now();
    }
    public void cancel(){

        if(status != PaymentStatus.PENDING ){
            throw new IllegalStateException(
                    "Somente pagamentos pendentes podem ser cancelados.");
        }
        status = PaymentStatus.FAILED;
    }
    public void refund(){
        if(status != PaymentStatus.COMPLETED){
            throw new IllegalStateException(
                    "Somente pagamentos concluídos podem ser reembolsados.");
        }
        status = PaymentStatus.REFUNDED;
    }
}
