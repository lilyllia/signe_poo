package br.com.signe.client.domain;

import jakarta.persistence.*;
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

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    // email TEM que ser único, pois é um dado sensível e de identificação do cliente
    @Column(unique = true)
    private String email;

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

    // por enquanto só vou incluir setters pra informações de contato, pois são as únicas que podem ser alteradas (para os casos de erro no cadastro, vou desenvolver algo depois
    public void updateContactInfo(String email, String phoneNumber) {
        if(email != null && !email.isEmpty()) {
            this.email = email;
        }
        if(phoneNumber != null && !phoneNumber.isEmpty()) {
            this.phoneNumber = phoneNumber;
        }
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