package br.com.signe.service;

import jakarta.persistence.*;

@Entity
@Table(name="procedure")
public class Procedure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcedureCategory category;

    @Column(nullable = false)
    private double cost;

    @Column(name = "raw_material")
    private String rawMaterial;

    @Column(name = "average_duration")
    private int averageDuration;

    protected Procedure(){
    }

    public Procedure(String name, ProcedureCategory category, double cost, String rawMaterial, int averageDuration){
        this.name = name;
        this.category = category;
        this.cost = cost;
        this.rawMaterial = rawMaterial;
        this.averageDuration = averageDuration; //minutes
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcedureCategory getCategory() {
        return category;
    }

    public void ProcedureCategory(ProcedureCategory category) {
        this.category = category;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getRawMaterial() {
        return rawMaterial;
    }

    public void setRawMaterial(String rawMaterial) {
        this.rawMaterial = rawMaterial;
    }

    public int getAverageDuration() {
        return averageDuration;
    }

    public void setAverageDuration(int averageDuration) {
        this.averageDuration = averageDuration;
    }

    @Override
    public String toString() {
        return "Procedimento{Nome: '" + name + "' |Categoria: " + category + " |Custo: R$" + cost +
                " |Matéria Prima: " + rawMaterial + " |Duração Média: " + averageDuration + "}";
    }
}
