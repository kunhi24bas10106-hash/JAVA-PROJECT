// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.components;

import com.google.common.collect.Maps;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.Map;

public class DamageResistComponent implements Component<DamageResistComponent> {
    public Map<String, Float> damageTypes;

    @Override
    public void copyFrom(DamageResistComponent other) {
        this.damageTypes = Maps.newHashMap(other.damageTypes);
    }
}
