// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.components;

import com.google.common.collect.Lists;
import org.terasology.engine.audio.StaticSound;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.List;

/**
 * Contains list of damage sounds, one of which is played when entity is damaged.
 */
public class DamageSoundComponent implements Component<DamageSoundComponent> {
    public List<StaticSound> sounds = Lists.newArrayList();

    @Override
    public void copyFrom(DamageSoundComponent other) {
        this.sounds = Lists.newArrayList(other.sounds);
    }
}
