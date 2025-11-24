// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.module.health.time;

import com.google.common.base.Objects;

public final class Duration {

    public static final Duration INFINITE = new Duration(-1L);

    private final long durationInMs;

    private Duration(long durationInMs) {
        this.durationInMs = durationInMs;
    }

    // ------------------------------------------------------------------------

    public static Duration fromMillis(long durationInMs) {
        if (durationInMs < 0) {
            return INFINITE;
        }
        return new Duration(durationInMs);
    }

    public static Duration fromSeconds(float durationInSeconds) {
        if (durationInSeconds < 0) {
            return INFINITE;
        }
        return new Duration((long) durationInSeconds * 1000);
    }

    // ------------------------------------------------------------------------

    public boolean isInfinite() {
        return durationInMs < 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Duration duration = (Duration) o;
        return durationInMs == duration.durationInMs;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(durationInMs);
    }

    // ------------------------------------------------------------------------

    public long getInMillis() {
        return durationInMs;
    }

    public float getInSeconds() {
        return durationInMs / 1000f;
    }

    // ------------------------------------------------------------------------

    public Duration max(Duration other) {
        if (this.isInfinite() || other.isInfinite()) {
            return INFINITE;
        } else {
            return this.durationInMs > other.durationInMs ? this : other;
        }
    }

    public Duration min(Duration other) {
        if (this.isInfinite()) {
            return other;
        }
        if (other.isInfinite()) {
            return this;
        }
        return this.durationInMs < other.durationInMs ? this : other;
    }

    // ------------------------------------------------------------------------

}
