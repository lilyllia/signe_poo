package br.com.signe.employee.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalTime;
import java.util.Set;

@Entity
@DiscriminatorValue("SPECIALIST")
@Getter
public class Specialist extends Employee {

    @Column(name = "commission_percentage")
    private double commissionPercentage;
    private double productivity;

    @Column(name = "shift_start")
    private LocalTime start;

    @Column(name = "shift_finish")
    private LocalTime finish;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tb_specialist_specialization", joinColumns = @JoinColumn(name = "specialist_id"))
    @Column(name = "specialization")
    @Enumerated(EnumType.STRING)
    private Set<Specialization> specializations;

    protected Specialist() {}

    public Specialist(String name, String cpf, String email, String phone, String address, double baseSalary, EmployeeStatus status, double commissionPercentage, LocalTime start, LocalTime finish, Set<Specialization> specializations) {
        super(name, cpf, email, phone, address, baseSalary, status);
        this.commissionPercentage = commissionPercentage;
        this.start = start;
        this.finish = finish;
        this.specializations = specializations;
        this.productivity = 0.0; // não faz sentido definir um valor inicial
    }

    public void updateSpecializations(Set<Specialization> newSpecializations) {
        this.specializations = newSpecializations;
    }

    public void updateSchedule(LocalTime start, LocalTime finish) {
        if (start.isBefore(finish)) {
            this.start = start;
            this.finish = finish;
        } else {
            throw new IllegalArgumentException("O horário de início deve ser antes do horário de término.");
        }
    }

    public void updateCommission(double newPercentage) {
        if (newPercentage >= 0 && newPercentage <= 1.0) {
            this.commissionPercentage = newPercentage;
        } else {
            throw new IllegalArgumentException("A comissão deve ser um valor entre 0.0 e 1.0.");
        }
    }

    public void addProductivity(double amount) {
        if(amount > 0) {
            this.productivity += amount;
        }
    }

    public void resetProductivity() {
        this.productivity = 0.0;
    }

    public boolean isWithinWorkingHours(LocalTime appointmentStart, LocalTime appointmentEnd) {
        if (appointmentStart == null || appointmentEnd == null) return false;

        return !appointmentStart.isBefore(this.start) && !appointmentEnd.isAfter(this.finish);
    }

    @Override
    public double calculateSalary() {
        double commission = this.productivity * this.commissionPercentage;
        return getBaseSalary() + commission;
    }
}