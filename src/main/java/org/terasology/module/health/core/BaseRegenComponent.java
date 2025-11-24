// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.module.health.core;

import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Configuration for the base regeneration as defined by {@link BaseRegenAuthoritySystem#BASE_REGEN}.
 */
public class BaseRegenComponent implements Component<BaseRegenComponent> {

    /**
     * Amount of health points restored per (real-time) second.
     */
    @Replicate
    public float regenRate;

    /**
     * The cool down in seconds before the base regeneration applies after taking damage.
     */
    @Replicate
    public float waitBeforeRegen;

    /** The last game time in milliseconds at which the entity received damage. */
    public long lastHitTimestampInMs;

    @Override
    public void copyFrom(BaseRegenComponent other) {
        this.regenRate = other.regenRate;
        this.waitBeforeRegen = other.waitBeforeRegen;
        this.lastHitTimestampInMs = other.lastHitTimestampInMs;

    }
}
