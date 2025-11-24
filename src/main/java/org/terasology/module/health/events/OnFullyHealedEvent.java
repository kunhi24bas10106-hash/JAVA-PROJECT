// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * Event sent upon an entity reaching full health if previously on less than full health.
 *
 */
public class OnFullyHealedEvent implements Event {
    private EntityRef instigator;

    public OnFullyHealedEvent(EntityRef instigator) {
        this.instigator = instigator;
    }

    public EntityRef getInstigator() {
        return instigator;
    }

}
