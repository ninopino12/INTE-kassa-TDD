import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KassaTest {
    // Money
    @Test
    void moneyAddsZero() {
        Money amount = new Money(1000);
        Money zero = new Money(0);

        Money firstResult = amount.add(zero);
        Money secondResult = zero.add(amount);

        assertEquals(1000L, firstResult.ore());
        assertEquals(1000L, secondResult.ore());
    }

    @Test
    void moneyAdditionReachesLongMaximum() {
        Money amount = new Money(Long.MAX_VALUE - 1);
        Money oneOre = new Money(1);

        Money result = amount.add(oneOre);

        assertEquals(Long.MAX_VALUE, result.ore());
    }

    @Test
    void moneyAdditionRejectsOverflow() {
        Money amount = new Money(Long.MAX_VALUE);
        Money oneOre = new Money(1);

        assertThrows(ArithmeticException.class, () -> {
            amount.add(oneOre);
        });

        assertEquals(Long.MAX_VALUE, amount.ore());
        assertEquals(1L, oneOre.ore());
    }

    @Test
    void moneyAdditionRejectsNull() {
        Money amount = new Money(1000);

        assertThrows(IllegalArgumentException.class, () -> {
            amount.add(null);
        });

        assertEquals(1000L, amount.ore());
    }

    @Test
    void moneySubtractsEqualAmounts() {
        Money amount = new Money(1000);
        Money other = new Money(1000);

        Money result = amount.subtract(other);

        assertEquals(0L, result.ore());
    }

    @Test
    void moneySubtractsZero() {
        Money amount = new Money(1000);
        Money zero = new Money(0);

        Money result = amount.subtract(zero);

        assertEquals(1000L, result.ore());
    }

    @Test
    void moneySubtractionRejectsNegativeResult() {
        Money amount = new Money(1000);
        Money other = new Money(1100);

        assertThrows(IllegalArgumentException.class, () -> {
            amount.subtract(other);
        });

        assertEquals(1000L, amount.ore());
        assertEquals(1100L, other.ore());
    }

    @Test
    void moneySubtractionRejectsNull() {
        Money amount = new Money(1000);

        assertThrows(IllegalArgumentException.class, () -> {
            amount.subtract(null);
        });

        assertEquals(1000L, amount.ore());
    }

    @Test
    void moneyComparisonHandlesExtremeValues() {
        Money zero = new Money(0);
        Money largest = new Money(Long.MAX_VALUE);

        assertTrue(zero.compareTo(largest) < 0);
        assertTrue(largest.compareTo(zero) > 0);
    }

    @Test
    void moneyComparisonRejectsNull() {
        Money amount = new Money(1000);

        assertThrows(IllegalArgumentException.class, () -> {
            amount.compareTo(null);
        });

        assertEquals(1000L, amount.ore());
    }

    @Test
    void moneyComparisonDetectsSmallerAmount() {
        Money amount = new Money(1000);
        Money other = new Money(1500);

        int result = amount.compareTo(other);

        assertTrue(result < 0);
        assertEquals(1000L, amount.ore());
        assertEquals(1500L, other.ore());
    }

    @Test
    void moneyComparisonDetectsEqualAmount() {
        Money amount = new Money(1000);
        Money other = new Money(1000);

        int result = amount.compareTo(other);

        assertEquals(0, result);
    }

    @Test
    void moneyComparisonDetectsGreaterAmount() {
        Money amount = new Money(1500);
        Money other = new Money(1000);

        int result = amount.compareTo(other);

        assertTrue(result > 0);
    }

    @Test
    void moneyAddsAmounts() {
        Money amount = new Money(1000);
        Money other = new Money(500);

        Money result = amount.add(other);

        assertEquals(1500L, result.ore());
        assertEquals(1000L, amount.ore());
        assertEquals(500L, other.ore());
    }

    @Test
    void moneySubtractsAmounts() {
        Money amount = new Money(1000);
        Money other = new Money(400);

        Money result = amount.subtract(other);

        assertEquals(600L, result.ore());
        assertEquals(1000L, amount.ore());
        assertEquals(400L, other.ore());
    }

    @Test
    void zeroOreCanBeCreatedAndRead() {
        Money money = new Money(0);

        assertEquals(0L, money.ore());
    }

    @Test
    void oneOreCanBeCreatedAndRead() {
        Money money = new Money(1);

        assertEquals(1L, money.ore());
    }

    @Test
    void oneThousandOreCanBeCreatedAndRead() {
        Money money = new Money(1000);

        assertEquals(1000L, money.ore());
    }

    @Test
    void negativeOreIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Money(-1);
        });
    }

    // Product
    private Product bread() {
        return new Product("A", "Bröd", Unit.PIECE, new Money(1000));
    }

    private Product candy() {
        return new Product("B", "Godis", Unit.KILOGRAM, new Money(4000));
    }

    @Test
    void pieceProductKeepsSuppliedValues() {
        Product product = bread();

        assertEquals("A", product.id());
        assertEquals("Bröd", product.name());
        assertEquals(Unit.PIECE, product.unit());
        assertEquals(1000L, product.unitPrice().ore());
    }

    @Test
    void kilogramProductKeepsSuppliedValues() {
        Product product = candy();

        assertEquals("B", product.id());
        assertEquals("Godis", product.name());
        assertEquals(Unit.KILOGRAM, product.unit());
        assertEquals(4000L, product.unitPrice().ore());
    }

    @Test
    void idKeepsItsCase() {
        Product product = new Product("a", "Bröd", Unit.PIECE, new Money(1000));

        assertEquals("a", product.id());
    }

    @Test
    void nullIdIsRejected() {
        Money price = new Money(1000);

        assertThrows(IllegalArgumentException.class, () -> {
            new Product(null, "Bröd", Unit.PIECE, price);
        });
    }

    @Test
    void blankIdIsRejected() {
        Money price = new Money(1000);

        assertThrows(IllegalArgumentException.class, () -> {
            new Product("  ", "Bröd", Unit.PIECE, price);
        });
    }

    @Test
    void nullNameIsRejected() {
        Money price = new Money(1000);

        assertThrows(IllegalArgumentException.class, () -> {
            new Product("A", null, Unit.PIECE, price);
        });
    }

    @Test
    void blankNameIsRejected() {
        Money price = new Money(1000);

        assertThrows(IllegalArgumentException.class, () -> {
            new Product("A", "", Unit.PIECE, price);
        });
    }

    @Test
    void longNameIsPreserved() {
        String name = "Ett långt produktnamn som ska få finnas kvar";
        Product product = new Product("A", name, Unit.PIECE, new Money(1000));

        assertEquals(name, product.name());
    }

    @Test
    void nullUnitIsRejected() {
        Money price = new Money(1000);

        assertThrows(IllegalArgumentException.class, () -> {
            new Product("A", "Bröd", null, price);
        });
    }

    @Test
    void nullPriceIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product("A", "Bröd", Unit.PIECE, null);
        });
    }

    @Test
    void zeroPriceIsRejected() {
        Money price = new Money(0);

        assertThrows(IllegalArgumentException.class, () -> {
            new Product("A", "Bröd", Unit.PIECE, price);
        });
    }

    @Test
    void pieceQuantityAllowsTrailingZeros() {
        Product product = bread();
        BigDecimal quantity = new BigDecimal("2.0");

        product.validatePositiveQuantity(quantity);
    }

    @Test
    void fractionalPieceIsRejected() {
        Product product = bread();
        BigDecimal quantity = new BigDecimal("1.5");

        assertThrows(IllegalArgumentException.class, () -> {
            product.validatePositiveQuantity(quantity);
        });
    }

    @Test
    void kilogramQuantityAllowsTrailingZeros() {
        Product product = candy();
        BigDecimal quantity = new BigDecimal("0.1250");

        product.validatePositiveQuantity(quantity);
    }

    @Test
    void oneGramIsAllowed() {
        Product product = candy();
        BigDecimal quantity = new BigDecimal("0.001");

        product.validatePositiveQuantity(quantity);
    }

    @Test
    void fractionOfAGramIsRejected() {
        Product product = candy();
        BigDecimal quantity = new BigDecimal("0.0005");

        assertThrows(IllegalArgumentException.class, () -> {
            product.validatePositiveQuantity(quantity);
        });
    }

    @Test
    void zeroActionQuantityIsRejected() {
        Product pieceProduct = bread();
        Product kilogramProduct = candy();
        BigDecimal quantity = BigDecimal.ZERO;

        assertThrows(IllegalArgumentException.class, () -> {
            pieceProduct.validatePositiveQuantity(quantity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            kilogramProduct.validatePositiveQuantity(quantity);
        });
    }

    @Test
    void negativeActionQuantityIsRejected() {
        Product pieceProduct = bread();
        Product kilogramProduct = candy();
        BigDecimal pieceQuantity = new BigDecimal("-1");
        BigDecimal kilogramQuantity = new BigDecimal("-0.001");

        assertThrows(IllegalArgumentException.class, () -> {
            pieceProduct.validatePositiveQuantity(pieceQuantity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            kilogramProduct.validatePositiveQuantity(kilogramQuantity);
        });
    }

    @Test
    void zeroStockQuantityIsAllowed() {
        Product pieceProduct = bread();
        Product kilogramProduct = candy();
        BigDecimal pieceQuantity = BigDecimal.ZERO;
        BigDecimal kilogramQuantity = new BigDecimal("0.0000");

        pieceProduct.validateStockQuantity(pieceQuantity);
        kilogramProduct.validateStockQuantity(kilogramQuantity);
    }

    @Test
    void negativeStockQuantityIsRejected() {
        Product pieceProduct = bread();
        Product kilogramProduct = candy();
        BigDecimal pieceQuantity = new BigDecimal("-1");
        BigDecimal kilogramQuantity = new BigDecimal("-0.001");

        assertThrows(IllegalArgumentException.class, () -> {
            pieceProduct.validateStockQuantity(pieceQuantity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            kilogramProduct.validateStockQuantity(kilogramQuantity);
        });
    }

    @Test
    void fractionalStockQuantityIsRejected() {
        Product pieceProduct = bread();
        Product kilogramProduct = candy();
        BigDecimal pieceQuantity = new BigDecimal("1.5");
        BigDecimal kilogramQuantity = new BigDecimal("0.0005");

        assertThrows(IllegalArgumentException.class, () -> {
            pieceProduct.validateStockQuantity(pieceQuantity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            kilogramProduct.validateStockQuantity(kilogramQuantity);
        });
    }

    @Test
    void nullQuantityIsRejected() {
        Product pieceProduct = bread();
        Product kilogramProduct = candy();

        assertThrows(IllegalArgumentException.class, () -> {
            pieceProduct.validatePositiveQuantity(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            kilogramProduct.validateStockQuantity(null);
        });
    }

    // Cart
    @Test
    void cartMergesSameProductId() {
        Cart cart = new Cart();

        cart.add(bread(), new BigDecimal("2"));
        cart.add(bread(), new BigDecimal("1"));

        assertEquals(1, cart.lines().size());

        CartLine line = cart.lines().get(0);
        assertEquals("A", line.product().id());
        assertEquals(new BigDecimal("3"), line.quantity());
    }

    @Test
    void cartStartsEmpty() {
        Cart cart = new Cart();

        assertTrue(cart.lines().isEmpty());
    }

    @Test
    void cartKeepsOtherProductWhenMerging() {
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("2"));
        cart.add(candy(), new BigDecimal("0.125"));

        cart.add(bread(), new BigDecimal("1"));

        assertEquals(2, cart.lines().size());

        CartLine breadLine = cart.lines().get(0);
        CartLine candyLine = cart.lines().get(1);

        assertEquals("A", breadLine.product().id());
        assertEquals(new BigDecimal("3"), breadLine.quantity());

        assertEquals("B", candyLine.product().id());
        assertEquals(new BigDecimal("0.125"), candyLine.quantity());
    }

    @Test
    void cartAddsWeightsExactly() {
        Cart cart = new Cart();
        cart.add(candy(), new BigDecimal("0.1000"));

        cart.add(candy(), new BigDecimal("0.025"));

        assertEquals(1, cart.lines().size());

        BigDecimal total = cart.lines().get(0).quantity();
        assertEquals(0, new BigDecimal("0.125").compareTo(total));
    }

    @Test
    void cartRejectsInvalidPieceQuantitiesWithoutChanges() {
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("2"));
        cart.add(candy(), new BigDecimal("0.125"));

        BigDecimal[] invalidQuantities = {
                BigDecimal.ZERO,
                new BigDecimal("-1"),
                new BigDecimal("1.5"),
                null
        };

        for (BigDecimal quantity : invalidQuantities) {
            assertThrows(IllegalArgumentException.class, () -> {
                cart.add(bread(), quantity);
            });

            assertEquals(2, cart.lines().size());

            assertEquals("A", cart.lines().get(0).product().id());
            assertEquals(new BigDecimal("2"), cart.lines().get(0).quantity());

            assertEquals("B", cart.lines().get(1).product().id());
            assertEquals(new BigDecimal("0.125"), cart.lines().get(1).quantity());
        }
    }

    @Test
    void cartRejectsFractionOfGramWithoutAddingLine() {
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("2"));

        assertThrows(IllegalArgumentException.class, () -> {
            cart.add(candy(), new BigDecimal("0.0005"));
        });

        assertEquals(1, cart.lines().size());
        assertEquals("A", cart.lines().get(0).product().id());
        assertEquals(new BigDecimal("2"), cart.lines().get(0).quantity());
    }

    @Test
    void cartRejectsNullProductWithoutChanges() {
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("2"));

        assertThrows(IllegalArgumentException.class, () -> {
            cart.add(null, BigDecimal.ONE);
        });

        assertEquals(1, cart.lines().size());
        assertEquals("A", cart.lines().get(0).product().id());
        assertEquals(new BigDecimal("2"), cart.lines().get(0).quantity());
    }

    // Checkout
    @Test
    void checkoutStartsShoppingAfterFirstProduct() {
        Checkout checkout = new Checkout();

        checkout.add(bread(), new BigDecimal("2"));

        assertEquals(CheckoutState.SHOPPING, checkout.state());
        assertEquals(1, checkout.lines().size());
        assertEquals("A", checkout.lines().get(0).product().id());
        assertEquals(new BigDecimal("2"), checkout.lines().get(0).quantity());
    }
        @Test
    void checkoutStartsEmpty() {
        Checkout checkout = new Checkout();

        assertEquals(CheckoutState.EMPTY, checkout.state());
        assertTrue(checkout.lines().isEmpty());
    }

    @Test
    void checkoutAddsProductsWhileShopping() {
        Checkout checkout = new Checkout();
        checkout.add(bread(), new BigDecimal("2"));

        checkout.add(candy(), new BigDecimal("0.125"));
        checkout.add(bread(), new BigDecimal("1"));

        assertEquals(CheckoutState.SHOPPING, checkout.state());
        assertEquals(2, checkout.lines().size());

        CartLine breadLine = checkout.lines().get(0);
        CartLine candyLine = checkout.lines().get(1);

        assertEquals("A", breadLine.product().id());
        assertEquals(new BigDecimal("3"), breadLine.quantity());

        assertEquals("B", candyLine.product().id());
        assertEquals(new BigDecimal("0.125"), candyLine.quantity());
    }

    @Test
    void checkoutRejectsInvalidFirstProductWithoutChangingState() {
        Checkout checkout = new Checkout();

        assertThrows(IllegalArgumentException.class, () -> {
            checkout.add(bread(), new BigDecimal("1.5"));
        });

        assertEquals(CheckoutState.EMPTY, checkout.state());
        assertTrue(checkout.lines().isEmpty());
    }

    @Test
    void checkoutRejectsInvalidAdditionWithoutChangingPurchase() {
        Checkout checkout = new Checkout();
        checkout.add(bread(), new BigDecimal("2"));
        checkout.add(candy(), new BigDecimal("0.125"));

        assertThrows(IllegalArgumentException.class, () -> {
            checkout.add(bread(), new BigDecimal("-1"));
        });

        assertEquals(CheckoutState.SHOPPING, checkout.state());
        assertEquals(2, checkout.lines().size());

        assertEquals("A", checkout.lines().get(0).product().id());
        assertEquals(new BigDecimal("2"), checkout.lines().get(0).quantity());

        assertEquals("B", checkout.lines().get(1).product().id());
        assertEquals(new BigDecimal("0.125"), checkout.lines().get(1).quantity());
    }

    // Inventory
    @Test
    void inventoryRestockAddsToExistingStock() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        inventory.register(candy(), new BigDecimal("0.500"));

        inventory.restock("A", new BigDecimal("2"));

        assertEquals(new BigDecimal("7"), inventory.stock("A"));
        assertEquals(new BigDecimal("0.500"), inventory.stock("B"));
    }

    @Test
    void inventoryRestocksKilogramsExactly() {
        Inventory inventory = new Inventory();
        inventory.register(candy(), new BigDecimal("0.5000"));

        inventory.restock("B", new BigDecimal("0.125"));

        assertEquals(0, new BigDecimal("0.625").compareTo(inventory.stock("B")));
    }

    @Test
    void inventoryAllowsZeroInitialStockAndLaterRestocking() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), BigDecimal.ZERO);
        inventory.register(candy(), new BigDecimal("0.0000"));

        assertEquals(0, inventory.stock("A").signum());
        assertEquals(0, inventory.stock("B").signum());

        inventory.restock("A", new BigDecimal("2"));
        inventory.restock("B", new BigDecimal("0.125"));

        assertEquals(new BigDecimal("2"), inventory.stock("A"));
        assertEquals(0, new BigDecimal("0.125").compareTo(inventory.stock("B")));
    }

    @Test
    void inventoryRejectsDuplicateRegistrationWithoutChangingStock() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        inventory.register(candy(), new BigDecimal("0.500"));

        assertThrows(IllegalArgumentException.class, () -> {
            inventory.register(bread(), new BigDecimal("2"));
        });

        assertEquals(new BigDecimal("5"), inventory.stock("A"));
        assertEquals(new BigDecimal("0.500"), inventory.stock("B"));
    }

    @Test
    void inventoryRejectsInvalidInitialPieceStockWithoutCreatingEntry() {
        BigDecimal[] invalidQuantities = {
                new BigDecimal("-1"),
                new BigDecimal("1.5"),
                null
        };

        for (BigDecimal quantity : invalidQuantities) {
            Inventory inventory = new Inventory();

            assertThrows(IllegalArgumentException.class, () -> {
                inventory.register(bread(), quantity);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                inventory.stock("A");
            });

            inventory.register(bread(), new BigDecimal("5"));
            assertEquals(new BigDecimal("5"), inventory.stock("A"));
        }
    }

    @Test
    void inventoryRejectsInvalidInitialWeightWithoutCreatingEntry() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));

        assertThrows(IllegalArgumentException.class, () -> {
            inventory.register(candy(), new BigDecimal("0.0005"));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            inventory.stock("B");
        });

        assertEquals(new BigDecimal("5"), inventory.stock("A"));

        inventory.register(candy(), new BigDecimal("0.500"));
        assertEquals(new BigDecimal("0.500"), inventory.stock("B"));
    }

    @Test
    void inventoryRejectsInvalidPieceRestockingWithoutChanges() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        inventory.register(candy(), new BigDecimal("0.500"));

        BigDecimal[] invalidQuantities = {
                BigDecimal.ZERO,
                new BigDecimal("-1"),
                new BigDecimal("1.5"),
                null
        };

        for (BigDecimal quantity : invalidQuantities) {
            assertThrows(IllegalArgumentException.class, () -> {
                inventory.restock("A", quantity);
            });

            assertEquals(new BigDecimal("5"), inventory.stock("A"));
            assertEquals(new BigDecimal("0.500"), inventory.stock("B"));
        }
    }

    @Test
    void inventoryRejectsFractionOfGramWhenRestocking() {
        Inventory inventory = new Inventory();
        inventory.register(candy(), new BigDecimal("0.500"));

        assertThrows(IllegalArgumentException.class, () -> {
            inventory.restock("B", new BigDecimal("0.0005"));
        });

        assertEquals(new BigDecimal("0.500"), inventory.stock("B"));
    }

    @Test
    void inventoryRejectsUnknownIdWithoutChangingKnownStock() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));

        assertThrows(IllegalArgumentException.class, () -> {
            inventory.stock("missing");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            inventory.restock("missing", BigDecimal.ONE);
        });

        assertEquals(new BigDecimal("5"), inventory.stock("A"));
    }

    @Test
    void inventoryRejectsNullProductWithoutChangingStock() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));

        assertThrows(IllegalArgumentException.class, () -> {
            inventory.register(null, BigDecimal.ONE);
        });

        assertEquals(new BigDecimal("5"), inventory.stock("A"));
    }

    // Prices
    @Test
    void priceCalculatorCalculatesPieceAndWeightPrices() {
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("2"));
        cart.add(candy(), new BigDecimal("0.125"));
        PriceCalculator calculator = new PriceCalculator();

        PriceResult result = calculator.calculate(cart.lines());

        assertEquals(2500L, result.total().ore());
        assertEquals(2, result.lines().size());

        PricedLine breadLine = result.lines().get(0);
        assertEquals("A", breadLine.product().id());
        assertEquals(new BigDecimal("2"), breadLine.quantity());
        assertEquals(2000L, breadLine.originalPrice().ore());
        assertEquals(0L, breadLine.discount().ore());
        assertEquals(2000L, breadLine.finalPrice().ore());

        PricedLine candyLine = result.lines().get(1);
        assertEquals("B", candyLine.product().id());
        assertEquals(new BigDecimal("0.125"), candyLine.quantity());
        assertEquals(500L, candyLine.originalPrice().ore());
        assertEquals(0L, candyLine.discount().ore());
        assertEquals(500L, candyLine.finalPrice().ore());
    }

    @Test
    void priceCalculatorHandlesEmptyCart() {
        Cart cart = new Cart();
        PriceCalculator calculator = new PriceCalculator();

        PriceResult result = calculator.calculate(cart.lines());

        assertEquals(0L, result.total().ore());
        assertTrue(result.lines().isEmpty());
    }

    @Test
    void priceCalculatorKeepsPreviousResultWhenCartChanges() {
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("2"));
        PriceCalculator calculator = new PriceCalculator();
        PriceResult previous = calculator.calculate(cart.lines());

        cart.add(bread(), BigDecimal.ONE);
        PriceResult current = calculator.calculate(cart.lines());

        assertEquals(2000L, previous.total().ore());
        assertEquals(new BigDecimal("2"), previous.lines().get(0).quantity());
        assertEquals(2000L, previous.lines().get(0).finalPrice().ore());

        assertEquals(3000L, current.total().ore());
        assertEquals(new BigDecimal("3"), current.lines().get(0).quantity());
    }

    @Test
    void priceResultProtectsItsLines() {
        Cart cart = new Cart();
        cart.add(bread(), BigDecimal.ONE);
        PriceCalculator calculator = new PriceCalculator();
        PriceResult result = calculator.calculate(cart.lines());

        assertThrows(UnsupportedOperationException.class, () -> {
            result.lines().clear();
        });

        assertEquals(1, result.lines().size());
        assertEquals(1000L, result.total().ore());
    }

    @Test
    void priceResultCopiesSuppliedLines() {
        PricedLine line = new PricedLine(bread(), BigDecimal.ONE,
                new Money(1000), new Money(0));
        List<PricedLine> lines = new ArrayList<>();
        lines.add(line);
        PriceResult result = new PriceResult(lines);

        lines.clear();

        assertEquals(1, result.lines().size());
        assertEquals("A", result.lines().get(0).product().id());
        assertEquals(1000L, result.total().ore());
    }

    @Test
    void priceResultUsesFinalPricesForTotal() {
        PricedLine breadLine = new PricedLine(bread(), new BigDecimal("3"),
                new Money(3000), new Money(600));
        PricedLine candyLine = new PricedLine(candy(), new BigDecimal("0.125"),
                new Money(500), new Money(0));

        PriceResult result = new PriceResult(List.of(breadLine, candyLine));

        assertEquals(2400L, breadLine.finalPrice().ore());
        assertEquals(600L, breadLine.discount().ore());
        assertEquals(2900L, result.total().ore());
    }

    @Test
    void priceCalculatorRejectsMissingInput() {
        PriceCalculator calculator = new PriceCalculator();

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculate(null);
        });

        List<CartLine> lines = new ArrayList<>();
        lines.add(null);

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculate(lines);
        });
    }

    @Test
    void pricedLineRejectsDiscountLargerThanPrice() {
        Product product = bread();
        Money originalPrice = new Money(1000);
        Money discount = new Money(1001);

        assertThrows(IllegalArgumentException.class, () -> {
            new PricedLine(product, BigDecimal.ONE, originalPrice, discount);
        });
    }
}