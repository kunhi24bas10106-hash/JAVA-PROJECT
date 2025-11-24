// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.components;

import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.nui.properties.TextField;

/**
 * Provides Health to entity attached with HealthComponent. Contains the parameters
 * required for all health related events.
 */
public class HealthComponent implements Component<HealthComponent> {

    /** Maximum allowed health, capped to this if exceeding this value. */
    @Replicate
    public int maxHealth = 20;

    /** Falling speed threshold above which damage is inflicted to entity. */
    @Replicate
    public float fallingDamageSpeedThreshold = 20;

    /** Horizontal speed threshold above which damage is inflicted to entity. */
    @Replicate
    public float horizontalDamageSpeedThreshold = 20;

    /** The multiplier used to calculate damage when horizontal or vertical threshold is crossed. */
    @Replicate
    public float excessSpeedDamageMultiplier = 10f;


    /** The current value of health. */
    @Replicate
    @TextField
    public int currentHealth = 20;

    /** Used to send Destroy event when health breaches zero. */
    public boolean destroyEntityOnNoHealth;

    @Override
    public void copyFrom(HealthComponent other) {
        this.maxHealth = other.maxHealth;
        this.fallingDamageSpeedThreshold = other.fallingDamageSpeedThreshold;
        this.horizontalDamageSpeedThreshold = other.horizontalDamageSpeedThreshold;
        this.excessSpeedDamageMultiplier = other.excessSpeedDamageMultiplier;
        this.currentHealth = other.currentHealth;
        this.destroyEntityOnNoHealth = other.destroyEntityOnNoHealth;
    }
}
