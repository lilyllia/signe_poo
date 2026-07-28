package br.com.signe.employee.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Administrator extends Employee {

    protected Administrator() {}

    public Administrator(String name, String cpf, String email, String phone, String address, double baseSalary, EmployeeStatus status) {
        super(name, cpf, email, phone, address, baseSalary, status);
    }

    @Override
    public double calculateSalary() {
        return getBaseSalary();
    }
}