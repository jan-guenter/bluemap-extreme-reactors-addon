/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.bigreactors.adapter.bluemap523;

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

/** Admits and routes the exact installed rotor and turbine-glass resources. */
final class InstalledRotorResources {

    private InstalledRotorResources() {
    }

    static Admission inspect(ResourcePack pack) {
        Set<Variant> routed = Collections.newSetFromMap(new IdentityHashMap<>());
        List<Variant> visible = new ArrayList<>();
        if (!inspectRotors(pack, routed, visible)
                || !inspectGlass(pack, routed, visible)) {
            return null;
        }
        if (routed.size() != 180 || visible.size() != 176) {
            return null;
        }
        return new Admission(List.copyOf(routed), List.copyOf(visible));
    }

    private static boolean inspectRotors(
            ResourcePack pack,
            Set<Variant> routed,
            List<Variant> visible
    ) {
        for (String blockId : RotorCatalog.BLOCK_IDS) {
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state =
                    pack.getBlockStates().get(RotorCatalog.blockKey(blockId));
            if (state == null || state.getVariants() == null
                    || state.getMultipart() != null) {
                return false;
            }
            List<Variant> all = new ArrayList<>();
            state.forEach(all::add);
            if (all.size() != RotorCatalog.states(blockId).size()) {
                return false;
            }
            for (String modelState : RotorCatalog.states(blockId)) {
                List<Variant> selected = select(
                        state,
                        RotorCatalog.blockKey(blockId),
                        "state",
                        modelState
                );
                if (selected.size() != 1) {
                    return false;
                }
                Variant variant = selected.getFirst();
                if (variant.getRenderer() != BlockRendererType.DEFAULT
                        || !validModelRoute(blockId, modelState, variant)) {
                    return false;
                }
                if (!routed.add(variant)) {
                    return false;
                }
                if (!"hidden".equals(modelState)) {
                    visible.add(variant);
                }
            }
        }
        return routed.size() == 52 && visible.size() == 48;
    }

    private static boolean inspectGlass(
            ResourcePack pack,
            Set<Variant> routed,
            List<Variant> visible
    ) {
        for (String blockId : GlassCatalog.BLOCK_IDS) {
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state =
                    pack.getBlockStates().get(GlassCatalog.blockKey(blockId));
            if (state == null || state.getVariants() == null
                    || state.getMultipart() != null) {
                return false;
            }
            List<Variant> all = new ArrayList<>();
            state.forEach(all::add);
            if (all.size() != GlassCatalog.STATES.size()) {
                return false;
            }
            for (String modelState : GlassCatalog.STATES) {
                List<Variant> selected = select(
                        state,
                        GlassCatalog.blockKey(blockId),
                        "facings",
                        modelState
                );
                if (selected.size() != 1) {
                    return false;
                }
                Variant variant = selected.getFirst();
                if (variant.getRenderer() != BlockRendererType.DEFAULT
                        || !validGlassModelRoute(blockId, variant)
                        || !routed.add(variant)) {
                    return false;
                }
                visible.add(variant);
            }
        }
        return routed.size() == 180 && visible.size() == 176;
    }

    private static List<Variant> select(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state,
            de.bluecolored.bluemap.core.util.Key blockKey,
            String property,
            String modelState
    ) {
        List<Variant> selected = new ArrayList<>();
        state.forEach(
                new BlockState(
                        blockKey,
                        Map.of(property, modelState)
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

    private static boolean validGlassModelRoute(
            String blockId,
            Variant variant
    ) {
        String tier = blockId.contains("reinforced") ? "reinforced" : "basic";
        return variant.getModel().getFormatted().startsWith(
                "bigreactors:block/turbine/" + tier + "/glass_"
        );
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
