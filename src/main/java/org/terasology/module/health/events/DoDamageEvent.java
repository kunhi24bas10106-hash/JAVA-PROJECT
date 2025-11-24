// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.events;

import com.google.common.base.Preconditions;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.logic.health.EngineDamageTypes;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * This event should be sent to cause damage to an entity.
 *
 */
public class DoDamageEvent implements Event {
    private int amount;
    private Prefab damageType;
    private EntityRef instigator;
    private EntityRef directCause;

    public DoDamageEvent(int amount) {
        this(amount, EngineDamageTypes.DIRECT.get());
    }

    public DoDamageEvent(int amount, Prefab damageType) {
        this(amount, damageType, EntityRef.NULL);
    }

    public DoDamageEvent(int amount, Prefab damageType, EntityRef instigator) {
        this(amount, damageType, instigator, EntityRef.NULL);
    }

    /**
     * @param amount     The amount of damage being caused
     * @param damageType The type of the damage being dealt
     * @param instigator The instigator of the damage (which entity caused it)
     * @param directCause       Tool used to cause the damage
     */
    public DoDamageEvent(int amount, Prefab damageType, EntityRef instigator, EntityRef directCause) {
        Preconditions.checkArgument(amount >= 0, "damage amount must be non-negative - use DoRestorationEvent instead");
        this.amount = amount;
        this.damageType = damageType;
        this.instigator = instigator;
        this.directCause = directCause;
    }

    public int getAmount() {
        return amount;
    }

    public EntityRef getInstigator() {
        return instigator;
    }

    public Prefab getDamageType() {
        return damageType;
    }

    public EntityRef getDirectCause() {
        return directCause;
    }
}
