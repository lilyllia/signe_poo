package br.com.signe.employee;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("SPECIALIST")
public class Specialist extends Employees {

    @Column(nullable = false)
    private LocalTime start;

    @Column(nullable = false)
    private LocalTime finish;

    @ElementCollection
    @CollectionTable(name = "specialist_specialization", joinColumns = @JoinColumn(name = "specialist_id"))
    @Column(name = "specialization")
    @Enumerated(EnumType.STRING)
    private Set<Specialization> specialization;

    public Specialist() {}

    public Specialist(String name, String cpf, String id, String email, String hasPhone, String address, double baseSalary, EmployeeStatus status, LocalTime start, LocalTime finish, Set<Specialization> specialization) {
        super(name, cpf, id, email, hasPhone, address, baseSalary, status);
        this.start = start;
        this.finish = finish;
        this.specialization = specialization;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getFinish() {
        return finish;
    }

    public void setFinish(LocalTime finish) {
        this.finish = finish;
    }

    public Set<Specialization> getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Set<Specialization> specialization) {
        this.specialization = specialization;
    }

    public boolean isWithinWorkingHours(LocalTime startScheduling, LocalTime finishScheduling) {
        return !startScheduling.isBefore(this.start)
                && !finishScheduling.isAfter(this.finish);
    }


    @Override
    public void showDetails() {
        super.showDetails();
        System.out.println("Horário de início do expediente: "+this.start);
        System.out.println("Horário de término do expediente: "+this.finish);
        String descricoes = this.specialization.stream()
                .map(Specialization::getDescription)
                .collect(Collectors.joining(", "));
        System.out.println("Especialização: " + descricoes);
    }
}
