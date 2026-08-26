/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.bigreactors.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.world.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Admits and routes only the exact installed turbine-rotor resource schema. */
final class InstalledRotorResources {

    private InstalledRotorResources() {
    }

    static Admission inspect(ResourcePack pack) {
        Set<Variant> routed = Collections.newSetFromMap(new IdentityHashMap<>());
        List<Variant> visible = new ArrayList<>();
        int completedBlocks = 0;
        for (String blockId : RotorCatalog.BLOCK_IDS) {
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state =
                    pack.getBlockStates().get(RotorCatalog.blockKey(blockId));
            if (state == null || state.getVariants() == null
                    || state.getMultipart() != null) {
                return null;
            }
            List<Variant> all = new ArrayList<>();
            state.forEach(all::add);
            if (all.size() != RotorCatalog.states(blockId).size()) {
                return null;
            }
            for (String modelState : RotorCatalog.states(blockId)) {
                List<Variant> selected = select(state, blockId, modelState);
                if (selected.size() != 1) {
                    return null;
                }
                Variant variant = selected.getFirst();
                if (variant.getRenderer() != BlockRendererType.DEFAULT
                        || !validModelRoute(blockId, modelState, variant)) {
                    return null;
                }
                routed.add(variant);
                if (!"hidden".equals(modelState)) {
                    visible.add(variant);
                }
            }
            completedBlocks++;
            if (routed.size() != visible.size() + completedBlocks) {
                return null;
            }
        }
        if (routed.size() != 52 || visible.size() != 48) {
            return null;
        }
        return new Admission(List.copyOf(routed), List.copyOf(visible));
    }

    private static List<Variant> select(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state,
            String blockId,
            String modelState
    ) {
        List<Variant> selected = new ArrayList<>();
        state.forEach(
                new BlockState(
                        RotorCatalog.blockKey(blockId),
                        Map.of("state", modelState)
                ),
                0,
                0,
                0,
                selected::add
        );
        return selected;
    }

    private static boolean validModelRoute(
            String blockId,
            String modelState,
            Variant variant
    ) {
        String model = variant.getModel().getFormatted();
        if ("hidden".equals(modelState)) {
            return "bigreactors:block/transparentblock".equals(model);
        }
        String tier = blockId.contains("reinforced") ? "reinforced" : "basic";
        String component = RotorCatalog.shaft(blockId)
                ? "rotorshaft_" : "rotorblade_";
        return model.startsWith("bigreactors:block/turbine/" + tier + '/'
                + component);
    }

    static boolean bakedModelsValid(ResourcePack pack, Admission admission) {
        for (Variant variant : admission.visibleVariants()) {
            Model model = pack.getModels().get(variant.getModel());
            if (model == null || model.getElements() == null
                    || model.getElements().length == 0) {
                return false;
            }
        }
        return true;
    }

    record Admission(List<Variant> variants, List<Variant> visibleVariants) {

        Admission {
            variants = List.copyOf(variants);
            visibleVariants = List.copyOf(visibleVariants);
        }

        boolean route(BlockRendererType renderer) {
            if (variants.stream().anyMatch(
                    variant -> variant.getRenderer() != BlockRendererType.DEFAULT
            )) {
                return false;
            }
            int routed = 0;
            try {
                for (Variant variant : variants) {
                    variant.setRenderer(renderer);
                    routed++;
                }
                return true;
            } catch (RuntimeException exception) {
                for (int index = 0; index < routed; index++) {
                    variants.get(index).setRenderer(BlockRendererType.DEFAULT);
                }
                return false;
            }
        }
    }
}
