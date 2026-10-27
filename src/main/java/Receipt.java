import java.util.List;

public final class Receipt {
    private final PriceResult priceResult;
    private final Money paid;
    private final Money change;

    public Receipt(PriceResult priceResult, Money paid) {
        if (priceResult == null || paid == null) {
            throw new IllegalArgumentException("Prisunderlag och betalning måste finnas");
        }
        this.priceResult = priceResult;
        this.paid = paid;
        this.change = paid.subtract(priceResult.total());
    }

    public List<PricedLine> lines() {
        return priceResult.lines();
    }

    public Money total() {
        return priceResult.total();
    }

    public Money paid() {
        return paid;
    }

    public Money change() {
        return change;
    }
}