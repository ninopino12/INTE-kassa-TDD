import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public final class Checkout {
    private final Cart cart = new Cart();
    private final PriceCalculator priceCalculator;
    private final PercentDiscount discount;
    private CheckoutState state = CheckoutState.EMPTY;
    private PriceResult priceResult;

    public Checkout() {
        this(new PriceCalculator(), new PercentDiscount(Set.of(), 0));
    }

    public Checkout(PriceCalculator priceCalculator, PercentDiscount discount) {
        if (priceCalculator == null || discount == null) {
            throw new IllegalArgumentException("Prisberäknare och rabattinställning måste finnas");
        }
        this.priceCalculator = priceCalculator;
        this.discount = discount;
    }

    public CheckoutState state() {
        return state;
    }

    public void add(Product product, BigDecimal quantity) {
        requireEditableState();
        cart.add(product, quantity);
        state = CheckoutState.SHOPPING;
    }

    public void changeQuantity(String id, BigDecimal quantity) {
        requireEditableState();
        cart.changeQuantity(id, quantity);
        state = CheckoutState.SHOPPING;
    }

    public void remove(String id, BigDecimal quantity) {
        requireEditableState();
        cart.remove(id, quantity);
        state = cart.lines().isEmpty() ? CheckoutState.EMPTY : CheckoutState.SHOPPING;
    }

    public PriceResult beginPayment() {
        if (state != CheckoutState.SHOPPING) {
            throw new IllegalStateException("Betalning kan bara börja från varuregistrering");
        }

        priceResult = priceCalculator.calculate(cart.lines(), discount);
        state = CheckoutState.PAYMENT;
        return priceResult;
    }

    public void returnToShopping() {
        if (state != CheckoutState.PAYMENT) {
            throw new IllegalStateException("Betalning pågår inte");
        }

        priceResult = null;
        state = CheckoutState.SHOPPING;
    }

    public Receipt pay(Money paid, Inventory inventory) {
        if (state != CheckoutState.PAYMENT) {
            throw new IllegalStateException("Betalning pågår inte");
        }
        if (paid == null || inventory == null) {
            throw new IllegalArgumentException("Betalning och lager måste finnas");
        }
        if (paid.compareTo(priceResult.total()) < 0) {
            throw new IllegalStateException("Betalningen räcker inte");
        }

        Receipt receipt = new Receipt(priceResult, paid);
        inventory.withdrawAll(cart.lines());
        state = CheckoutState.PRINTING_RECEIPT;
        return receipt;
    }

    public void completeReceiptPrinting() {
        if (state != CheckoutState.PRINTING_RECEIPT) {
            throw new IllegalStateException("Kvittoutskrift pågår inte");
        }

        cart.clear();
        priceResult = null;
        state = CheckoutState.EMPTY;
    }

    public List<CartLine> lines() {
        return cart.lines();
    }

    private void requireEditableState() {
        if (state != CheckoutState.EMPTY && state != CheckoutState.SHOPPING) {
            throw new IllegalStateException("Korgen kan inte ändras under betalning eller kvittoutskrift");
        }
    }
}