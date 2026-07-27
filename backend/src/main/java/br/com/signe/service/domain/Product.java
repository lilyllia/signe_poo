package br.com.signe.service.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="tb_product") //tabela ent coloca tb
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(name = "unit_measure")
    private String unitMeasure;

    private String brand;

    @Column(name = "inventory_quantity", nullable = false)
    private int inventoryQuantity;

    @Column(name = "is_active")
    private boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    protected Product() {}

    public Product(String name, double price, String unitMeasure, String brand, int inventoryQuantity) {
        this.name = name;
        this.price = price;
        this.unitMeasure = unitMeasure;
        this.brand = brand;
        this.inventoryQuantity = inventoryQuantity;
    }

    public void updateDetails(String name, String unitMeasure, String brand) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        this.unitMeasure = unitMeasure;
        this.brand = brand;
    }

    public void updatePrice(double newPrice) {
        if (newPrice >= 0) {
            this.price = newPrice;
        } else {
            throw new IllegalArgumentException("O preço do produto não pode ser negativo.");
        }
    }

    // sem raw setters
    public void addStock(int quantity) {
        if (quantity > 0) {
            this.inventoryQuantity += quantity;
        }
    }

    public void removeStock(int quantity) {
        if (quantity > 0 && this.inventoryQuantity >= quantity) {
            this.inventoryQuantity -= quantity;
        } else {
            throw new IllegalArgumentException("Estoque insuficiente para a remoção!");
        }
    }

    public void deactivate() {
        this.active = false;
    }
}