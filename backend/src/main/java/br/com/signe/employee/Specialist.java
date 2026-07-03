package SalonBeauty;

import java.util.Set;

public class Specialist extends Employees{
    private Set<Specialization> specialization;

    public Specialist(String name, String cpf, String id, String email, String hasPhone, String address, double baseSalary, Set<Specialization> specialization) {
        super(name, cpf, id, email, hasPhone, address, baseSalary);
        this.specialization = specialization;
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
        System.out.println("Especialização: "+this.specialization);
    }
}
