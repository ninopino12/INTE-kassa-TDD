import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
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

    public void withdrawAll(List<CartLine> lines) {
        if (lines == null) {
            throw new IllegalArgumentException("Korgrader måste finnas");
        }

        Map<String, BigDecimal> remainingStock = new HashMap<>(quantities);
        for (CartLine line : lines) {
            if (line == null) {
                throw new IllegalArgumentException("Korgrad måste finnas");
            }
            Product product = requireProduct(line.product().id());
            product.validatePositiveQuantity(line.quantity());

            BigDecimal currentStock = remainingStock.get(product.id());
            if (line.quantity().compareTo(currentStock) > 0) {
                throw new IllegalStateException("Lagret räcker inte");
            }
            remainingStock.put(product.id(), currentStock.subtract(line.quantity()));
        }

        quantities.putAll(remainingStock);
    }

    private Product requireProduct(String id) {
        Product product = products.get(id);
        if (product == null) {
            throw new IllegalArgumentException("Okänt varuid");
        }
        return product;
    }
}