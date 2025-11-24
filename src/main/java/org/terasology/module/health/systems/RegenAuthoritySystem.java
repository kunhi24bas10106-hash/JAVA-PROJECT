// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.systems;

import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.gestalt.naming.Name;
import org.terasology.module.health.components.HealthComponent;
import org.terasology.module.health.components.RegenComponent;
import org.terasology.module.health.events.BeforeRegenEvent;
import org.terasology.module.health.events.DeregisterRegenEvent;
import org.terasology.module.health.events.RegisterRegenEvent;
import org.terasology.module.health.time.Instant;

/**
 * This system handles the natural regeneration of entities with HealthComponent.
 * <p>
 * Regeneration is applied once every second (every 1000ms) per {@link RegenComponent}. The active components are
 * checked five times per second (every 200ms) whether they are due for application.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class RegenAuthoritySystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    public static final String BASE_REGEN = "baseRegen";

    @In
    EntityManager entityManager;

    @In
    Time time;

    /**
     * The time delta in seconds elapsed since the last regeneration tick.
     * <p>
     * This is required as the sampling rate of applying regeneration effects is usually lower than the tick rate of the
     * game.
     */
    float regenTick;

    @Override
    public void update(float delta) {
        //TODO: bring back to optimization to update each entity only once per second, but scan for
        //      entities to regenerate every 200ms
        regenTick += delta;
        Instant currentTime = Instant.fromMillis(time.getGameTimeInMs());

        if (regenTick > 0.2f) {
            entityManager.getEntitiesWith(HealthComponent.class, RegenComponent.class).forEach(entity -> {
                RegenComponent regen = entity.getComponent(RegenComponent.class);
                HealthComponent health = entity.getComponent(HealthComponent.class);

                applyRegeneration(entity, regen, health, regenTick);
                // remove expired regen actions
                regen.actions.values().removeIf(endTime -> endTime.isBefore(currentTime));
            });

            regenTick = 0f;
        }
    }

    /**
     * Send out <i>collector events</i> ({@link BeforeRegenEvent}) for all registered regeneration ids and apply the
     * resulting amount to the entity's health component.
     * <p>
     * The final amount is adjusted by the time {@code delta}.
     *
     * <pre>
     *     δ × ∑ max(BeforeRegenValue(id), 0) ∀ registered id
     * </pre>
     *
     * @param entity the entity targeted by the regeneration action
     * @param regen the entity's regen component tracking registered regeneration ids
     * @param health the entity's health component
     * @param delta the time delta to adjust the regeneration amount for
     */
    private void applyRegeneration(EntityRef entity, RegenComponent regen, HealthComponent health, float delta) {
        float collectedRegenValue = regen.actions.keySet().stream()
                .map(actionId -> collectRegenValue(entity, actionId))
                .filter(event -> !event.isConsumed())
                .reduce(0f, (accumulator, event) -> accumulator + event.getResultValue(), Float::sum);

        // compute the time-adjusted regeneration amount and update the entity's health component
        //TODO: HealthComponent should probably track currentHealth as floating point, which would simplify this a lot!
        float fullRegenAmount = collectedRegenValue * delta + regen.remainder;
        regen.remainder = fullRegenAmount % 1;
        int regenAmount = (int) fullRegenAmount;
        if (regenAmount > 0) {
            RestorationAuthoritySystem.restore(entity, health, regenAmount);
        }
    }

    /**
     * Send out a {@link BeforeRegenEvent} <i>collector event</i> to ask systems for collaboration on determining the
     * regeneration value.
     *
     * @param entity the entity the regeneration action affects
     * @param id the regeneration id to collect the current amount for
     * @return the collector event after event processing
     */
    private BeforeRegenEvent collectRegenValue(EntityRef entity, Name id) {
        BeforeRegenEvent beforeRegenEvent = new BeforeRegenEvent(id, 0);
        entity.send(beforeRegenEvent);
        return beforeRegenEvent;
    }

    @ReceiveEvent(components = HealthComponent.class)
    public void onRegenActivated(RegisterRegenEvent event, EntityRef entity) {
        Instant currentTime = Instant.fromMillis(time.getGameTimeInMs());
        Instant endTime = currentTime.plus(event.duration);

        entity.upsertComponent(RegenComponent.class, regenComponent -> {
            RegenComponent regen = regenComponent.orElse(new RegenComponent());
            regen.actions.merge(event.id, endTime, Instant::max);
            return regen;
        });
    }

    @ReceiveEvent
    public void onRegenDeactivated(DeregisterRegenEvent event, EntityRef entity, RegenComponent regen) {
        regen.actions.remove(event.id);

        if (regen.actions.isEmpty()) {
            entity.removeComponent(RegenComponent.class);
        } else {
            entity.saveComponent(regen);
        }
    }
}
