// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.health.systems;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.joml.Math;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.RenderSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.assets.texture.Texture;
import org.terasology.engine.rendering.assets.texture.TextureRegionAsset;
import org.terasology.engine.rendering.world.selection.BlockSelectionRenderer;
import org.terasology.engine.utilities.Assets;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.regions.BlockRegionComponent;
import org.terasology.module.health.components.HealthComponent;

import java.util.Optional;

/**
 * This system visualizes damaged blocks by rendering a damage overlay.
 * <p>
 * The system derives a damage effect level between 0 (full health / no damage) and 10 (0 health / full damage). Starting from level 1 to
 * level 10 the damage overlay effect is taken from the {@code CoreAssets:blockDamageEffects} texture atlas.
 * <p>
 * To change the default damage effects the texture can be overridden.
 */
@RegisterSystem(RegisterMode.CLIENT)
public class BlockDamageRenderer extends BaseComponentSystem implements RenderSystem {

    private BlockSelectionRenderer blockSelectionRenderer;

    @In
    private EntityManager entityManager;

    @Override
    public void renderOverlay() {
        if (blockSelectionRenderer == null) {
            Texture texture = Assets.getTextureRegion("CoreAssets:blockDamageEffects#1").get().getTexture();
            blockSelectionRenderer = new BlockSelectionRenderer(texture);
        }
        // group the entities into what texture they will use so that there is less recreating meshes (changing a
        // texture region on the BlockSelectionRenderer will recreate the mesh to use the different UV coordinates).
        Multimap<Integer, Vector3i> groupedEntitiesByEffect = ArrayListMultimap.create();

        for (EntityRef entity : entityManager.getEntitiesWith(HealthComponent.class, BlockComponent.class)) {
            HealthComponent health = entity.getComponent(HealthComponent.class);
            if (health.currentHealth == health.maxHealth) {
                continue;
            }
            BlockComponent blockComponent = entity.getComponent(BlockComponent.class);
            groupedEntitiesByEffect.put(getDamageEffectsNumber(health), blockComponent.getPosition(new Vector3i()));
        }
        for (EntityRef entity : entityManager.getEntitiesWith(HealthComponent.class, BlockRegionComponent.class)) {
            HealthComponent health = entity.getComponent(HealthComponent.class);
            if (health.currentHealth == health.maxHealth) {
                continue;
            }
            BlockRegionComponent blockRegion = entity.getComponent(BlockRegionComponent.class);
            for (Vector3ic blockPos : blockRegion.region) {
                groupedEntitiesByEffect.put(getDamageEffectsNumber(health), new Vector3i(blockPos));
            }
        }

        // Bind the texture already as we know that the texture will be the same for each block effect, just different UV coordinates.
        blockSelectionRenderer.beginRenderOverlay();

        for (Integer effectsNumber : groupedEntitiesByEffect.keySet()) {
            Optional<TextureRegionAsset> texture =
                    Assets.getTextureRegion("CoreAssets:blockDamageEffects#" + effectsNumber);
            if (texture.isPresent()) {
                blockSelectionRenderer.setEffectsTexture(texture.get());
                for (Vector3i position : groupedEntitiesByEffect.get(effectsNumber)) {
                    blockSelectionRenderer.renderMark(position);
                }
            }
        }

        blockSelectionRenderer.endRenderOverlay();
    }

    /**
     * Compute the damage effect number as linear mapping from damage percentage to the range [0..10].
     * <p>
     * A value of 0 denotes no damage, while 10 denotes a current health of 0.
     *
     * @return the effect number in [0..10] linear to damage percentage
     */
    int getDamageEffectsNumber(HealthComponent health) {
        Preconditions.checkArgument(health.currentHealth >= 0);
        Preconditions.checkArgument(health.maxHealth > 0);

        float damagePercentage = 1f - Math.clamp(0f, 1f, (float) health.currentHealth / health.maxHealth);
        return Math.round(damagePercentage * 10);
    }
}
