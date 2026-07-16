package br.com.signe.service;

public class Product {
    private String name;
    private double price;
    private String unitMeasure;
    private String brand;
    private int inventoryQuantity;

    public Product(String name, double price, String unitMeasure, String brand, int inventoryQuantity) {
        this.name = name;
        this.price = price;
        this.unitMeasure = unitMeasure;
        this.brand = brand;
        this.inventoryQuantity = inventoryQuantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUnitMeasure() {
        return unitMeasure;
    }

    public void setUnitMeasure(String unitMeasure) {
        this.unitMeasure = unitMeasure;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getInventoryQuantity() {
        return inventoryQuantity;
    }

    public void setInventoryQuantity(int inventoryQuantity) {
        this.inventoryQuantity = inventoryQuantity;
    }

    @Override
    public String toString() {
        return "Produto{Nome: '" + name + "' |Preço: R$" + price + " |Unidade de medida: " + unitMeasure +
                " |Marca: " + brand + " Quantidade em estoque: " + inventoryQuantity + "}";
    }
}
