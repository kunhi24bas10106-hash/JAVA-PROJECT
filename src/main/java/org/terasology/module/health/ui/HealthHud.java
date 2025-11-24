// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.ui;

import org.terasology.engine.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.module.health.components.HealthComponent;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.widgets.UIIconBar;
import org.terasology.engine.registry.In;

public class HealthHud extends CoreHudWidget {

    @In
    private LocalPlayer localPlayer;

    @Override
    public void initialise() {
        UIIconBar healthBar = find("healthBar", UIIconBar.class);
        healthBar.bindValue(new ReadOnlyBinding<Float>() {
            @Override
            public Float get() {
                HealthComponent healthComponent = localPlayer.getCharacterEntity().getComponent(HealthComponent.class);
                if (healthComponent != null) {
                    return (float) healthComponent.currentHealth;
                }
                return 0f;
            }
        });
        healthBar.bindMaxValue(new ReadOnlyBinding<Float>() {
            @Override
            public Float get() {
                HealthComponent healthComponent = localPlayer.getCharacterEntity().getComponent(HealthComponent.class);
                if (healthComponent != null) {
                    return (float) healthComponent.maxHealth;
                }
                return 0f;
            }
        });
    }
}
