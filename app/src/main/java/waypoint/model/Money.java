package waypoint.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** VALUE OBJECT. Exact money stored in minor units (paise). No double, no float. */
public final class Money implements Comparable<Money> {
    private final long paise;
    private final Currency currency;

    private Money(long paise, Currency currency) {
        this.paise = paise;
        this.currency = Objects.requireNonNull(currency, "currency must not be null");
    }

    public static Money ofMinor(long paise, Currency currency) {
        return new Money(paise, currency);
    }

    public static Money rupees(long rupees) {
        return new Money(Math.multiplyExact(rupees, 100L), Currency.INR);
    }

    public long paise() {
        return paise;
    }

    public Currency currency() {
        return currency;
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "other must not be null");
        if (this.currency != other.currency) {
            throw new CurrencyMismatch("Currency mismatch: " + this.currency + " vs " + other.currency);
        }
    }

    public Money plus(Money other) {
        requireSameCurrency(other);
        return new Money(Math.addExact(this.paise, other.paise), currency);
    }

    public Money minus(Money other) {
        requireSameCurrency(other);
        return new Money(Math.subtractExact(this.paise, other.paise), currency);
    }

    public Money times(int factor) {
        return new Money(Math.multiplyExact(this.paise, (long) factor), currency);
    }

    public boolean isNegative() {
        return paise < 0;
    }

    @Override
    public int compareTo(Money other) {
        requireSameCurrency(other);
        return Long.compare(this.paise, other.paise);
    }

    /** Splits this amount into n shares that sum EXACTLY to this amount. */
    public List<Money> allocate(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }
        long base = Math.floorDiv(paise, (long) n);
        long remainder = Math.floorMod(paise, (long) n);
        List<Money> shares = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            shares.add(new Money(i < remainder ? base + 1 : base, currency));
        }
        return Collections.unmodifiableList(shares);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money other)) return false;
        return this.paise == other.paise && this.currency == other.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(paise, currency);
    }

    @Override
    public String toString() {
        String sign = paise < 0 ? "-" : "";
        return currency + " " + sign + Math.abs(paise / 100) + "."
                + String.format("%02d", Math.abs(paise % 100));
    }
}
class CurrencyMismatch extends RuntimeException {
    CurrencyMismatch(String message) {
        super(message);
    }
}
enum Currency {
    INR, USD
}