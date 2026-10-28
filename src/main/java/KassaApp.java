import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class KassaApp {
    private final KSIO io;
    private final Checkout checkout;
    private final Inventory inventory;
    private final Map<String, Product> products;

    public KassaApp(KSIO io, Checkout checkout, Inventory inventory,
                    Map<String, Product> products) {
        if (io == null || checkout == null || inventory == null || products == null) {
            throw new IllegalArgumentException("Kassans delar måste finnas");
        }
        this.io = io;
        this.checkout = checkout;
        this.inventory = inventory;
        this.products = Map.copyOf(products);
    }

    public void run() {
        for (Product product : products.values()) {
            String unit = product.unit() == Unit.PIECE ? "st" : "kg";
            io.print(product.id() + ": " + product.name() + " - "
                    + kronor(product.unitPrice()) + " kr/" + unit);
        }
        io.print("""
                Kommandon:
                vara ID mängd | andra ID mängd | ta-bort ID mängd
                kassa | tillbaka | betala kronor | sluta
                Mängd anges i st eller kg. Punkt och komma fungerar för decimaler.
                """);

        while (true) {
            String input = io.read();
            if (input == null || input.strip().equalsIgnoreCase("sluta")) {
                return;
            }
            try {
                execute(input.strip().split("\\s+"));
            } catch (NumberFormatException | ArithmeticException e) {
                io.print("Fel: Ange ett giltigt tal. Betalning får ha högst två decimaler.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                io.print("Fel: " + e.getMessage());
            }
        }
    }

    private void execute(String[] parts) {
        switch (parts[0].toLowerCase(Locale.ROOT)) {
            case "vara" -> {
                requireLength(parts, 3);
                Product product = products.get(parts[1]);
                if (product == null) {
                    throw new IllegalArgumentException("Okänt varuid");
                }
                checkout.add(product, number(parts[2]));
                io.print("Korgen är uppdaterad.");
            }
            case "andra" -> {
                requireLength(parts, 3);
                checkout.changeQuantity(parts[1], number(parts[2]));
                io.print("Korgen är uppdaterad.");
            }
            case "ta-bort" -> {
                requireLength(parts, 3);
                checkout.remove(parts[1], number(parts[2]));
                io.print("Korgen är uppdaterad.");
            }
            case "kassa" -> {
                requireLength(parts, 1);
                PriceResult result = checkout.beginPayment();
                io.print("Att betala: " + kronor(result.total()) + " kr");
            }
            case "tillbaka" -> {
                requireLength(parts, 1);
                checkout.returnToShopping();
                io.print("Du kan ändra korgen igen.");
            }
            case "betala" -> {
                requireLength(parts, 2);
                Money paid = new Money(number(parts[1]).movePointRight(2).longValueExact());
                Receipt receipt = checkout.pay(paid, inventory);
                io.print(receipt.format());
                checkout.completeReceiptPrinting();
            }
            default -> throw new IllegalArgumentException("Okänt kommando");
        }
    }

    private void requireLength(String[] parts, int expected) {
        if (parts.length != expected) {
            throw new IllegalArgumentException("Fel antal delar i kommandot");
        }
    }

    private BigDecimal number(String text) {
        return new BigDecimal(text.replace(',', '.'));
    }

    private String kronor(Money money) {
        return BigDecimal.valueOf(money.ore(), 2).toPlainString().replace('.', ',');
    }

    public static void main(String[] args) {
        Map<String, Product> products = Map.of(
                "P", new Product("P", "Piggelin", Unit.PIECE, new Money(1000)),
                "D", new Product("D", "Daim", Unit.PIECE, new Money(1500)),
                "M", new Product("M", "Magnum", Unit.PIECE, new Money(2000)),
                "B", new Product("B", "Godis", Unit.KILOGRAM, new Money(4000)));
        Inventory inventory = new Inventory();
        for (Product product : products.values()) {
            BigDecimal stock = product.unit() == Unit.PIECE
                    ? new BigDecimal("10") : new BigDecimal("2.000");
            inventory.register(product, stock);
        }
        Checkout checkout = new Checkout(new PriceCalculator(),
                new PercentDiscount(Set.of("P", "D"), 25),
                new ThreeForTwoDiscount(Set.of("P", "D")));
        KSIO io = new RKSIO();
        io.print("Piggelin/Daim: 25 % eller 3 för 2, högst två grupper. Bästa priset väljs.");
        new KassaApp(io, checkout, inventory, products).run();
    }
}
