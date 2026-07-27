package br.com.signe.employee.domain;

public enum Specialization {

    HAIRDRESSER("Cabeleireiro(a)"),

    MANICURE("Manicure/Pedicure"),

    PODIATRIST("Podóloga"),

    COLORING("Colorista"),

    CAPILLARY_THERAPY("Terapia-Capilar");

    private final String description;

    Specialization(String description) {
            this.description = description;
    }

    public String getDescription() {
        return description;
    }
}