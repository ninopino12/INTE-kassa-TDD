import java.math.BigDecimal;

public final class Product {
    private final String id;
    private final String name;
    private final Unit unit;
    private final Money unitPrice;

    public Product(String id, String name, Unit unit, Money unitPrice) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Varuid måste finnas");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Varunamn måste finnas");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Enhet måste finnas");
        }
        if (unitPrice == null || unitPrice.ore() == 0) {
            throw new IllegalArgumentException("Priset måste vara positivt");
        }

        this.id = id;
        this.name = name;
        this.unit = unit;
        this.unitPrice = unitPrice;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Unit unit() {
        return unit;
    }

    public Money unitPrice() {
        return unitPrice;
    }

    public void validatePositiveQuantity(BigDecimal quantity) {
        validateQuantity(quantity, false);
    }

    public void validateStockQuantity(BigDecimal quantity) {
        validateQuantity(quantity, true);
    }

    private void validateQuantity(BigDecimal quantity, boolean allowZero) {
        if (quantity == null) {
            throw new IllegalArgumentException("Mängd måste finnas");
        }
        if (quantity.signum() < 0 || (!allowZero && quantity.signum() == 0)) {
            throw new IllegalArgumentException("Mängden är inte tillåten");
        }

        int maxScale = unit == Unit.PIECE ? 0 : 3;

        if (quantity.stripTrailingZeros().scale() > maxScale) {
            throw new IllegalArgumentException(
                    "Mängden måste vara hela styck eller hela gram");
        }
    }
}