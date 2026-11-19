package waypoint.model;

/** Distinct id types so they can never be swapped at a call site. */
record TicketId(String value) {
    TicketId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TicketId must not be null or blank");
        }
    }
}

record SpotId(String value) {
    SpotId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SpotId must not be null or blank");
        }
    }
}

record VehicleId(String value) {
    VehicleId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("VehicleId must not be null or blank");
        }
    }
}