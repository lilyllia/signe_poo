package br.com.signe.service.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="tb_procedure") //se eh table nomeia com tb
@Getter
public class Procedure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

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

    @Column(name = "is_active")
    private boolean active = true; //soft delete

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    protected Procedure() {}

    public Procedure(String name, ProcedureCategory category, double cost, String rawMaterial, int averageDuration){
        this.name = name;
        this.category = category;
        this.cost = cost;
        this.rawMaterial = rawMaterial;
        this.averageDuration = averageDuration; //minutes
    }

    public void updateDetails(String name, ProcedureCategory category, String rawMaterial, int averageDuration) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (category != null) {
            this.category = category;
        }
        this.rawMaterial = rawMaterial;
        if (averageDuration > 0) {
            this.averageDuration = averageDuration;
        }
    }

    // sem fazer setter aleatório p tudo plmdds
    public void updateCost(double newCost) {
        if (newCost >= 0) {
            this.cost = newCost;
        } else {
            throw new IllegalArgumentException("O custo do procedimento não pode ser negativo.");
        }
    }

    public void deactivate() {
        this.active = false;
    }
}