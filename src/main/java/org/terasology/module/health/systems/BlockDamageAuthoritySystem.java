// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.systems;

import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.Priority;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.NetFilterEvent;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.events.AttackEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.utilities.random.FastRandom;
import org.terasology.engine.utilities.random.Random;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.entity.damage.BlockDamageModifierComponent;
import org.terasology.engine.world.block.family.BlockFamily;
import org.terasology.engine.world.block.regions.ActAsBlockComponent;
import org.terasology.engine.world.block.tiles.WorldAtlas;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.module.health.components.BlockDamagedComponent;
import org.terasology.module.health.components.DamageSoundComponent;
import org.terasology.module.health.components.HealthComponent;
import org.terasology.module.health.core.BaseRegenComponent;
import org.terasology.module.health.events.BeforeDamagedEvent;
import org.terasology.module.health.events.OnDamagedEvent;
import org.terasology.module.health.events.OnFullyHealedEvent;

/**
 * This system is responsible for giving blocks health when they are attacked and
 * damaging them instead of destroying them.
 */
@RegisterSystem
public class BlockDamageAuthoritySystem extends BaseComponentSystem {
    private static final float BLOCK_REGEN_SECONDS = 4.0f;

    @In
    private EntityManager entityManager;

    @In
    private WorldAtlas worldAtlas;

    private Random random = new FastRandom();

    /** Consumes damage event if block is indestructible. */
    @Priority(EventPriority.PRIORITY_HIGH)
    @ReceiveEvent
    public void beforeDamaged(BeforeDamagedEvent event, EntityRef blockEntity, BlockComponent blockComp) {
        if (!blockComp.getBlock().isDestructible()) {
            event.consume();
        }
    }

    /** Consumes damage event if entity acting as block is indestructible. */
    @Priority(EventPriority.PRIORITY_HIGH)
    @ReceiveEvent
    public void beforeDamaged(BeforeDamagedEvent event, EntityRef blockEntity, ActAsBlockComponent blockComp) {
        if (blockComp.block != null && !blockComp.block.getArchetypeBlock().isDestructible()) {
            event.consume();
        }
    }

    /**
     * Removes the {@link BlockDamagedComponent} marker component when a block or block-like entity is fully healed.
     *
     * @param event the notification event that an entity was fully healed.
     * @param entity Block entity the (potential) block or block-like entity
     */
    @ReceiveEvent(components = BlockDamagedComponent.class)
    public void onRepaired(OnFullyHealedEvent event, EntityRef entity) {
        //TODO: should this be split into two handlers for BlockComponent and ActAsBlockComponent?
        entity.removeComponent(BlockDamagedComponent.class);
    }

    /** Adds the {@link BlockDamagedComponent} marker component to block which is damaged. */
    @ReceiveEvent
    public void onDamaged(OnDamagedEvent event, EntityRef blockEntity, BlockComponent blockComponent, LocationComponent locComp) {
        if (!blockEntity.hasComponent(BlockDamagedComponent.class)) {
            blockEntity.addComponent(new BlockDamagedComponent());
        }
    }

    @ReceiveEvent
    public void onDamaged(OnDamagedEvent event, EntityRef entity, ActAsBlockComponent blockComponent, LocationComponent locComp) {
        //TODO: add the marker component also for block-like entities (does not do anything, but all "damaged blocks" are marked with that
        //      component then.
    }

    @NetFilterEvent(netFilter = RegisterMode.AUTHORITY)
    @ReceiveEvent
    public void beforeDamage(BeforeDamagedEvent event, EntityRef entity, BlockComponent blockComp) {
        beforeDamageCommon(event, blockComp.getBlock());
    }

    @NetFilterEvent(netFilter = RegisterMode.AUTHORITY)
    @ReceiveEvent
    public void beforeDamage(BeforeDamagedEvent event, EntityRef entity, ActAsBlockComponent blockComp) {
        if (blockComp.block != null) {
            beforeDamageCommon(event, blockComp.block.getArchetypeBlock());
        }
    }

    private void beforeDamageCommon(BeforeDamagedEvent event, Block block) {
        if (event.getDamageType() != null) {
            BlockDamageModifierComponent blockDamage = event.getDamageType().getComponent(BlockDamageModifierComponent.class);
            if (blockDamage != null) {
                BlockFamily blockFamily = block.getBlockFamily();
                for (String category : blockFamily.getCategories()) {
                    if (blockDamage.materialDamageMultiplier.containsKey(category)) {
                        event.multiply(blockDamage.materialDamageMultiplier.get(category));
                    }
                }
            }
        }
    }

    /** Causes damage to block without health component, leads to adding health component to the block. */
    @NetFilterEvent(netFilter = RegisterMode.AUTHORITY)
    @ReceiveEvent
    public void onAttackHealthlessBlock(AttackEvent event, EntityRef targetEntity, BlockComponent blockComponent) {
        if (!targetEntity.hasComponent(HealthComponent.class)) {
            DamageAuthoritySystem.damageEntity(event, targetEntity);
        }
    }

    @NetFilterEvent(netFilter = RegisterMode.AUTHORITY)
    @ReceiveEvent
    public void onAttackHealthlessActAsBlock(AttackEvent event, EntityRef targetEntity, ActAsBlockComponent actAsBlockComponent) {
        if (!targetEntity.hasComponent(HealthComponent.class)) {
            DamageAuthoritySystem.damageEntity(event, targetEntity);
        }
    }

    /**
     * Adds health component to blocks when damaged.
     */
    @ReceiveEvent
    public void beforeDamagedEnsureHealthPresent(BeforeDamagedEvent event, EntityRef blockEntity,
                                                 BlockComponent blockComponent) {
        Block type = blockComponent.getBlock();
        if (type.isDestructible()) {

            if (!blockEntity.hasComponent(HealthComponent.class)) {
                HealthComponent healthComponent = new HealthComponent();
                healthComponent.maxHealth = type.getHardness();
                healthComponent.currentHealth = type.getHardness();
                healthComponent.destroyEntityOnNoHealth = true;

                blockEntity.addComponent(healthComponent);
            }

            if (!blockEntity.hasComponent(BaseRegenComponent.class)) {
                BaseRegenComponent baseRegenComponent = new BaseRegenComponent();
                baseRegenComponent.regenRate = type.getHardness() / BLOCK_REGEN_SECONDS;
                baseRegenComponent.waitBeforeRegen = 1f;

                blockEntity.addComponent(baseRegenComponent);
            }

            // Give the block entity a damage sound component to make the default damage authority system play sound
            // effects when a block is damaged.
            if (!blockEntity.hasComponent(DamageSoundComponent.class)) {
                BlockFamily blockFamily = type.getBlockFamily();

                DamageSoundComponent damageSounds = new DamageSoundComponent();
                damageSounds.sounds.addAll(blockFamily.getArchetypeBlock().getSounds().getDigSounds());

                blockEntity.addComponent(damageSounds);
            }
        }
    }
}
