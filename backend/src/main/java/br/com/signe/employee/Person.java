package br.com.signe.employee;

public abstract class  Person {

    private String name;
    private String cpf;
    private String id;
    private String email;
    private String hasPhone;
    private String address;

    public Person(String name, String cpf, String id, String email, String hasPhone, String address) {
        this.name = name;
        this.cpf = cpf;
        this.id = id;
        this.email = email;
        this.hasPhone = hasPhone;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHasPhone(){
        return hasPhone;
    }

    public void setHasPhone(String hasPhone){
        this.hasPhone = hasPhone;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
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
