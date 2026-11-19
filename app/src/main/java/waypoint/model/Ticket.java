package waypoint.model;

import java.time.Instant;
import java.util.Objects;

/** ENTITY. Invariants:
 *   I1  entry is always present
 *   I2  exit is null, or not before entry
 *   I3  fare is null, or not negative
 *   I4  fare exists only if exit exists
 *   I5  a closed ticket never reopens
 */
public final class Ticket {
    private final TicketId id;
    private final SpotId spotId;
    private final Instant entry;
    private Instant exit;   // non-final
    private Money fare;     // non-final

    private Ticket(TicketId id, SpotId spotId, Instant entry) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.spotId = Objects.requireNonNull(spotId, "spotId must not be null");
        this.entry = Objects.requireNonNull(entry, "entry must not be null");
    }

    public static Ticket open(TicketId id, SpotId spotId, Instant entry) {
        return new Ticket(id, spotId, entry);
    }

    public boolean isOpen() {
        return exit == null;
    }

    public Money close(Instant at, FarePolicy policy) {
        Objects.requireNonNull(at, "at must not be null");
        Objects.requireNonNull(policy, "policy must not be null");
        if (!isOpen()) {
            throw new TicketAlreadyClosed("Ticket " + id.value() + " is already closed");
        }
        if (at.isBefore(entry)) {
            throw new IllegalArgumentException("Exit " + at + " is before entry " + entry);
        }
        Money computed = policy.fareFor(entry, at, spotId);
        if (computed == null || computed.isNegative()) {
            throw new IllegalStateException("Fare policy returned an invalid fare");
        }
        // everything validated: set exit and fare together
        this.exit = at;
        this.fare = computed;
        return computed;
    }

    public TicketId id() {
        return id;
    }

    public SpotId spotId() {
        return spotId;
    }

    public Instant entry() {
        return entry;
    }

    public Instant exit() {
        return exit;
    }

    public Money fare() {
        return fare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
class TicketAlreadyClosed extends RuntimeException {
    TicketAlreadyClosed(String message) {
        super(message);
    }
}