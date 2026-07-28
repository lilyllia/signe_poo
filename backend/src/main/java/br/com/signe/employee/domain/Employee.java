package br.com.signe.employee.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_employee")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@Getter // Only getters, keeping it secure!
public abstract class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phone; // nomenclatura confusa "has" indica um booleano

    private String address;

    @Column(name = "base_salary", nullable = false)
    private double baseSalary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatus status;

    @Column(name = "is_active")
    private boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    protected Employee() {}

    public Employee(String name, String cpf, String email, String phone, String address, double baseSalary, EmployeeStatus status) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.baseSalary = baseSalary;
        this.status = status;
    }

    public void updateProfile(String name, String phone, String address) {
        if (name != null && !name.trim().isEmpty()) this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public void updateSalary(double newBaseSalary) {
        if (newBaseSalary >= 0) {
            this.baseSalary = newBaseSalary;
        } else {
            throw new IllegalArgumentException("O salário base não pode ser negativo.");
        }
    }

    public void changeStatus(EmployeeStatus newStatus) {
        this.status = newStatus;
    }

    public void deactivate() {
        this.active = false;
        this.status = EmployeeStatus.INACTIVE;
    }

    public abstract double calculateSalary();
}