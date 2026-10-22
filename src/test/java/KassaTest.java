import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KassaTest {
    // Money
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
}