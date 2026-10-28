import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.containsInAnyOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;

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

    // Checkout payment phase
    @Test
    void checkoutBeginsPaymentWithoutEmptyingCart() {
        Checkout checkout = new Checkout();
        checkout.add(bread(), new BigDecimal("2"));

        PriceResult result = checkout.beginPayment();

        assertEquals(CheckoutState.PAYMENT, checkout.state());
        assertEquals(2000L, result.total().ore());
        assertEquals(1, checkout.lines().size());
        assertEquals(new BigDecimal("2"), checkout.lines().get(0).quantity());
    }

    @Test
    void checkoutRejectsChangesAndRepeatedPaymentStartWhilePaying() {
        Checkout checkout = new Checkout();
        checkout.add(bread(), new BigDecimal("3"));
        checkout.add(candy(), new BigDecimal("0.125"));
        PriceResult frozen = checkout.beginPayment();

        assertThrows(IllegalStateException.class, () -> {
            checkout.add(bread(), BigDecimal.ONE);
        });
        assertThrows(IllegalStateException.class, () -> {
            checkout.changeQuantity("A", BigDecimal.ONE);
        });
        assertThrows(IllegalStateException.class, () -> {
            checkout.remove("A", new BigDecimal("3"));
        });
        assertThrows(IllegalStateException.class, () -> {
            checkout.beginPayment();
        });

        assertEquals(CheckoutState.PAYMENT, checkout.state());
        assertEquals(2, checkout.lines().size());
        assertEquals(new BigDecimal("3"), checkout.lines().get(0).quantity());
        assertEquals(new BigDecimal("0.125"), checkout.lines().get(1).quantity());
        assertEquals(3500L, frozen.total().ore());
    }

    @Test
    void checkoutRecalculatesAfterReturningAndEditingPurchase() {
        Checkout checkout = new Checkout();
        checkout.add(bread(), new BigDecimal("3"));
        checkout.add(candy(), new BigDecimal("0.125"));
        PriceResult previous = checkout.beginPayment();

        checkout.returnToShopping();
        assertEquals(CheckoutState.SHOPPING, checkout.state());

        checkout.changeQuantity("A", new BigDecimal("4"));
        checkout.remove("A", new BigDecimal("2"));
        assertEquals(CheckoutState.SHOPPING, checkout.state());

        PriceResult current = checkout.beginPayment();

        assertEquals(CheckoutState.PAYMENT, checkout.state());
        assertEquals(2500L, current.total().ore());
        assertEquals(new BigDecimal("2"), current.lines().get(0).quantity());
        assertEquals(3500L, previous.total().ore());
        assertEquals(new BigDecimal("3"), previous.lines().get(0).quantity());
    }

    @Test
    void checkoutRejectsPaymentTransitionsFromWrongStates() {
        Checkout checkout = new Checkout();

        assertThrows(IllegalStateException.class, () -> {
            checkout.beginPayment();
        });
        assertThrows(IllegalStateException.class, () -> {
            checkout.returnToShopping();
        });
        assertEquals(CheckoutState.EMPTY, checkout.state());
        assertTrue(checkout.lines().isEmpty());

        checkout.add(bread(), BigDecimal.ONE);

        assertThrows(IllegalStateException.class, () -> {
            checkout.returnToShopping();
        });
        assertEquals(CheckoutState.SHOPPING, checkout.state());
        assertEquals(BigDecimal.ONE, checkout.lines().get(0).quantity());

        checkout.remove("A", BigDecimal.ONE);
        assertEquals(CheckoutState.EMPTY, checkout.state());
        assertTrue(checkout.lines().isEmpty());

        assertThrows(IllegalStateException.class, () -> {
            checkout.beginPayment();
        });
        assertEquals(CheckoutState.EMPTY, checkout.state());
    }

    @Test
    void checkoutPaymentUsesConfiguredPercentageOffer() {
        PercentDiscount discount = new PercentDiscount(Set.of("P", "D"), 20);
        Checkout checkout = new Checkout(new PriceCalculator(), discount);
        checkout.add(piggelin(), new BigDecimal("2"));
        checkout.add(daim(), BigDecimal.ONE);
        checkout.add(magnum(), BigDecimal.ONE);

        PriceResult result = checkout.beginPayment();

        assertEquals(CheckoutState.PAYMENT, checkout.state());
        assertEquals(4800L, result.total().ore());
        assertEquals(2000L, result.lines().get(2).finalPrice().ore());
        assertEquals(3, checkout.lines().size());
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

    @Test
    void inventoryWithdrawalReducesOnlyRequestedStock() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        inventory.register(candy(), new BigDecimal("0.500"));

        inventory.withdraw("A", new BigDecimal("2"));

        assertEquals(new BigDecimal("3"), inventory.stock("A"));
        assertEquals(new BigDecimal("0.500"), inventory.stock("B"));
    }

     @Test
    void inventoryWithdrawalCanEmptyStock() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        inventory.register(candy(), new BigDecimal("0.500"));

        inventory.withdraw("A", new BigDecimal("5"));

        assertEquals(0, inventory.stock("A").signum());
        assertEquals(new BigDecimal("0.500"), inventory.stock("B"));
    }

    @Test
    void inventoryRejectsWithdrawalLargerThanStock() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        inventory.register(candy(), new BigDecimal("0.500"));

        assertThrows(IllegalStateException.class, () -> {
            inventory.withdraw("A", new BigDecimal("6"));
        });

        assertEquals(new BigDecimal("5"), inventory.stock("A"));
        assertEquals(new BigDecimal("0.500"), inventory.stock("B"));
    }

    @Test
    void inventoryRejectsWithdrawalFromEmptyStock() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), BigDecimal.ZERO);
        inventory.register(candy(), new BigDecimal("0.500"));

        assertThrows(IllegalStateException.class, () -> {
            inventory.withdraw("A", BigDecimal.ONE);
        });

        assertEquals(0, inventory.stock("A").signum());
        assertEquals(new BigDecimal("0.500"), inventory.stock("B"));
    }

    @Test
    void inventoryWithdrawsKilogramsExactly() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        inventory.register(candy(), new BigDecimal("0.500"));

        inventory.withdraw("B", new BigDecimal("0.125"));

        assertEquals(new BigDecimal("0.375"), inventory.stock("B"));
        assertEquals(new BigDecimal("5"), inventory.stock("A"));
    }

    @Test
    void inventoryRejectsInvalidPieceWithdrawalsWithoutChanges() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        inventory.register(candy(), new BigDecimal("0.500"));
        BigDecimal[] invalidQuantities = {
                null, BigDecimal.ZERO, new BigDecimal("-1"), new BigDecimal("1.5")
        };

        for (BigDecimal quantity : invalidQuantities) {
            assertThrows(IllegalArgumentException.class, () -> {
                inventory.withdraw("A", quantity);
            });

            assertEquals(new BigDecimal("5"), inventory.stock("A"));
            assertEquals(new BigDecimal("0.500"), inventory.stock("B"));
        }
    }

    @Test
    void inventoryRejectsFractionOfGramWhenWithdrawing() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        inventory.register(candy(), new BigDecimal("0.500"));

        assertThrows(IllegalArgumentException.class, () -> {
            inventory.withdraw("B", new BigDecimal("0.0005"));
        });

        assertEquals(new BigDecimal("5"), inventory.stock("A"));
        assertEquals(new BigDecimal("0.500"), inventory.stock("B"));
    }

    @Test
    void inventoryRejectsUnknownWithdrawalIdWithoutChanges() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        inventory.register(candy(), new BigDecimal("0.500"));
        String[] unknownIds = {null, "", "missing", "a"};

        for (String id : unknownIds) {
            assertThrows(IllegalArgumentException.class, () -> {
                inventory.withdraw(id, BigDecimal.ONE);
            });

            assertEquals(new BigDecimal("5"), inventory.stock("A"));
            assertEquals(new BigDecimal("0.500"), inventory.stock("B"));
        }
    }

    @Test
    void inventoryCanRestockAfterFullWithdrawal() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));

        inventory.withdraw("A", new BigDecimal("5"));
        assertEquals(0, inventory.stock("A").signum());

        inventory.restock("A", new BigDecimal("2"));
        assertEquals(new BigDecimal("2"), inventory.stock("A"));

        inventory.withdraw("A", BigDecimal.ONE);
        assertEquals(BigDecimal.ONE, inventory.stock("A"));
    }

    @Test
    void inventoryChecksStockOnEveryWithdrawal() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        inventory.register(candy(), new BigDecimal("0.500"));

        inventory.withdraw("A", new BigDecimal("3"));
        assertEquals(new BigDecimal("2"), inventory.stock("A"));

        assertThrows(IllegalStateException.class, () -> {
            inventory.withdraw("A", new BigDecimal("3"));
        });
        assertEquals(new BigDecimal("2"), inventory.stock("A"));
        assertEquals(new BigDecimal("0.500"), inventory.stock("B"));

        inventory.withdraw("A", new BigDecimal("2"));
        assertEquals(0, inventory.stock("A").signum());
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

    // Cart changes
    @Test
    void cartChangeQuantityReplacesPreviousAmount() {
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("5"));
        cart.add(candy(), new BigDecimal("0.500"));

        cart.changeQuantity("A", new BigDecimal("2"));

        assertEquals(new BigDecimal("2"), cart.lines().get(0).quantity());
        assertEquals(2, cart.lines().size());
        assertEquals("A", cart.lines().get(0).product().id());
        assertEquals("B", cart.lines().get(1).product().id());
        assertEquals(new BigDecimal("0.500"), cart.lines().get(1).quantity());
    }

    @Test
    void cartChangeQuantityCanIncreaseAmount() {
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("2"));

        cart.changeQuantity("A", new BigDecimal("7"));

        assertEquals(1, cart.lines().size());
        assertEquals(new BigDecimal("7"), cart.lines().get(0).quantity());
    }

    @Test
    void cartChangesWeightAndAcceptsTrailingZeros() {
        Cart cart = new Cart();
        cart.add(candy(), new BigDecimal("0.500"));

        cart.changeQuantity("B", new BigDecimal("0.1250"));

        assertEquals(1, cart.lines().size());
        assertEquals(0, new BigDecimal("0.125")
                .compareTo(cart.lines().get(0).quantity()));
    }

    @Test
    void cartRemoveReducesOnlyRequestedProduct() {
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("5"));
        cart.add(candy(), new BigDecimal("0.500"));

        cart.remove("A", new BigDecimal("2"));

        assertEquals(2, cart.lines().size());
        assertEquals("A", cart.lines().get(0).product().id());
        assertEquals(new BigDecimal("3"), cart.lines().get(0).quantity());
        assertEquals("B", cart.lines().get(1).product().id());
        assertEquals(new BigDecimal("0.500"), cart.lines().get(1).quantity());
    }

    @Test
    void cartRemoveWholeQuantityDeletesLine() {
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("5"));
        cart.add(candy(), new BigDecimal("0.500"));

        cart.remove("A", new BigDecimal("5.0"));

        assertEquals(1, cart.lines().size());
        assertEquals("B", cart.lines().get(0).product().id());
        assertEquals(new BigDecimal("0.500"), cart.lines().get(0).quantity());
    }

    @Test
    void cartRemoveLastLineLeavesEmptyCart() {
        Cart cart = new Cart();
        cart.add(bread(), BigDecimal.ONE);

        cart.remove("A", BigDecimal.ONE);

        assertTrue(cart.lines().isEmpty());
    }

    @Test
    void cartRemovesWeightExactly() {
        Cart cart = new Cart();
        cart.add(candy(), new BigDecimal("0.625"));

        cart.remove("B", new BigDecimal("0.125"));

        assertEquals(new BigDecimal("0.500"), cart.lines().get(0).quantity());

        cart.remove("B", new BigDecimal("0.5000"));

        assertTrue(cart.lines().isEmpty());
    }

    @Test
    void cartRejectsInvalidPieceChangesWithoutChangingCart() {
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("5"));
        cart.add(candy(), new BigDecimal("0.500"));
        BigDecimal[] invalidQuantities = {
                null, BigDecimal.ZERO, new BigDecimal("-1"), new BigDecimal("1.5")
        };

        for (BigDecimal quantity : invalidQuantities) {
            assertThrows(IllegalArgumentException.class, () -> {
                cart.changeQuantity("A", quantity);
            });

            assertEquals(2, cart.lines().size());
            assertEquals(new BigDecimal("5"), cart.lines().get(0).quantity());
            assertEquals(new BigDecimal("0.500"), cart.lines().get(1).quantity());
        }
    }

    @Test
    void cartRejectsRemovingMoreThanCartContains() {
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("5"));
        cart.add(candy(), new BigDecimal("0.500"));

        assertThrows(IllegalArgumentException.class, () -> {
            cart.remove("A", new BigDecimal("6"));
        });

        assertEquals(2, cart.lines().size());
        assertEquals(new BigDecimal("5"), cart.lines().get(0).quantity());
        assertEquals(new BigDecimal("0.500"), cart.lines().get(1).quantity());
    }

    @Test
    void cartRejectsInvalidRemovalAmountsWithoutChangingCart() {
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("5"));
        cart.add(candy(), new BigDecimal("0.500"));
        BigDecimal[] invalidQuantities = {
                null, BigDecimal.ZERO, new BigDecimal("-1"), new BigDecimal("1.5")
        };

        for (BigDecimal quantity : invalidQuantities) {
            assertThrows(IllegalArgumentException.class, () -> {
                cart.remove("A", quantity);
            });

            assertEquals(2, cart.lines().size());
            assertEquals(new BigDecimal("5"), cart.lines().get(0).quantity());
            assertEquals(new BigDecimal("0.500"), cart.lines().get(1).quantity());
        }
    }

    @Test
    void cartRejectsFractionOfGramWithoutChangingCart() {
        Cart cart = new Cart();
        cart.add(candy(), new BigDecimal("0.500"));

        assertThrows(IllegalArgumentException.class, () -> {
            cart.changeQuantity("B", new BigDecimal("0.1251"));
        });
        assertEquals(new BigDecimal("0.500"), cart.lines().get(0).quantity());

        assertThrows(IllegalArgumentException.class, () -> {
            cart.remove("B", new BigDecimal("0.0001"));
        });
        assertEquals(1, cart.lines().size());
        assertEquals(new BigDecimal("0.500"), cart.lines().get(0).quantity());
    }

    @Test
    void cartRejectsUnknownIdWithoutChangingCart() {
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("5"));
        cart.add(candy(), new BigDecimal("0.500"));
        String[] unknownIds = {null, "", "X", "a"};

        for (String id : unknownIds) {
            assertThrows(IllegalArgumentException.class, () -> {
                cart.changeQuantity(id, BigDecimal.ONE);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                cart.remove(id, BigDecimal.ONE);
            });

            assertEquals(2, cart.lines().size());
            assertEquals(new BigDecimal("5"), cart.lines().get(0).quantity());
            assertEquals(new BigDecimal("0.500"), cart.lines().get(1).quantity());
        }
    }

    @Test
    void cartClearRemovesAllLinesAndAllowsReuse() {
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("5"));
        cart.add(candy(), new BigDecimal("0.500"));

        cart.clear();
        assertTrue(cart.lines().isEmpty());

        cart.clear();
        assertTrue(cart.lines().isEmpty());

        cart.add(bread(), BigDecimal.ONE);

        assertEquals(1, cart.lines().size());
        assertEquals("A", cart.lines().get(0).product().id());
        assertEquals(BigDecimal.ONE, cart.lines().get(0).quantity());
    }

    // Percentage discounts
    // EQ-P2, EQ-V1 och EQ-V2
    @Test
    void percentageDiscountAppliesToEligibleProductsInMixedCart() {
        Cart cart = new Cart();
        cart.add(piggelin(), new BigDecimal("2"));
        cart.add(daim(), BigDecimal.ONE);
        cart.add(magnum(), BigDecimal.ONE);
        PercentDiscount discount = new PercentDiscount(Set.of("P", "D"), 20);

        PriceResult result = new PriceCalculator().calculate(cart.lines(), discount);

        assertEquals(4800L, result.total().ore());
        assertEquals(400L, result.lines().get(0).discount().ore());
        assertEquals(1600L, result.lines().get(0).finalPrice().ore());
        assertEquals(300L, result.lines().get(1).discount().ore());
        assertEquals(1200L, result.lines().get(1).finalPrice().ore());
        assertEquals(0L, result.lines().get(2).discount().ore());
        assertEquals(2000L, result.lines().get(2).finalPrice().ore());
    }

    private Product piggelin() {
        return new Product("P", "Piggelin", Unit.PIECE, new Money(1000));
    }

    private Product daim() {
        return new Product("D", "Daim", Unit.PIECE, new Money(1500));
    }

    private Product magnum() {
        return new Product("M", "Magnum", Unit.PIECE, new Money(2000));
    }

    // EQ-P1 och EQ-P3
    @Test
    void percentDiscountRejectsPercentOutsideAllowedRange() {
        int[] invalidPercentages = {-1, 101};

        for (int percent : invalidPercentages) {
            assertThrows(IllegalArgumentException.class, () -> {
                new PercentDiscount(Set.of("P", "D"), percent);
            });
        }
    }

    // Gränsvärden inom EQ-P2, med både EQ-V1 och EQ-V2 i korgen
    @Test
    void percentageDiscountHandlesBoundariesInMixedCart() {
        Cart cart = new Cart();
        cart.add(piggelin(), new BigDecimal("2"));
        cart.add(daim(), BigDecimal.ONE);
        cart.add(magnum(), BigDecimal.ONE);
        int[] percentages = {0, 1, 99, 100};
        long[] expectedTotals = {5500, 5465, 2035, 2000};
        PriceCalculator calculator = new PriceCalculator();

        for (int i = 0; i < percentages.length; i++) {
            PercentDiscount discount = new PercentDiscount(Set.of("P", "D"), percentages[i]);

            PriceResult result = calculator.calculate(cart.lines(), discount);

            assertEquals(expectedTotals[i], result.total().ore(),
                    "Procent: " + percentages[i]);
            assertEquals(2000L, result.lines().get(2).finalPrice().ore());
        }
    }

    // EQ-V1 och EQ-V2: namnändring ska inte styra erbjudandet
    @Test
    void percentageDiscountUsesProductIdsWhenNamesChange() {
        Product renamedProduct = new Product("P", "Piggelin nytt namn",
                Unit.PIECE, new Money(1000));
        Product otherProduct = new Product("X", "Piggelin",
                Unit.PIECE, new Money(1000));
        Cart cart = new Cart();
        cart.add(renamedProduct, BigDecimal.ONE);
        cart.add(otherProduct, BigDecimal.ONE);
        PercentDiscount discount = new PercentDiscount(Set.of("P"), 20);

        PriceResult result = new PriceCalculator().calculate(cart.lines(), discount);

        assertEquals(1800L, result.total().ore());
        assertEquals(800L, result.lines().get(0).finalPrice().ore());
        assertEquals(1000L, result.lines().get(1).finalPrice().ore());
        assertEquals(0L, result.lines().get(1).discount().ore());
    }
    // Cart snapshots
    @Test
    void checkoutExposesReadOnlySnapshotDuringPayment() {
        Checkout checkout = new Checkout();
        checkout.add(bread(), new BigDecimal("2"));
        PriceResult frozen = checkout.beginPayment();
        List<CartLine> exposed = checkout.lines();

        assertThrows(UnsupportedOperationException.class, () -> {
            exposed.clear();
        });
        assertThrows(UnsupportedOperationException.class, () -> {
            exposed.add(new CartLine(bread(), BigDecimal.ONE));
        });
        assertThrows(UnsupportedOperationException.class, () -> {
            exposed.set(0, new CartLine(bread(), new BigDecimal("5")));
        });

        assertEquals(CheckoutState.PAYMENT, checkout.state());
        assertEquals(1, checkout.lines().size());
        assertEquals(new BigDecimal("2"), checkout.lines().get(0).quantity());
        assertEquals(2000L, frozen.total().ore());
    }

    @Test
    void cartSnapshotsKeepTheirValuesAcrossEditsAndClear() {
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("2"));
        cart.add(candy(), new BigDecimal("0.125"));
        List<CartLine> original = cart.lines();

        cart.add(bread(), BigDecimal.ONE);
        List<CartLine> afterAddition = cart.lines();

        cart.changeQuantity("A", new BigDecimal("4"));
        cart.remove("B", new BigDecimal("0.125"));
        cart.clear();

        assertTrue(cart.lines().isEmpty());

        assertEquals(2, original.size());
        assertEquals(new BigDecimal("2"), original.get(0).quantity());
        assertEquals(new BigDecimal("0.125"), original.get(1).quantity());

        assertEquals(2, afterAddition.size());
        assertEquals(new BigDecimal("3"), afterAddition.get(0).quantity());
        assertEquals(new BigDecimal("0.125"), afterAddition.get(1).quantity());
    }

    // Uttag av hela kundkorgen
    @Test
    void inventoryRejectsWholePurchaseWhenOneProductIsShort() {
        CartLine breadLine = new CartLine(bread(), BigDecimal.ONE);
        CartLine candyLine = new CartLine(candy(), new BigDecimal("0.501"));
        List<List<CartLine>> orders = List.of(
                List.of(breadLine, candyLine),
                List.of(candyLine, breadLine));

        for (List<CartLine> lines : orders) {
            Inventory inventory = new Inventory();
            inventory.register(bread(), new BigDecimal("5"));
            inventory.register(candy(), new BigDecimal("0.500"));

            assertThrows(IllegalStateException.class, () -> {
                inventory.withdrawAll(lines);
            });

            assertEquals(new BigDecimal("5"), inventory.stock("A"));
            assertEquals(new BigDecimal("0.500"), inventory.stock("B"));
        }
    }

    @Test
    void inventoryWithdrawsWholeCartWithoutChangingCartOrOtherStock() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        inventory.register(candy(), new BigDecimal("0.500"));
        inventory.register(magnum(), new BigDecimal("7"));
        Cart cart = new Cart();
        cart.add(bread(), new BigDecimal("3"));
        cart.add(candy(), new BigDecimal("0.125"));

        inventory.withdrawAll(cart.lines());

        assertEquals(new BigDecimal("2"), inventory.stock("A"));
        assertEquals(new BigDecimal("0.375"), inventory.stock("B"));
        assertEquals(new BigDecimal("7"), inventory.stock("M"));
        assertEquals(2, cart.lines().size());
        assertEquals(new BigDecimal("3"), cart.lines().get(0).quantity());
        assertEquals(new BigDecimal("0.125"), cart.lines().get(1).quantity());

        inventory.withdrawAll(List.of());

        assertEquals(new BigDecimal("2"), inventory.stock("A"));
        assertEquals(new BigDecimal("0.375"), inventory.stock("B"));
        assertEquals(new BigDecimal("7"), inventory.stock("M"));
    }

    @Test
    void inventoryCountsRepeatedProductIdsAcrossTheWholeRequest() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        inventory.register(candy(), new BigDecimal("0.500"));
        List<CartLine> tooMuch = List.of(
                new CartLine(bread(), new BigDecimal("2")),
                new CartLine(candy(), new BigDecimal("0.125")),
                new CartLine(bread(), new BigDecimal("4")));

        assertThrows(IllegalStateException.class, () -> {
            inventory.withdrawAll(tooMuch);
        });
        assertEquals(new BigDecimal("5"), inventory.stock("A"));
        assertEquals(new BigDecimal("0.500"), inventory.stock("B"));

        List<CartLine> exactStock = List.of(
                new CartLine(bread(), new BigDecimal("2")),
                new CartLine(candy(), new BigDecimal("0.125")),
                new CartLine(bread(), new BigDecimal("3")));

        inventory.withdrawAll(exactStock);

        assertEquals(BigDecimal.ZERO, inventory.stock("A"));
        assertEquals(new BigDecimal("0.375"), inventory.stock("B"));
    }

    @Test
    void inventoryRejectsInvalidRequestsWithoutPartialWithdrawal() {
        List<CartLine> unknownProduct = List.of(
                new CartLine(bread(), BigDecimal.ONE),
                new CartLine(magnum(), BigDecimal.ONE));
        List<CartLine> missingLine = new ArrayList<>();
        missingLine.add(new CartLine(bread(), BigDecimal.ONE));
        missingLine.add(null);
        List<List<CartLine>> invalidRequests = new ArrayList<>();
        invalidRequests.add(unknownProduct);
        invalidRequests.add(missingLine);
        invalidRequests.add(null);

        for (List<CartLine> lines : invalidRequests) {
            Inventory inventory = new Inventory();
            inventory.register(bread(), new BigDecimal("5"));
            inventory.register(candy(), new BigDecimal("0.500"));

            assertThrows(IllegalArgumentException.class, () -> {
                inventory.withdrawAll(lines);
            });

            assertEquals(new BigDecimal("5"), inventory.stock("A"));
            assertEquals(new BigDecimal("0.500"), inventory.stock("B"));
        }
    }

    // Betalning och kvitto
    @Test
    void checkoutRejectsUnderpaymentAndAllowsANewFullPayment() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        Checkout checkout = new Checkout();
        checkout.add(bread(), new BigDecimal("2"));
        PriceResult frozen = checkout.beginPayment();

        assertThrows(IllegalStateException.class, () -> {
            checkout.pay(new Money(1900), inventory);
        });

        assertEquals(CheckoutState.PAYMENT, checkout.state());
        assertEquals(1, checkout.lines().size());
        assertEquals(new BigDecimal("2"), checkout.lines().get(0).quantity());
        assertEquals(new BigDecimal("5"), inventory.stock("A"));
        assertEquals(2000L, frozen.total().ore());

        Receipt receipt = checkout.pay(new Money(2500), inventory);

        assertEquals(CheckoutState.PRINTING_RECEIPT, checkout.state());
        assertEquals(2000L, receipt.total().ore());
        assertEquals(2500L, receipt.paid().ore());
        assertEquals(500L, receipt.change().ore());
        assertEquals(1, receipt.lines().size());
        assertEquals("A", receipt.lines().get(0).product().id());
        assertEquals(new BigDecimal("2"), receipt.lines().get(0).quantity());
        assertEquals(new BigDecimal("3"), inventory.stock("A"));

        checkout.completeReceiptPrinting();

        assertEquals(CheckoutState.EMPTY, checkout.state());
        assertTrue(checkout.lines().isEmpty());
        assertEquals(2000L, receipt.total().ore());
        assertEquals(new BigDecimal("2"), receipt.lines().get(0).quantity());
    }

        @Test
    void checkoutPreservesPurchaseOnStockShortageAndCanRetryAfterRestock() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        inventory.register(candy(), new BigDecimal("0.500"));
        Checkout checkout = new Checkout();
        checkout.add(bread(), BigDecimal.ONE);
        checkout.add(candy(), new BigDecimal("0.501"));
        checkout.beginPayment();

        assertThrows(IllegalStateException.class, () -> {
            checkout.pay(new Money(4000), inventory);
        });

        assertEquals(CheckoutState.PAYMENT, checkout.state());
        assertEquals(2, checkout.lines().size());
        assertEquals(new BigDecimal("5"), inventory.stock("A"));
        assertEquals(new BigDecimal("0.500"), inventory.stock("B"));

        inventory.restock("B", new BigDecimal("0.001"));
        Receipt receipt = checkout.pay(new Money(4000), inventory);

        assertEquals(CheckoutState.PRINTING_RECEIPT, checkout.state());
        assertEquals(3004L, receipt.total().ore());
    }

    @Test
    void checkoutCannotChargeOrEditPurchaseWhileReceiptIsPrinting() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        Checkout checkout = new Checkout();
        checkout.add(bread(), BigDecimal.ONE);
        checkout.beginPayment();
        checkout.pay(new Money(1000), inventory);

        assertThrows(IllegalStateException.class, () -> {
            checkout.pay(new Money(1000), inventory);
        });
        assertThrows(IllegalStateException.class, () -> {
            checkout.add(bread(), BigDecimal.ONE);
        });

        assertEquals(CheckoutState.PRINTING_RECEIPT, checkout.state());
        assertEquals(BigDecimal.ONE, checkout.lines().get(0).quantity());
        assertEquals(new BigDecimal("4"), inventory.stock("A"));
    }

    @Test
    void checkoutAcceptsZeroPaymentForARealPurchaseWithFullDiscount() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        PercentDiscount discount = new PercentDiscount(Set.of("A"), 100);
        Checkout checkout = new Checkout(new PriceCalculator(), discount);
        checkout.add(bread(), BigDecimal.ONE);
        checkout.beginPayment();

        Receipt receipt = checkout.pay(new Money(0), inventory);

        assertEquals(CheckoutState.PRINTING_RECEIPT, checkout.state());
        assertEquals(0L, receipt.total().ore());
        assertEquals(1, receipt.lines().size());
        assertEquals(new BigDecimal("4"), inventory.stock("A"));
    }


    // 3 för 2
    @Test
    void threeForTwoGroupsMixedProductsByPriceRegardlessOfRowOrder() {
        Cart cart = new Cart();
        cart.add(piggelin(), new BigDecimal("2"));
        cart.add(daim(), new BigDecimal("4"));
        cart.add(magnum(), BigDecimal.ONE);
        cart.add(candy(), new BigDecimal("0.125"));
        ThreeForTwoDiscount discount = new ThreeForTwoDiscount(Set.of("P", "D", "B"));
        Map<String, Long> expectedSavings = Map.of(
                "P", 1000L, "D", 1500L, "M", 0L, "B", 0L);
        PriceCalculator calculator = new PriceCalculator();

        for (List<CartLine> lines : List.of(cart.lines(), cart.lines().reversed())) {
            PriceResult result = calculator.calculate(lines, discount);

            assertEquals(8000L, result.total().ore());
            assertEquals(4, result.lines().size());
            for (PricedLine line : result.lines()) {
                assertEquals(expectedSavings.get(line.product().id()),
                        Long.valueOf(line.discount().ore()), line.product().id());
            }
        }
    }

    @Test
    void threeForTwoHonorsGroupBoundariesAndPurchaseLimit() {
        int[] quantities = {0, 2, 3, 5, 6, 8, 9};
        long[] expectedTotals = {0, 2000, 2000, 4000, 4000, 6000, 7000};
        ThreeForTwoDiscount discount = new ThreeForTwoDiscount(Set.of("P", "D"));
        PriceCalculator calculator = new PriceCalculator();

        for (int i = 0; i < quantities.length; i++) {
            Cart cart = new Cart();
            if (quantities[i] > 0) {
                cart.add(piggelin(), BigDecimal.valueOf(quantities[i]));
            }

            PriceResult result = calculator.calculate(cart.lines(), discount);

            assertEquals(expectedTotals[i], result.total().ore(),
                    "Antal rabattvaror: " + quantities[i]);
        }
    }

    // Val av erbjudande
    @Test
    void bestOfferChoosesWholeCampaignAroundThePriceCrossover() {
        Cart cart = new Cart();
        cart.add(piggelin(), BigDecimal.ONE);
        cart.add(daim(), new BigDecimal("2"));
        cart.add(magnum(), BigDecimal.ONE);
        ThreeForTwoDiscount threeForTwo = new ThreeForTwoDiscount(Set.of("P", "D"));
        PriceCalculator calculator = new PriceCalculator();
        int[] percentages = {24, 25, 26};
        long[] expectedTotals = {5000, 5000, 4960};

        for (int i = 0; i < percentages.length; i++) {
            PercentDiscount percent = new PercentDiscount(Set.of("P", "D"), percentages[i]);

            PriceResult result = calculator.calculate(cart.lines(), percent, threeForTwo);

            assertEquals(expectedTotals[i], result.total().ore(),
                    "Procent: " + percentages[i]);
        }
    }

    //Kvitto formatering
    @Test
    void receiptFormatsStoredRowsAndPaymentInAlignedColumns() {
        PriceResult prices = new PriceResult(List.of(
                new PricedLine(bread(), new BigDecimal("3.0"),
                        new Money(3000), new Money(1000)),
                new PricedLine(candy(), new BigDecimal("0.1260"),
                        new Money(504), new Money(0))));
        Receipt receipt = new Receipt(prices, new Money(3000));

        List<String> expected = List.of(
                "KVITTO",
                "Vara                      Mängd    Ord. kr  Rabatt kr    Pris kr",
                "Bröd                       3 st      30,00      10,00      20,00",
                "Godis                  0,126 kg       5,04       0,00       5,04",
                "Totalt: 25,04 kr",
                "Betalt: 30,00 kr",
                "Växel: 4,96 kr");

        assertEquals(expected, receipt.format().lines().toList());
    }

    @Test
    void receiptKeepsTwentyCharacterNamesAndShortensLongerNames() {
        Product exact = new Product("C", "Vaniljglass med kola",
                Unit.PIECE, new Money(1000));
        Product longer = new Product("D", "Vaniljglass med kola!",
                Unit.PIECE, new Money(1000));
        PriceResult prices = new PriceResult(List.of(
                new PricedLine(exact, BigDecimal.ONE, new Money(1000), new Money(0)),
                new PricedLine(longer, BigDecimal.ONE, new Money(1000), new Money(0))));
        Receipt receipt = new Receipt(prices, new Money(2000));

        List<String> text = receipt.format().lines().toList();

        assertEquals("Vaniljglass med kola", text.get(2).substring(0, 20));
        assertEquals("Vaniljglass med k...", text.get(3).substring(0, 20));
    }

    //Hantera flera kunder utan att förlora tidigare kvitto
    @Test
    void checkoutHandlesNextCustomerWithoutChangingPreviousReceipt() {
        Inventory inventory = new Inventory();
        inventory.register(bread(), new BigDecimal("5"));
        inventory.register(daim(), new BigDecimal("2"));
        Checkout checkout = new Checkout();

        checkout.add(bread(), new BigDecimal("2"));
        checkout.beginPayment();
        Receipt first = checkout.pay(new Money(2500), inventory);
        String firstText = first.format();
        checkout.completeReceiptPrinting();

        assertEquals(CheckoutState.EMPTY, checkout.state());
        assertTrue(checkout.lines().isEmpty());

        checkout.add(daim(), BigDecimal.ONE);
        assertEquals(CheckoutState.SHOPPING, checkout.state());
        checkout.beginPayment();
        assertEquals(CheckoutState.PAYMENT, checkout.state());
        Receipt second = checkout.pay(new Money(2000), inventory);
        assertEquals(CheckoutState.PRINTING_RECEIPT, checkout.state());

        assertEquals(1500L, second.total().ore());
        assertEquals(1, second.lines().size());
        assertEquals("D", second.lines().get(0).product().id());

        checkout.completeReceiptPrinting();

        assertEquals(CheckoutState.EMPTY, checkout.state());
        assertTrue(checkout.lines().isEmpty());
        assertEquals(firstText, first.format());
    }

    // Avrundning
    @Test
    void priceCalculatorRoundsAfterDiscountAndSumsRoundedRows() {
        Product first = new Product("R1", "Godis", Unit.KILOGRAM, new Money(101));
        Product second = new Product("R2", "Nötter", Unit.KILOGRAM, new Money(101));
        List<CartLine> lines = List.of(
                new CartLine(first, new BigDecimal("0.500")),
                new CartLine(second, new BigDecimal("0.500")));
        PercentDiscount discount = new PercentDiscount(Set.of("R1", "R2"), 10);

        PriceResult result = new PriceCalculator().calculate(lines, discount);

        for (PricedLine line : result.lines()) {
            assertEquals(51L, line.originalPrice().ore());
            assertEquals(45L, line.finalPrice().ore());
            assertEquals(6L, line.discount().ore());
        }
        assertEquals(90L, result.total().ore());
    }

    // IO och köpflöde
    @Test
    void ioDrivesPurchaseAndPrintsReceiptBeforeResettingCheckout() {
        Inventory inventory = new Inventory();
        inventory.register(piggelin(), new BigDecimal("5"));
        inventory.register(candy(), new BigDecimal("0.500"));
        Checkout checkout = new Checkout(new PriceCalculator(),
                new PercentDiscount(Set.of("P", "D"), 25),
                new ThreeForTwoDiscount(Set.of("P", "D")));
        TKSIO io = new TKSIO(checkout,
                "vara P 1", "vara B 0,125", "kassa", "tillbaka",
                "andra P 4", "ta-bort P 1", "kassa", "betala 30,00", "sluta");
        KassaApp app = new KassaApp(io, checkout, inventory,
                Map.of("P", piggelin(), "B", candy()));

        app.run();

        assertEquals(List.of(
                CheckoutState.EMPTY, CheckoutState.SHOPPING, CheckoutState.SHOPPING,
                CheckoutState.PAYMENT, CheckoutState.SHOPPING, CheckoutState.SHOPPING,
                CheckoutState.SHOPPING, CheckoutState.PAYMENT, CheckoutState.EMPTY),
                io.statesAtRead);
        assertTrue(io.statesAtPrint.contains(CheckoutState.PRINTING_RECEIPT));
        String receipt = io.out.get(io.out.size() - 1);
        assertTrue(receipt.startsWith("KVITTO\n"));
        assertTrue(receipt.contains("Piggelin"));
        assertTrue(receipt.contains("Godis"));
        assertTrue(receipt.contains("Totalt: 25,00 kr\nBetalt: 30,00 kr\nVäxel: 5,00 kr"));
    }

    @Test
    void ioLetsCustomerRetryInvalidInputAndUnderpayment() {
        Inventory inventory = new Inventory();
        inventory.register(piggelin(), new BigDecimal("5"));
        Checkout checkout = new Checkout();
        TKSIO io = new TKSIO(checkout,
                "vara P fel", "vara P 1", "kassa", "betala 1,00", "betala 10,00");
        KassaApp app = new KassaApp(io, checkout, inventory, Map.of("P", piggelin()));

        app.run();

        assertEquals(List.of(
                CheckoutState.EMPTY, CheckoutState.EMPTY, CheckoutState.SHOPPING,
                CheckoutState.PAYMENT, CheckoutState.PAYMENT, CheckoutState.EMPTY),
                io.statesAtRead);
        assertTrue(io.out.contains("Fel: Ange ett giltigt tal. Betalning får ha högst två decimaler."));
        assertTrue(io.out.contains("Fel: Betalningen räcker inte"));
        assertEquals(new BigDecimal("4"), inventory.stock("P"));
    }

    private static final class TKSIO implements KSIO {
        private final Checkout checkout;
        private final List<String> in;
        private final List<String> out = new ArrayList<>();
        private final List<CheckoutState> statesAtRead = new ArrayList<>();
        private final List<CheckoutState> statesAtPrint = new ArrayList<>();
        private int index;

        private TKSIO(Checkout checkout, String... in) {
            this.checkout = checkout;
            this.in = List.of(in);
        }

        @Override
        public String read() {
            statesAtRead.add(checkout.state());
            return index < in.size() ? in.get(index++) : null;
        }

        @Override
        public void print(String str) {
            statesAtPrint.add(checkout.state());
            out.add(str);
        }
    }

    //Matchers
    @Test
    void inventoryHandlesLastGramWithoutDependingOnDecimalScale() {
        Inventory inventory = new Inventory();
        inventory.register(candy(), new BigDecimal("0.5000"));

        inventory.withdraw("B", new BigDecimal("0.499"));
        assertThat(inventory.stock("B"), comparesEqualTo(new BigDecimal("0.001")));

        assertThrows(IllegalStateException.class, () -> {
            inventory.withdraw("B", new BigDecimal("0.002"));
        });
        assertThat(inventory.stock("B"), comparesEqualTo(new BigDecimal("0.001")));

        inventory.withdraw("B", new BigDecimal("0.0010"));
        assertThat(inventory.stock("B"), comparesEqualTo(BigDecimal.ZERO));
    }

    @Test
    void checkoutChangesWinningOfferAfterCartEditAndUsesItForPayment() {
        Inventory inventory = new Inventory();
        inventory.register(piggelin(), new BigDecimal("10"));
        inventory.register(magnum(), new BigDecimal("2"));
        PercentDiscount percent = new PercentDiscount(Set.of("P", "D"), 25);
        ThreeForTwoDiscount threeForTwo = new ThreeForTwoDiscount(Set.of("P", "D"));
        Checkout checkout = new Checkout(new PriceCalculator(), percent, threeForTwo);
        checkout.add(piggelin(), new BigDecimal("3"));
        checkout.add(magnum(), BigDecimal.ONE);

        PriceResult first = checkout.beginPayment();
        assertEquals(4000L, first.total().ore());

        checkout.returnToShopping();
        checkout.changeQuantity("P", new BigDecimal("9"));
        PriceResult updated = checkout.beginPayment();
        assertEquals(8750L, updated.total().ore());

        Receipt receipt = checkout.pay(new Money(10000), inventory);

        List<String> ids = new ArrayList<>();
        for (PricedLine line : receipt.lines()) {
            String id = line.product().id();
            ids.add(id);
            if (id.equals("P")) {
                assertEquals(new BigDecimal("9"), line.quantity());
                assertEquals(6750L, line.finalPrice().ore());
            } else if (id.equals("M")) {
                assertEquals(BigDecimal.ONE, line.quantity());
                assertEquals(2000L, line.finalPrice().ore());
            }

        }
        assertThat(ids, containsInAnyOrder("M", "P"));

        assertEquals(8750L, receipt.total().ore());
        assertEquals(1250L, receipt.change().ore());
        assertEquals(BigDecimal.ONE, inventory.stock("P"));
        assertEquals(BigDecimal.ONE, inventory.stock("M"));
    }
}