import java.util.List;

public final class PriceResult {
    private final List<PricedLine> lines;
    private final Money total;

    public PriceResult(List<PricedLine> lines) {
        if (lines == null) {
            throw new IllegalArgumentException("Prisrader måste finnas");
        }

        Money sum = new Money(0);
        for (PricedLine line : lines) {
            if (line == null) {
                throw new IllegalArgumentException("En prisrad saknas");
            }
            sum = sum.add(line.finalPrice());
        }

        this.lines = List.copyOf(lines);
        this.total = sum;
    }

    public List<PricedLine> lines() {
        return lines;
    }

    public Money total() {
        return total;
    }
}
