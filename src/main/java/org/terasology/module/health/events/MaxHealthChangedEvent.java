// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.module.health.events;

import org.terasology.engine.network.OwnerEvent;
import org.terasology.gestalt.entitysystem.event.Event;

@OwnerEvent
public class MaxHealthChangedEvent implements Event {
    private final int newValue;
    private final int oldValue;

    /**
     * INTERNAL: Only required for internal replication of network events
     */
    MaxHealthChangedEvent() {
        this(0, 0);
    }

    /**
     * Create a new notification event on character maxHealth scaling.
     *
     * @param oldValue the entity's old maxHealth.
     * @param newValue the entity's new maxHealth. (must be greater zero)
     */
    public MaxHealthChangedEvent(final int oldValue, final int newValue) {
        if (newValue <= 0) {
            throw new IllegalArgumentException("zero or negative value");
        }
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * The old max Health previous to scaling.
     */
    public int getOldValue() {
        return oldValue;
    }

    /**
     * The new max Health after scaling.
     */
    public int getNewValue() {
        return newValue;
    }

    /**
     * The scaling factor determined by the quotient of new and old value.
     * <p>
     * This is guaranteed to be greater zero (> 0).
     */
    public float getFactor() {
        return (float) newValue / oldValue;
    }
}

