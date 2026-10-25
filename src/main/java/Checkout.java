import java.math.BigDecimal;
import java.util.List;

public final class Checkout {
    private final Cart cart = new Cart();
    private CheckoutState state = CheckoutState.EMPTY;

    public CheckoutState state() {
        return state;
    }

    public void add(Product product, BigDecimal quantity) {
        cart.add(product, quantity);
        state = CheckoutState.SHOPPING;
    }

    public List<CartLine> lines() {
        return cart.lines();
    }
}