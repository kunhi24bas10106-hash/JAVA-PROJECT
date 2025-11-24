// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.events;

import org.terasology.engine.entitySystem.event.AbstractConsumableValueModifiableEvent;

/**
 * A <i>collector event</i> to allow interested systems to contribute to a restoration action.
 * <p>
 * To inflict damage caused by restoration send a separate {@link DoDamageEvent} instead.
 */
public class BeforeRestoreEvent extends AbstractConsumableValueModifiableEvent {

    // TODO: make package-private after restructuring module (logical instead of ECS-based packages)
    public BeforeRestoreEvent(int baseValue) {
        super(baseValue);
    }
}
