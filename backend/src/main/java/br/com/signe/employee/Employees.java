package br.com.signe.employee;

public class Employees extends Person {

    private double baseSalary;
    private br.com.signe.employee.EmployeeStatus status;

    public Employees(String name, String cpf, String id, String email, String hasPhone, String address, double baseSalary) {
        super(name, cpf, id, email, hasPhone, address);
        this.baseSalary = baseSalary;
        this.status = br.com.signe.employee.EmployeeStatus.ACTIVE;
    }
    public double getBaseSalary(){
        return baseSalary;
    }
    public void setBaseSalary(double baseSalary){
        this.baseSalary = baseSalary;
    }
    public br.com.signe.employee.EmployeeStatus getStatus(){
        return status;
    }
    public void setStatus(br.com.signe.employee.EmployeeStatus status){
        this.status = status;
    }
    @Override
    public void showDetails() {
        super.showDetails();
    }
}
