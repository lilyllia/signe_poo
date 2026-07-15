package br.com.signe.client.domain;

import br.com.signe.client.domain.enums.HairLength;
import br.com.signe.client.domain.enums.HairPorosity;
import br.com.signe.client.domain.enums.HairShape;
import br.com.signe.client.domain.enums.HairThickness;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Embeddable
@Getter
public class HairProfile {

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_shape")
    private HairShape shape;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_porosity")
    private HairPorosity porosity;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_thickness")
    private HairThickness thickness;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_length")
    private HairLength length;

    @Column(name = "is_chemically_treated")
    private boolean chemicallyTreated;

    @Column(name = "is_damaged")
    private boolean damaged;

    // jpa
    protected HairProfile() {}

    public HairProfile(HairShape shape, HairPorosity porosity, HairThickness thickness,
                       HairLength length, boolean chemicallyTreated, boolean damaged) {
        this.shape = shape;
        this.porosity = porosity;
        this.thickness = thickness;
        this.length = length;
        this.chemicallyTreated = chemicallyTreated;
        this.damaged = damaged;
    }

}
