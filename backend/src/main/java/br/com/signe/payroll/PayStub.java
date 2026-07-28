package br.com.signe.payroll;

import br.com.signe.employee.Specialist;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pay_stub")
public class PayStub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "specialist_id", nullable = false)
    private Specialist specialist;

    @Column(name = "reference_month", nullable = false)
    private int referenceMonth;

    @Column(name = "reference_year", nullable = false)
    private int referenceYear;

    @ElementCollection
    @CollectionTable(name = "pay_stub_services", joinColumns = @JoinColumn(name = "pay_stub_id"))
    private List<ProcedureValue> services = new ArrayList<>();

    // soma de todos os valores dos procedimentos realizados no período
    @Column(nullable = false)
    private double productivity;

    @Column(name = "total_salary", nullable = false)
    private double totalSalary;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    protected PayStub() {}

    public PayStub(Specialist specialist, int referenceMonth, int referenceYear) {
        this.specialist = specialist;
        this.referenceMonth = referenceMonth;
        this.referenceYear = referenceYear;
        this.services = new ArrayList<>();
        this.productivity = 0;
        this.generatedAt = LocalDateTime.now();
    }

    // Adiciona um serviço prestado à folha e já soma o valor à produtividade.
    public void addService(String procedureName, double value) {
        services.add(new ProcedureValue(procedureName, value));
        this.productivity += value;
    }

    /** Imprime todos os serviços prestados com seus respectivos valores. */
    public void printServices() {
        System.out.println("===== Folha de Pagamento =====");
        System.out.println("Especialista: " + specialist.getName());
        System.out.println("Período: " + referenceMonth + "/" + referenceYear);
        System.out.println("-------------------------------");
        for (ProcedureValue pv : services) {
            System.out.printf("%-35s R$ %.2f%n", pv.getProcedureName(), pv.getValue());
        }
        System.out.println("-------------------------------");
        System.out.printf("Produtividade total: R$ %.2f%n", productivity);
        System.out.printf("Salário total: R$ %.2f%n", totalSalary);
        System.out.println("===============================");
    }

    public Long getId() { return id; }
    public Specialist getSpecialist() { return specialist; }
    public int getReferenceMonth() { return referenceMonth; }
    public int getReferenceYear() { return referenceYear; }
    public List<ProcedureValue> getServices() { return services; }
    public double getProductivity() { return productivity; }
    public double getTotalSalary() { return totalSalary; }
    public void setTotalSalary(double totalSalary) { this.totalSalary = totalSalary; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
}