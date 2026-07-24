package br.com.signe.employee;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("ADMINISTRATOR")
public class Administrator extends Employees {

    public Administrator() {}

    public Administrator(String name, String cpf, String id, String email, String hasPhone, String address, double baseSalary, EmployeeStatus status) {
        super(name, cpf, id, email, hasPhone, address, baseSalary, status);
    }

    @Override
    public void showDetails() {
        super.showDetails();
    }
}
