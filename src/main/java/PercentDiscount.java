import java.util.Set;

public final class PercentDiscount {
    private final Set<String> productIds;
    private final int percent;

    public PercentDiscount(Set<String> productIds, int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("Rabatten måste vara mellan 0 och 100 procent");
        }
        if (productIds == null) {
            throw new IllegalArgumentException("Varuid för erbjudandet måste finnas");
        }
        for (String id : productIds) {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("Varuid får inte vara tomt");
            }
        }

        this.productIds = Set.copyOf(productIds);
        this.percent = percent;
    }

    public boolean appliesTo(String productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Varuid måste finnas");
        }
        return productIds.contains(productId);
    }

    public int percent() {
        return percent;
    }
}
