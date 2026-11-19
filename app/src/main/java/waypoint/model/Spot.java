package waypoint.model;

import java.util.Objects;

/** ENTITY. Identity = id. Invariant: a spot cannot be assigned while occupied. */
public final class Spot {
    private final SpotId id;
    private VehicleId occupant;   // the ONLY mutable field

    public Spot(SpotId id) {
        this.id = Objects.requireNonNull(id, "id must not be null");
    }

    public SpotId id() {
        return id;
    }

    /** true only if this call took the spot. */
    public boolean tryAssign(VehicleId v) {
        Objects.requireNonNull(v, "vehicle must not be null");
        if (occupant != null) {
            return false;
        }
        occupant = v;
        return true;
    }

    /** Idempotent: releasing a free spot is not an error. */
    public void release(VehicleId v) {
        if (occupant != null && occupant.equals(v)) {
            occupant = null;
        }
    }

    public boolean isFree() {
        return occupant == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Spot other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}