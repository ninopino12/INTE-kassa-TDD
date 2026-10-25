import java.math.BigDecimal;

public final class PricedLine {
    private final Product product;
    private final BigDecimal quantity;
    private final Money originalPrice;
    private final Money discount;
    private final Money finalPrice;

    public PricedLine(Product product, BigDecimal quantity,
                      Money originalPrice, Money discount) {
        if (product == null || originalPrice == null || discount == null) {
            throw new IllegalArgumentException("Varan och beloppen måste finnas");
        }
        product.validatePositiveQuantity(quantity);
        this.product = product;
        this.quantity = quantity;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.finalPrice = originalPrice.subtract(discount);
    }

    public Product product() {
        return product;
    }

    public BigDecimal quantity() {
        return quantity;
    }

    public Money originalPrice() {
        return originalPrice;
    }

    public Money discount() {
        return discount;
    }

    public Money finalPrice() {
        return finalPrice;
    }
}
