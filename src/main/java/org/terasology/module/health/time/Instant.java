// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.module.health.time;

import java.util.Objects;

public final class Instant {

    public static final Instant NEVER = new Instant(-1);

    private final long timestampInMs;

    private Instant(long timestampInMs) {
        this.timestampInMs = timestampInMs;
    }

    // ------------------------------------------------------------------------

    public static Instant fromMillis(long timestampInMs) {
        if (timestampInMs < 0) {
            return NEVER;
        }
        return new Instant(timestampInMs);
    }

    public static Instant fromSeconds(float timestampInSeconds) {
        if (timestampInSeconds < 0) {
            return NEVER;
        }
        return new Instant((long) timestampInSeconds * 1000);
    }

    // ------------------------------------------------------------------------

    public boolean isNever() {
        return timestampInMs < 0;
    }

    public boolean isBefore(Instant other) {
        return !isNever() && this.timestampInMs < other.timestampInMs;
    }

    public boolean isAfter(Instant other) {
        return isNever() || this.timestampInMs > other.timestampInMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Instant instant = (Instant) o;
        return timestampInMs == instant.timestampInMs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestampInMs);
    }

    // ------------------------------------------------------------------------

    public long getInMillis() {
        return timestampInMs;
    }

    public float getInSeconds() {
        return timestampInMs / 1000f;
    }

    // ------------------------------------------------------------------------

    public Instant max(Instant other) {
        if (this.isNever() || other.isNever()) {
            return NEVER;
        } else {
            return this.timestampInMs > other.timestampInMs ? this : other;
        }
    }

    public Instant min(Instant other) {
        if (this.isNever()) {
            return other;
        }
        if (other.isNever()) {
            return this;
        }
        return this.timestampInMs < other.timestampInMs ? this : other;
    }

    public Instant plus(Duration duration) {
        if (this.isNever() || duration.isInfinite()) {
            return NEVER;
        }
        return new Instant(this.timestampInMs + duration.getInMillis());
    }
}
