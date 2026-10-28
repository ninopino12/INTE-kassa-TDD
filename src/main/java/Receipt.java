import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

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

    public String format() {
        StringBuilder text = new StringBuilder("KVITTO\n");
        text.append(formatRow("Vara", "Mängd", "Ord. kr", "Rabatt kr", "Pris kr"));

        for (PricedLine line : lines()) {
            String name = line.product().name();
            if (name.length() > 20) {
                name = name.substring(0, 17) + "...";
            }
            String quantity = line.quantity().stripTrailingZeros()
                    .toPlainString().replace('.', ',');
            String unit = line.product().unit() == Unit.PIECE ? "st" : "kg";

            text.append(formatRow(name, quantity + " " + unit,
                    formatMoney(line.originalPrice()),
                    formatMoney(line.discount()),
                    formatMoney(line.finalPrice())));
        }

        text.append("Totalt: ").append(formatMoney(total())).append(" kr\n");
        text.append("Betalt: ").append(formatMoney(paid())).append(" kr\n");
        text.append("Växel: ").append(formatMoney(change())).append(" kr\n");
        return text.toString();
    }

    private String formatRow(String name, String quantity, String original,
                             String discount, String finalPrice) {
        return String.format(Locale.ROOT, "%-20s %10s %10s %10s %10s\n",
                name, quantity, original, discount, finalPrice);
    }

    private String formatMoney(Money amount) {
        return BigDecimal.valueOf(amount.ore(), 2).toPlainString().replace('.', ',');
    }
}