import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Cart {
    private final Map<String, CartLine> lines = new LinkedHashMap<>();

    public void add(Product product, BigDecimal quantity) {
        CartLine line = new CartLine(product, quantity);
        CartLine current = lines.get(product.id());

        if (current != null) {
            BigDecimal total = current.quantity().add(quantity);
            line = new CartLine(current.product(), total);
        }

        lines.put(product.id(), line);
    }

    public List<CartLine> lines() {
        return new ArrayList<>(lines.values());
    }
}