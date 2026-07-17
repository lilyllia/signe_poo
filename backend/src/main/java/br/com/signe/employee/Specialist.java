package br.com.signe.employee;

import java.time.LocalTime;
import java.util.Set;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class Specialist extends Employees {
    private LocalTime start;
    private LocalTime finish;
    private Set<br.com.signe.employee.Specialization> specialization;

    public Specialist(String name, String cpf, String id, String email, String hasPhone, String address, double baseSalary, LocalTime start, LocalTime finish, Set<Specialization> specialization) {
        super(name, cpf, id, email, hasPhone, address, baseSalary);
        this.start = start;
        this.finish = finish;
        this.specialization = specialization;
    }

    public LocalTime getFinish() {
        return finish;
    }

    public LocalTime getStart() {
        return start;
    }

    public Set<br.com.signe.employee.Specialization> getSpecialization() {
        return specialization;
    }
    public void setSpecialization(Set<br.com.signe.employee.Specialization> specialization) {
        this.specialization = specialization;
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
