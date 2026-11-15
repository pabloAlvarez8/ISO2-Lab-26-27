package es.uclm.rental_platform.domain;

public enum PaymentMethod {
    TARJETA("Tarjeta de crédito/débito"),
    BIZUM("Bizum"),
    PAYPAL("PayPal"),
    TRANSFERENCIA("Transferencia bancaria");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

