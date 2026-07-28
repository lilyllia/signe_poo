package br.com.signe.client.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_client")
@Getter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Nome é obrigatório.")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Sobrenome é obrigatório")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    // email TEM que ser único, pois é um dado sensível e de identificação do cliente
    @Column(unique = true, nullable = true)
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // soft delete: adicionando um campo para indicar se o cliente está ativo ou não, ao invés de deletar o registro do banco de dados
    @Column(name = "is_active")
    private boolean active = true;

    // jpa
    protected Client() {}

    // construtor
    public Client(String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    //setters pras informacoes de contato
    public void updateProfile(String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth) {
        if (firstName != null && !firstName.trim().isEmpty()) {
            this.firstName = firstName;
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            this.lastName = lastName;
        }

        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }
}