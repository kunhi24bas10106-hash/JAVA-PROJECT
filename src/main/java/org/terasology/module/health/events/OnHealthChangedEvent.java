// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.module.health.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * This event (or a subtype) is sent whenever health changes
 *
 */
public class OnHealthChangedEvent implements Event {
    protected EntityRef instigator;
    protected int change;

    public OnHealthChangedEvent(int change) {
        this(change, EntityRef.NULL);
    }

    public OnHealthChangedEvent(int change, EntityRef instigator) {
        this.instigator = instigator;
        this.change = change;
    }

    public EntityRef getInstigator() {
        return instigator;
    }

    /**
     * Method to get the amount by which health has changed.
     *
     * @return The amount by which health changed. This is capped (by the implementing method),
     *         so if the entity received 9999 damage and only had 10 health, only 10 points damage will be inflicted.
     */
    public int getHealthChange() {
        return change;
    }
}
