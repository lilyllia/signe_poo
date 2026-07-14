package br.com.signe.client;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_anamnesis_record")
@Getter //precisa do lombok pra funcionar
public class AnamnesisRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "client_id", nullable = false, unique = true)
    private UUID clientId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private SkinType skinType;

    @Enumerated(EnumType.STRING)
    private HairProfile hairProfile;

    @ElementCollection
    @CollectionTable(name = "tb_form_allergies", joinColumns = @JoinColumn(name = "form_id"))
    @Column(name = "allergy")
    private List<String> allergies = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String progressNotes;

    // construtor vazio exigido pelo jpa
    protected AnamnesisRecord() {}

    public AnamnesisRecord(UUID clientId, SkinType skinType, HairProfile hairProfile, String notes) {
        this.clientId = clientId;
        this.skinType = skinType;
        this.hairProfile = hairProfile;
        this.progressNotes = notes;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }

    public void addAllergy(String allergy) {
        this.allergies.add(allergy);
    }

    public void setHairProfile(HairProfile hairProfile) {
        this.hairProfile = hairProfile;
    }

    public void setSkinType(SkinType skinType) {
        this.skinType = skinType;
    }

    public void setProgressNotes(String progressNotes) {
        this.progressNotes = progressNotes;
    }
}