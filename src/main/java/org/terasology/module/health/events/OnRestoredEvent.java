// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.events;

import com.google.common.base.Preconditions;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.OwnerEvent;

/**
 * A <i>notification event</i> to inform that an entity was restored. This is the final event of the {@link
 * org.terasology.module.health.systems.RestorationAuthoritySystem Restoration Event Flow}.
 */
@OwnerEvent
public class OnRestoredEvent extends OnHealthChangedEvent {
    /**
     * INTERNAL: Only required for internal replication of network events
     */
    OnRestoredEvent() {
        this(0, EntityRef.NULL);
    }

    public OnRestoredEvent(int amount, EntityRef instigator) {
        super(amount, instigator);
        Preconditions.checkArgument(amount >= 0, "restoration amount must be non-negative - use OnDamagedEvent " +
                "instead");
    }

    /**
     * The amount of health points that was restored.
     */
    public int getRestorationAmount() {
        return change;
    }

}
