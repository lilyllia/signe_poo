package SalonBeauty;

public class Employees extends Person{

    private double baseSalary;
    private EmployeeStatus status;

    public Employees(String name, String cpf, String id, String email, String hasPhone, String address, double baseSalary) {
        super(name, cpf, id, email, hasPhone, address);
        this.baseSalary = baseSalary;
        this.status = EmployeeStatus.ACTIVE;
    }
    public double getBaseSalary(){
        return baseSalary;
    }
    public void setBaseSalary(double baseSalary){
        this.baseSalary = baseSalary;
    }
    public EmployeeStatus getStatus(){
        return status;
    }
    public void setStatus(EmployeeStatus status){
        this.status = status;
    }
}
