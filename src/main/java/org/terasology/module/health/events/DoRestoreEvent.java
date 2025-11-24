// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.events;

import com.google.common.base.Preconditions;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * A <i>trigger event</i> to restore health to an entity. This event is the starting point of a {@link
 * org.terasology.module.health.systems.RestorationAuthoritySystem Restoration Event Flow}.
 * <p>
 * To inflict damage, send a {@link DoDamageEvent} instead.
 */
public class DoRestoreEvent implements Event {
    private int amount;
    private EntityRef instigator;

    public DoRestoreEvent(int amount) {
        this(amount, EntityRef.NULL);
    }

    public DoRestoreEvent(int amount, EntityRef entity) {
        Preconditions.checkArgument(amount >= 0, "restoration amount must be non-negative - use DoDamageEvent instead");
        this.amount = amount;
        this.instigator = entity;
    }

    /**
     * The amount of health points to be restored.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * The entity that caused the restoration effect.
     */
    public EntityRef getInstigator() {
        return instigator;
    }
}
