// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * The event sent to restore entity to full health.
 */
public class RestoreFullHealthEvent implements Event {
    /** The entity that caused the full health restoration */
    private EntityRef instigator;


    public RestoreFullHealthEvent(EntityRef entity) {
        this.instigator = entity;
    }

    public EntityRef getInstigator() {
        return instigator;
    }
}
