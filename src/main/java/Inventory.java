import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class Inventory {
    private final Map<String, Product> products = new HashMap<>();
    private final Map<String, BigDecimal> quantities = new HashMap<>();

    public void register(Product product, BigDecimal initialStock) {
        if (product == null) {
            throw new IllegalArgumentException("Vara måste finnas");
        }
        product.validateStockQuantity(initialStock);
        if (products.containsKey(product.id())) {
            throw new IllegalArgumentException("Varan finns redan i lagret");
        }

        products.put(product.id(), product);
        quantities.put(product.id(), initialStock);
    }

    public BigDecimal stock(String id) {
        requireProduct(id);
        return quantities.get(id);
    }

    public void restock(String id, BigDecimal quantity) {
        Product product = requireProduct(id);
        product.validatePositiveQuantity(quantity);
        quantities.put(id, quantities.get(id).add(quantity));
    }

    public void withdraw(String id, BigDecimal quantity) {
        Product product = requireProduct(id);
        product.validatePositiveQuantity(quantity);

        BigDecimal currentStock = quantities.get(id);
        if (quantity.compareTo(currentStock) > 0) {
            throw new IllegalStateException("Lagret räcker inte");
        }

        quantities.put(id, currentStock.subtract(quantity));
    }

    private Product requireProduct(String id) {
        Product product = products.get(id);
        if (product == null) {
            throw new IllegalArgumentException("Okänt varuid");
        }
        return product;
    }
}