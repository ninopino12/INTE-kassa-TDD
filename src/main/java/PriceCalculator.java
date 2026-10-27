import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class PriceCalculator {
    public PriceResult calculate(List<CartLine> lines) {
        return calculate(lines, new PercentDiscount(Set.of(), 0));
    }

    public PriceResult calculate(List<CartLine> lines, PercentDiscount percentDiscount,
                                 ThreeForTwoDiscount threeForTwoDiscount) {
        PriceResult percentPrice = calculate(lines, percentDiscount);
        PriceResult threeForTwoPrice = calculate(lines, threeForTwoDiscount);

        if (percentPrice.total().compareTo(threeForTwoPrice.total()) <= 0) {
            return percentPrice;
        }
        return threeForTwoPrice;
    }

    public PriceResult calculate(List<CartLine> lines, ThreeForTwoDiscount discount) {
        if (discount == null) {
            throw new IllegalArgumentException("Rabattinställningen måste finnas");
        }

        PriceResult original = calculate(lines);
        List<Money> savings = discount.savingsFor(original);
        List<PricedLine> pricedLines = new ArrayList<>();
        for (int i = 0; i < original.lines().size(); i++) {
            PricedLine line = original.lines().get(i);
            pricedLines.add(new PricedLine(line.product(), line.quantity(),
                    line.originalPrice(), savings.get(i)));
        }

        return new PriceResult(pricedLines);
    }

    public PriceResult calculate(List<CartLine> lines, PercentDiscount discount) {
        if (lines == null) {
            throw new IllegalArgumentException("Korgrader måste finnas");
        }
        if (discount == null) {
            throw new IllegalArgumentException("Rabattinställningen måste finnas");
        }

        List<PricedLine> pricedLines = new ArrayList<>();
        for (CartLine line : lines) {
            if (line == null) {
                throw new IllegalArgumentException("En korgrad saknas");
            }

            BigDecimal exactPrice = BigDecimal.valueOf(line.product().unitPrice().ore())
                    .multiply(line.quantity());
            BigDecimal exactFinalPrice = exactPrice;

            if (discount.appliesTo(line.product().id())) {
                exactFinalPrice = exactPrice
                        .multiply(BigDecimal.valueOf(100 - discount.percent()))
                        .movePointLeft(2);
            }

            Money originalPrice = new Money(exactPrice
                    .setScale(0, RoundingMode.HALF_UP).longValueExact());
            Money finalPrice = new Money(exactFinalPrice
                    .setScale(0, RoundingMode.HALF_UP).longValueExact());
            Money saving = originalPrice.subtract(finalPrice);

            pricedLines.add(new PricedLine(line.product(), line.quantity(),
                    originalPrice, saving));
        }

        return new PriceResult(pricedLines);
    }
}
