// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.module.health;

import org.junit.jupiter.api.Test;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.integrationenvironment.ModuleTestingHelper;
import org.terasology.engine.integrationenvironment.TestEventReceiver;
import org.terasology.engine.integrationenvironment.jupiter.IntegrationEnvironment;
import org.terasology.engine.logic.health.EngineDamageTypes;
import org.terasology.engine.logic.players.PlayerCharacterComponent;
import org.terasology.engine.registry.In;
import org.terasology.module.health.components.HealthComponent;
import org.terasology.module.health.events.BeforeDamagedEvent;
import org.terasology.module.health.events.DoDamageEvent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationEnvironment(dependencies = "Health")
public class DamageEventTest {
    @In
    protected EntityManager entityManager;
    @In
    protected ModuleTestingHelper helper;

    private EntityRef newPlayer(int currentHealth) {
        HealthComponent healthComponent = new HealthComponent();
        healthComponent.currentHealth = currentHealth;
        healthComponent.maxHealth = 100;

        final EntityRef player = entityManager.create();
        player.addComponent(new PlayerCharacterComponent());
        player.addComponent(healthComponent);
        return player;
    }

    @Test
    public void damageTest() {
        final EntityRef player = newPlayer(50);

        player.send(new DoDamageEvent(10));
        assertEquals(player.getComponent(HealthComponent.class).currentHealth, 40);

        player.send(new DoDamageEvent(999));
        assertEquals(player.getComponent(HealthComponent.class).currentHealth, 0);
    }

    @Test
    public void damageEventSentTest() {
        TestEventReceiver<BeforeDamagedEvent> receiver = new TestEventReceiver<>(helper.getHostContext(),
                BeforeDamagedEvent.class);
        List<BeforeDamagedEvent> list = receiver.getEvents();
        assertTrue(list.isEmpty());

        final EntityRef player = newPlayer(50);

        player.send(new DoDamageEvent(10));
        assertEquals(1, list.size());
    }

    @Test
    public void damageModifyEventTest() {
        final EntityRef player = newPlayer(50);

        EntityRef dummyInstigator = entityManager.create();

        TestEventReceiver<BeforeDamagedEvent> receiver = new TestEventReceiver<>(helper.getHostContext(),
                BeforeDamagedEvent.class,
                (event, entity) -> {
                    if (event.getInstigator().equals(dummyInstigator)) {
                        event.add(5);
                        event.multiply(2);
                    }
                });
        DoDamageEvent damageEvent = new DoDamageEvent(10, EngineDamageTypes.DIRECT.get(), dummyInstigator);
        player.send(damageEvent);
        // Expected damage value is ( initial:10 + 5 ) * 2 = 30
        // Expected health value is ( 50 - 30 ) == 20
        assertEquals(20, player.getComponent(HealthComponent.class).currentHealth);
    }

    @Test
    public void negativeDamageModifyEventTest() {
        final EntityRef player = newPlayer(50);

        EntityRef dummyInstigator = entityManager.create();

        TestEventReceiver<BeforeDamagedEvent> receiver = new TestEventReceiver<>(helper.getHostContext(),
                BeforeDamagedEvent.class,
                (event, entity) -> {
                    if (event.getInstigator().equals(dummyInstigator)) {
                        event.add(5);
                        event.multiply(-2);
                    }
                });

        DoDamageEvent damageEvent = new DoDamageEvent(10, EngineDamageTypes.DIRECT.get(), dummyInstigator);
        player.send(damageEvent);

        // Expected damage value is ( initial:10 + 5 ) * -2 = -30
        // Expected health value is ( 50 - (-30) ) == 80
        assertEquals(80, player.getComponent(HealthComponent.class).currentHealth);
    }
    
    @Test
    public void damageEventCancelTest() {
        final EntityRef player = newPlayer(50);

        TestEventReceiver<BeforeDamagedEvent> receiver = new TestEventReceiver<>(helper.getHostContext(),
                BeforeDamagedEvent.class,
                (event, entity) -> {
                    event.consume();
                });
        player.send(new DoDamageEvent(10));
        assertEquals(50, player.getComponent(HealthComponent.class).currentHealth);
    }

    @Test
    public void damageNegativeTest() {
        assertThrows(IllegalArgumentException.class, () -> new DoDamageEvent(-10));
    }

}
