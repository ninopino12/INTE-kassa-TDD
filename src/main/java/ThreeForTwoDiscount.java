import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ThreeForTwoDiscount {
    private static final int MAX_GROUPS = 2;
    private final Set<String> productIds;

    public ThreeForTwoDiscount(Set<String> productIds) {
        if (productIds == null) {
            throw new IllegalArgumentException("Varuid för erbjudandet måste finnas");
        }
        for (String id : productIds) {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("Varuid får inte vara tomt");
            }
        }
        this.productIds = Set.copyOf(productIds);
    }

    List<Money> savingsFor(PriceResult original) {
        List<PricedLine> lines = original.lines();
        List<Money> savings = new ArrayList<>();
        List<Integer> eligible = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            savings.add(new Money(0));
            Product product = lines.get(i).product();
            if (product.unit() == Unit.PIECE && productIds.contains(product.id())) {
                eligible.add(i);
            }
        }

        eligible.sort((first, second) -> Long.compare(
                lines.get(second).product().unitPrice().ore(),
                lines.get(first).product().unitPrice().ore()));

        int position = 0;
        for (int index : eligible) {
            PricedLine line = lines.get(index);
            int count = line.quantity()
                    .min(BigDecimal.valueOf(MAX_GROUPS * 3 - position))
                    .intValueExact();

            for (int i = 0; i < count; i++) {
                position++;
                if (position % 3 == 0) {
                    savings.set(index, savings.get(index).add(line.product().unitPrice()));
                }
            }
            if (position == MAX_GROUPS * 3) {
                break;
            }
        }

        return List.copyOf(savings);
    }
}
