package br.com.signe.employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "person_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Person {


    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @Column(nullable = false)
    private String email;

    @Column
    private String hasPhone;

    @Column
    private String address;

    public Person() {}

    public Person(String name, String cpf, String id, String email, String hasPhone, String address) {
        this.name = name;
        this.cpf = cpf;
        this.id = id;
        this.email = email;
        this.hasPhone = hasPhone;
        this.address = address;
    }

    public void showDetails() {
        System.out.println("Nome: " + this.name);
        System.out.println("CPF: " + this.cpf);
        System.out.println("ID: " + this.id);
        System.out.println("Email: " + this.email);
        System.out.println("Telefone: " + this.hasPhone);
        System.out.println("Endereço: " + this.address);
    }
}
