package SalonBeauty;

import java.util.Set;
import java.time.LocalDateTime;

public class Specialist extends Employees{
    private LocalDateTime start;
    private LocalDateTime finish;
    private Set<Specialization> specialization;

    public Specialist(String name, String cpf, String id, String email, String hasPhone, String address, double baseSalary,LocalDateTime start, LocalDateTime finish, Set<Specialization> specialization) {
        super(name, cpf, id, email, hasPhone, address, baseSalary);
        this.start = start;
        this.finish = finish;
        this.specialization = specialization;
    }

    public LocalDateTime getFinish() {
        return finish;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public Set<Specialization> getSpecialization() {
        return specialization;
    }
    public void setSpecialization(Set<Specialization> specialization) {
        this.specialization = specialization;
    }
    @Override
    public void showDetails() {
        super.showDetails();
        System.out.println("Horário de início do expediente: "+this.start);
        System.out.println("Horário de término do expediente: "+this.finish);
        System.out.println("Especialização: "+this.specialization);
    }
}
