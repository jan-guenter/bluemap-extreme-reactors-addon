/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.bigreactors.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.MaxCapacityReachedException;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.ResourceModelRenderer;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import io.github.janguenter.bluemap.bigreactors.activation.AddonRuntime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Restores assembled turbine rotors in a deterministic stopped pose. */
final class TurbineRotorRenderer implements BlockRenderer {

    private final ResourcePack resourcePack;
    private final ResourceModelRenderer resources;
    private final AddonRuntime runtime;
    private final Map<ModelKey, Variant> variants = new ConcurrentHashMap<>();

    TurbineRotorRenderer(
            ResourcePack resourcePack,
            TextureGallery textures,
            RenderSettings settings,
            AddonRuntime runtime
    ) {
        this.resourcePack = resourcePack;
        this.resources = new ResourceModelRenderer(resourcePack, textures, settings);
        this.runtime = runtime;
    }

    @Override
    public void render(
            BlockNeighborhood block,
            Variant fallback,
            TileModelView target,
            Color mapColor
    ) {
        if (!runtime.active()) {
            resources.render(block, fallback, target, mapColor);
            return;
        }

        int start = target.getStart();
        Color initialMapColor = new Color().set(mapColor);
        try {
            String blockId = block.getBlockState().getId().getFormatted();
            if (!RotorCatalog.owns(blockId)
                    || fallback.getRenderer() != BlueMap522Adapter.renderer()) {
                resources.render(block, fallback, target, mapColor);
                return;
            }
            String state = RotorTopology.state(
                    blockId,
                    (x, y, z) -> block.getNeighborBlock(x, y, z)
                            .getBlockState().getId().getFormatted()
            );
            resources.render(
                    block,
                    variant(blockId, state),
                    target,
                    mapColor
            );
        } catch (MaxCapacityReachedException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            target.getTileModel().reset(start);
            target.initialize(start);
            mapColor.set(initialMapColor);
            runtime.inactive("turbine-rotor-renderer-"
                    + exception.getClass().getSimpleName());
            resources.render(block, fallback, target, mapColor);
        }
    }

    private Variant variant(String blockId, String state) {
        return variants.computeIfAbsent(
                new ModelKey(blockId, state),
                key -> select(key.blockId(), key.state())
        );
    }

    private Variant select(String blockId, String state) {
        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState
                definition = resourcePack.getBlockStates().get(
                        RotorCatalog.blockKey(blockId)
                );
        if (definition == null) {
            throw new IllegalStateException("rotor blockstate disappeared");
        }
        List<Variant> selected = new ArrayList<>();
        definition.forEach(
                new BlockState(
                        RotorCatalog.blockKey(blockId),
                        Map.of("state", state)
                ),
                0,
                0,
                0,
                selected::add
        );
        if (selected.size() != 1) {
            throw new IllegalStateException("rotor state no longer selects one model");
        }
        return selected.getFirst();
    }

    private record ModelKey(String blockId, String state) {
    }
}
