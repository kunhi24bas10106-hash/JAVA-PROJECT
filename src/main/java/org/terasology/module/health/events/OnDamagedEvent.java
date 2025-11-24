// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.events;

import com.google.common.base.Preconditions;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.network.OwnerEvent;

/**
 * This event is sent after damage has been dealt to an entity.
 */
@OwnerEvent
public class OnDamagedEvent extends OnHealthChangedEvent {
    private final Prefab damageType;

    /**
     * INTERNAL: Only required for internal replication of network events
     */
    OnDamagedEvent() {
        this(0, null, EntityRef.NULL);
    }

    public OnDamagedEvent(int change, Prefab damageType, EntityRef instigator) {
        super(-change, instigator);
        Preconditions.checkArgument(change >= 0, "damage amount must be non-negative - use OnRestoredEvent instead");
        this.damageType = damageType;
    }

    public int getDamageAmount() {
        return -change;
    }

    public Prefab getType() {
        return damageType;
    }

}
