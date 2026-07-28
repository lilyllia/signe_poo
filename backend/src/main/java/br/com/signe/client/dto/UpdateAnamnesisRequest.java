package br.com.signe.client.dto;

import br.com.signe.client.domain.enums.*;

public record UpdateAnamnesisRequest(
        SkinType skinType,
        HairShape hairShape,
        HairPorosity hairPorosity,
        HairThickness hairThickness,
        HairLength hairLength,
        boolean chemicallyTreated,
        boolean damaged,
        String progressNotes
) {}
