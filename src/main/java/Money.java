public final class Money {
    private final long ore;

    public Money(long ore) {
        if (ore < 0) {
            throw new IllegalArgumentException("Beloppet får inte vara negativt");
        }
        this.ore = ore;
    }

    public long ore() {
        return ore;
    }

    public Money add(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Belopp måste finnas");
        }

        return new Money(Math.addExact(ore, other.ore));
    }

    public Money subtract(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Belopp måste finnas");
        }
        if (ore < other.ore) {
            throw new IllegalArgumentException("Resultatet får inte vara negativt");
        }

        return new Money(ore - other.ore);
    }

    public int compareTo(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Belopp måste finnas");
        }

        return Long.compare(ore, other.ore);
    }
}