package SalonBeauty;

public enum Specialization {

    HAIRDRESSER("Cabeleireiro(a)"),

    MANICURE("Manicure"),

    PODIATRIST("Podóloga"),

    COLORING("Colorista"),

    TERRAPIN_CAPILAR("Terapia-Capilar");

    private final String description;

    Specialization(String description) {
            this.description = description;
    }

    public String getDescription() {
        return description;
    }
}