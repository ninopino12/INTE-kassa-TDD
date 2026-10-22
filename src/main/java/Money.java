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
}
