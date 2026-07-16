package br.com.signe.service;

public class Procedure {
    private String name;
    private String category;
    private double cost;
    private String rawMaterial;
    private int averageDuration;

    public Procedure(String name, String category, double cost, String rawMaterial, int averageDuration){
        this.name = name;
        this.category = category;
        this.cost = cost;
        this.rawMaterial = rawMaterial;
        this.averageDuration = averageDuration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
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
