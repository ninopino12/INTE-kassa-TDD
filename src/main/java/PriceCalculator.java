import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public final class PriceCalculator {
    public PriceResult calculate(List<CartLine> lines) {
        if (lines == null) {
            throw new IllegalArgumentException("Korgrader måste finnas");
        }

        List<PricedLine> pricedLines = new ArrayList<>();
        for (CartLine line : lines) {
            if (line == null) {
                throw new IllegalArgumentException("En korgrad saknas");
            }

            BigDecimal exactPrice = BigDecimal.valueOf(line.product().unitPrice().ore())
                    .multiply(line.quantity());
            long ore = exactPrice.setScale(0, RoundingMode.HALF_UP).longValueExact();
            Money originalPrice = new Money(ore);

            pricedLines.add(new PricedLine(line.product(), line.quantity(),
                    originalPrice, new Money(0)));
        }

        return new PriceResult(pricedLines);
    }
}
