import java.math.BigDecimal;

public final class CartLine {
    private final Product product;
    private final BigDecimal quantity;

    public CartLine(Product product, BigDecimal quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Vara måste finnas");
        }
        product.validatePositiveQuantity(quantity);
        this.product = product;
        this.quantity = quantity;
    }

    public Product product() {
        return product;
    }

    public BigDecimal quantity() {
        return quantity;
    }
}