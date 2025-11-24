// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.module.health.core;

import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.gestalt.naming.Name;
import org.terasology.module.health.components.HealthComponent;
import org.terasology.module.health.events.BeforeRegenEvent;
import org.terasology.module.health.events.OnDamagedEvent;
import org.terasology.module.health.events.RegisterRegenEvent;

/**
 * A system adding core functionality and default behavior based on the mechanics defined in this module.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class BaseRegenAuthoritySystem extends BaseComponentSystem {

    public static final Name BASE_REGEN = new Name("health:baseRegen");

    @In
    Time time;

    /**
     * Register base regeneration action id if the {@link BaseRegenComponent} is present.
     *
     * @param entity the entity with {@link BaseRegenComponent} that was created, loaded or extended with that component
     */
    @ReceiveEvent(components = BaseRegenComponent.class)
    public void registerBaseRegen(OnActivatedComponent event, EntityRef entity) {
        entity.send(new RegisterRegenEvent(BASE_REGEN));
    }

    /**
     * Contribute to regeneration actions by listening for {@link BaseRegenAuthoritySystem#BASE_REGEN} and adding the
     * regen rate from the entity's {@link HealthComponent} to the result.
     *
     * @param event collector event for regeneration actions
     * @param entity the entity affected by the regeneration action
     * @param baseRegen the entity's base regen configuration
     */
    @ReceiveEvent
    public void addBaseRegen(BeforeRegenEvent event, EntityRef entity, BaseRegenComponent baseRegen) {
        if (event.getId().equals(BASE_REGEN)) {
            event.add(baseRegen.regenRate);
        }
    }

    /**
     * Cancel (consume) the base regen collector event if within the cool down/delay for base regeneration.
     *
     * @param event collector event for regeneration actions
     * @param entity the entity affected by the regeneration action
     * @param baseRegen the entity's base regen configuration
     */
    @ReceiveEvent
    public void preventBaseRegenDuringCoolDown(BeforeRegenEvent event, EntityRef entity, BaseRegenComponent baseRegen) {
        long currentTime = time.getGameTimeInMs();

        if (event.getId().equals(BASE_REGEN) && currentTime < baseRegen.lastHitTimestampInMs + (long) (baseRegen.waitBeforeRegen * 1000)) {
            event.consume();
        }
    }

    /**
     * Memorize the latest point in time at which the entity received damage.
     * <p>
     * This is relevant for the base regen cool down/delay.
     *
     * @param event collector event for regeneration actions
     * @param entity the entity affected by the regeneration action
     * @param baseRegen the entity's base regen configuration
     */
    //TODO: is this relevant for other systems? should this be a feature of DamageAuthoritySystem?
    @ReceiveEvent
    public void onDamaged(OnDamagedEvent event, EntityRef entity, BaseRegenComponent baseRegen) {
        baseRegen.lastHitTimestampInMs = time.getGameTimeInMs();
    }
}
