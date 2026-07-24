package br.com.signe.service;

import jakarta.persistence.*;

@Entity
@Table(name="product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(name = "unit_measure")
    private String unitMeasure;

    private String brand;

    @Column(name = "inventory_quantity", nullable = false)
    private int inventoryQuantity;

    protected Product(){
    }

    public Product(String name, double price, String unitMeasure, String brand, int inventoryQuantity) {
        this.name = name;
        this.price = price;
        this.unitMeasure = unitMeasure;
        this.brand = brand;
        this.inventoryQuantity = inventoryQuantity;
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
