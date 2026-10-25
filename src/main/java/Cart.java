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

    public void changeQuantity(String id, BigDecimal quantity) {
        CartLine current = requireLine(id);
        CartLine replacement = new CartLine(current.product(), quantity);
        lines.put(id, replacement);
    }

    public void remove(String id, BigDecimal quantity) {
        CartLine current = requireLine(id);
        current.product().validatePositiveQuantity(quantity);

        if (quantity.compareTo(current.quantity()) > 0) {
            throw new IllegalArgumentException("Kan inte ta bort mer än det finns i korgen");
        }

        BigDecimal remaining = current.quantity().subtract(quantity);
        if (remaining.signum() == 0) {
            lines.remove(id);
        } else {
            lines.put(id, new CartLine(current.product(), remaining));
        }
    }

    public void clear() {
        lines.clear();
    }

    public List<CartLine> lines() {
        return new ArrayList<>(lines.values());
    }

    private CartLine requireLine(String id) {
        CartLine line = lines.get(id);
        if (line == null) {
            throw new IllegalArgumentException("Varan finns inte i korgen");
        }
        return line;
    }
}