// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.events;

import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.gestalt.naming.Name;
import org.terasology.module.health.components.RegenComponent;
import org.terasology.module.health.systems.RegenAuthoritySystem;

/**
 * Send this event to immediately and explicitly deregister a regeneration effect from an entity.
 * <p>
 * This event does not have an effect, if the referenced regeneration effect is not registered on the target entity.
 *
 * @see RegenAuthoritySystem
 * @see RegenComponent
 */
public class DeregisterRegenEvent implements Event {
    /**
     * Identifier of the regeneration effect to be removed.
     */
    public Name id;

    /**
     * Deregister a regeneration effect from the target entity.
     *
     * @param id identifier of the effect
     */
    public DeregisterRegenEvent(Name id) {
        this.id = id;
    }
}
