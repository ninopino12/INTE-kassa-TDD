package se.inte.kassa;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {
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
        assertThrows(IllegalArgumentException.class, () -> new Money(-1));
    }
}
