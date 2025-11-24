// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.module.health.events;

import org.terasology.engine.entitySystem.event.AbstractConsumableValueModifiableEvent;
import org.terasology.gestalt.naming.Name;

/**
 * A <i>collector event</i> to allow interested systems to contribute to a regeneration action.
 * <p>
 * An individual collector event is sent for each registered regeneration effect. The resulting amount per regeneration
 * effect is at minimum 0 (no negative effects due to regeneration). The final regeneration amount is computed as the
 * sum of all individual regeneration effect amounts.
 * <p>
 * To inflict damage caused by regeneration send a separate {@link DoDamageEvent} instead.
 */
public class BeforeRegenEvent extends AbstractConsumableValueModifiableEvent {
    final Name id;

    // TODO: make package-private after restructuring module (logical instead of ECS-based packages)
    public BeforeRegenEvent(Name id, float baseValue) {
        super(baseValue);
        this.id = id;
    }

    /**
     * Identifier of the regeneration effect to collect contribution for.
     */
    public Name getId() {
        return id;
    }
}
