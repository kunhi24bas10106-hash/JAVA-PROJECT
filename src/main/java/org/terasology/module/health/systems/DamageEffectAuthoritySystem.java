// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.module.health.systems;

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.terasology.engine.entitySystem.entity.EntityBuilder;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.particles.components.ParticleDataSpriteComponent;
import org.terasology.engine.particles.components.generators.TextureOffsetGeneratorComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.assets.texture.Texture;
import org.terasology.engine.utilities.Assets;
import org.terasology.engine.utilities.random.FastRandom;
import org.terasology.engine.utilities.random.Random;
import org.terasology.engine.world.block.BlockAppearance;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.BlockPart;
import org.terasology.engine.world.block.entity.damage.BlockDamageModifierComponent;
import org.terasology.engine.world.block.family.BlockFamily;
import org.terasology.engine.world.block.regions.ActAsBlockComponent;
import org.terasology.engine.world.block.tiles.WorldAtlas;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.math.TeraMath;
import org.terasology.module.health.events.OnDamagedEvent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A server-side system to create particle effect entities for damage effects.
 * <p>
 * Particle emitter entities can be created server-side and replicated to clients by having the
 * {@link org.terasology.engine.network.NetworkComponent NetworkComponent}. The actual rendering of particles happens on client-side.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class DamageEffectAuthoritySystem extends BaseComponentSystem {

    @In
    private EntityManager entityManager;
    @In
    private WorldAtlas worldAtlas;

    private Random random = new FastRandom();

    /**
     * Show a particle effect for the damaged block based on the block's texture.
     *
     * @param event the notification event for the damaged block
     * @param blockEntity the entity representing the damaged block
     * @param blockComponent
     */
    @ReceiveEvent
    public void onDamaged(OnDamagedEvent event, EntityRef blockEntity, BlockComponent blockComponent) {
        //TODO: the BlockDamageModifierComponent also holds a modifier for `impulsePower` - should that influence the particle effect?
        if (isEffectsEnabled(event)) {
            createBlockParticleEffect(blockComponent.getBlock().getBlockFamily(), new Vector3f(blockComponent.getPosition()));
        }
    }

    /**
     * Show a particle effect for the damaged block-like entity based on the block's texture.
     *
     * @param event the notification event for the damaged block
     * @param entity the entity acting as a block
     * @param blockComponent
     * @param location the location of the block-like entity
     */
    @ReceiveEvent
    public void onDamaged(OnDamagedEvent event, EntityRef entity, ActAsBlockComponent blockComponent, LocationComponent location) {
        if (blockComponent.block != null && isEffectsEnabled(event)) {
            //TODO: the BlockDamageModifierComponent also holds a modifier for `impulsePower` - should that influence the particle effect?
            createBlockParticleEffect(blockComponent.block, location.getWorldPosition(new Vector3f()));
        }
    }

    /**
     * Whether per-block effects should be shown or not.
     * <p>
     * Each damage event may reference a prefab describing the type of damage inflicted. This damage type may have a
     * {@link BlockDamageModifierComponent} denoting that block effects should be skipped for this damage event.
     *
     * @param event the event describing the inflicted damage
     * @return true if particle effects should be shown, false otherwise
     */
    private boolean isEffectsEnabled(OnDamagedEvent event) {
        BlockDamageModifierComponent blockDamageSettings = event.getType().getComponent(BlockDamageModifierComponent.class);
        if (blockDamageSettings != null) {
            return !blockDamageSettings.skipPerBlockEffects;
        }
        return true;
    }

    /**
     * Computes n random offset values for each block part texture.
     *
     * @param blockAppearance the block appearance information to generate offsets from
     * @param scale the scale of the texture area (should be in 0 < scale <= 1.0)
     *
     * @return a list of random offsets sampled from all block parts
     */
    private List<Vector2f> computeOffsets(BlockAppearance blockAppearance, float scale) {
        final float relativeTileSize = worldAtlas.getRelativeTileSize();
        final int absoluteTileSize = worldAtlas.getTileSize();
        final float pixelSize = relativeTileSize / absoluteTileSize;
        final int spriteWidth = TeraMath.ceilToInt(scale * absoluteTileSize);

        final Stream<Vector2fc> baseOffsets =
                BlockPart.allParts().stream().map(blockAppearance::getTextureAtlasPos);

        return baseOffsets.flatMap(baseOffset ->
                IntStream.range(0, 8).boxed()
                        .map(i ->
                                new Vector2f(baseOffset)
                                        .add(random.nextInt(absoluteTileSize - spriteWidth) * pixelSize,
                                                random.nextInt(absoluteTileSize - spriteWidth) * pixelSize)
                        )
        ).collect(Collectors.toList());
    }

    /**
     * Creates a new entity for the block damage particle effect.
     *
     * If the terrain texture of the damaged block is available, the particles will have the block texture. Otherwise,
     * the default sprite (smoke) is used.
     *
     * @param family the {@link BlockFamily} of the damaged block
     * @param location the location of the damaged block
     */
    private void createBlockParticleEffect(BlockFamily family, Vector3fc location) {
        EntityBuilder builder = entityManager.newBuilder("CoreAssets:defaultBlockParticles");
        builder.getComponent(LocationComponent.class).setWorldPosition(location);

        Optional<Texture> terrainTexture = Assets.getTexture("engine:terrain");
        if (terrainTexture.isPresent() && terrainTexture.get().isLoaded()) {
            final BlockAppearance blockAppearance = family.getArchetypeBlock().getPrimaryAppearance();

            final float relativeTileSize = worldAtlas.getRelativeTileSize();
            final float particleScale = 0.25f;

            final float spriteSize = relativeTileSize * particleScale;

            ParticleDataSpriteComponent spriteComponent = builder.getComponent(ParticleDataSpriteComponent.class);
            spriteComponent.texture = terrainTexture.get();
            spriteComponent.textureSize.set(spriteSize, spriteSize);

            final List<Vector2f> offsets = computeOffsets(blockAppearance, particleScale);

            TextureOffsetGeneratorComponent textureOffsetGeneratorComponent = builder.getComponent(TextureOffsetGeneratorComponent.class);
            textureOffsetGeneratorComponent.validOffsets.addAll(offsets);
        }

        builder.build();
    }
}
