package finance;

public enum PaymentStatus {

    PENDING("Pendente"),

    COMPLETED("Concluído"),

    FAILED("Falhou"),

    REFUNDED("Reembolsado");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
