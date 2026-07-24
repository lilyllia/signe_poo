package br.com.signe.employee;

import jakarta.persistence.*;

@Entity
    @DiscriminatorValue("EMPLOYEE")
    public class Employees extends Person {

        @Column(nullable = false)
        private double baseSalary;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private EmployeeStatus status;

        public Employees() {}

    public Employees(String name, String cpf, String id, String email, String hasPhone, String address, double baseSalary, EmployeeStatus status) {
        super(name, cpf, id, email, hasPhone, address);
        this.baseSalary = baseSalary;
        this.status = status;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    @Override
    public void showDetails() {
        super.showDetails();
    }
}
