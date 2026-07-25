package finance;

import br.com.signe.schedule.Scheduling;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import finance.PaymentMethod;
import finance.PaymentStatus;

@Entity
public class Payment {

    @Id
    @Column(unique = true, nullable = false)
    private String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
