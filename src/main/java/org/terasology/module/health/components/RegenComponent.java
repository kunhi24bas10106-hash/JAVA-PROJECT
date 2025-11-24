// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.components;

import com.google.common.collect.Maps;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gestalt.naming.Name;
import org.terasology.module.health.events.DeregisterRegenEvent;
import org.terasology.module.health.events.RegisterRegenEvent;
import org.terasology.module.health.time.Instant;

import java.util.HashMap;
import java.util.Map;

/**
 * [INTERNAL] This component is managed by {@link org.terasology.module.health.systems.RegenAuthoritySystem}.
 *
 * <p>
 * To register an indefinite regeneration action in a prefab you can give it a regen component and pre-fill the
 * registered actions with the respective id. To denote an indefinite timestamp, i.e., a never ending action, use a
 * negative value (usually {@code -1}).
 * <p>
 * It is recommended to format regeneration ids as follows:
 * <pre>{@code
 *  <module>:<cause>Regen
 * }</pre>
 * Append a unique suffix in case multiple regeneration actions with the same cause should be registered.
 * <p>
 * To pre-register an indefinite regeneration specified in the "gooey" module and caused by magic, for instance, include
 * the following in your prefab:
 *
 * <pre>
 * {
 *   "Regen": {
 *     "actions": [
 *       { "key": "gooey:magicRegen", "value": -1 }
 *     ]
 *   }
 * }
 * </pre>
 *
 * @see RegisterRegenEvent
 * @see DeregisterRegenEvent
 */
public class RegenComponent implements Component<RegenComponent> {
    /**
     * The decimal place of the last regen tick before rounding the amount to integer.
     * <p>
     * This is to compensate for inaccuracy when computing the regeneration amount in integers. The remainder is picked
     * up at the next iteration of the {@link org.terasology.module.health.systems.RegenAuthoritySystem}.
     */
    @Replicate
    public float remainder;

    /**
     * Registered regeneration action ids and their expiration timestamp.
     */
    @Replicate
    public Map<Name, Instant> actions = new HashMap<>();

    @Override
    public void copyFrom(RegenComponent other) {
        this.remainder = other.remainder;
        this.actions = Maps.newHashMap(other.actions);
    }
}
