// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.AbstractConsumableValueModifiableEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;

/**
 * This event is sent to allow damage to be modified or cancelled, before it is processed.
 * <br><br>
 * Damage modifications are accumulated as additions/subtractions (modifiers) and multipliers.
 *
 */
public class BeforeDamagedEvent extends AbstractConsumableValueModifiableEvent {
    private Prefab damageType;
    private EntityRef instigator;
    private EntityRef directCause;

    public BeforeDamagedEvent(int baseDamage, Prefab damageType, EntityRef instigator, EntityRef directCause) {
        super(baseDamage);
        this.damageType = damageType;
        this.instigator = instigator;
        this.directCause = directCause;
    }

    public Prefab getDamageType() {
        return damageType;
    }

    public EntityRef getInstigator() {
        return instigator;
    }

    public EntityRef getDirectCause() {
        return directCause;
    }
}
