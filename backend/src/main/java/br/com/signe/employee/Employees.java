package br.com.signe.employee;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
    @DiscriminatorValue("EMPLOYEE")
    public class Employees extends Person {

        @Column(nullable = false)
        private double baseSalary;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private EmployeeStatus status;

        public Employees() {}

    public Employees(String name, String cpf, String id, String email, String hasPhone, String address, double baseSalary) {
        super(name, cpf, id, email, hasPhone, address);
        this.baseSalary = baseSalary;
        this.status = br.com.signe.employee.EmployeeStatus.ACTIVE;
    }

    @Override
    public void showDetails() {
        super.showDetails();
    }
}
