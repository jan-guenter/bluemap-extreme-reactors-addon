/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.bigreactors.adapter.bluemap522;

import de.bluecolored.bluemap.core.util.Key;

import java.util.List;
import java.util.Set;

/** Exact rotor block and state catalog for Extreme Reactors 1.21.1-2.4.28. */
final class RotorCatalog {

    static final String BASIC_SHAFT = "bigreactors:basic_turbinerotorshaft";
    static final String BASIC_BLADE = "bigreactors:basic_turbinerotorblade";
    static final String REINFORCED_SHAFT =
            "bigreactors:reinforced_turbinerotorshaft";
    static final String REINFORCED_BLADE =
            "bigreactors:reinforced_turbinerotorblade";
    static final List<String> BLOCK_IDS = List.of(
            BASIC_SHAFT,
            BASIC_BLADE,
            REINFORCED_SHAFT,
            REINFORCED_BLADE
    );
    static final Set<String> SHAFT_STATES = Set.of(
            "hidden",
            "x_noblades", "x_y", "x_z", "x_yz",
            "y_noblades", "y_x", "y_z", "y_xz",
            "z_noblades", "z_x", "z_y", "z_xy"
    );
    static final Set<String> BLADE_STATES = Set.of(
            "hidden",
            "x_y_neg", "x_y_pos", "x_z_neg", "x_z_pos",
            "y_x_neg", "y_x_pos", "y_z_neg", "y_z_pos",
            "z_x_neg", "z_x_pos", "z_y_neg", "z_y_pos"
    );
    private static final Set<String> BLOCK_ID_SET = Set.copyOf(BLOCK_IDS);

    private RotorCatalog() {
    }

    static boolean owns(String blockId) {
        return BLOCK_ID_SET.contains(blockId);
    }

    static boolean shaft(String blockId) {
        return BASIC_SHAFT.equals(blockId) || REINFORCED_SHAFT.equals(blockId);
    }

    static String shaftFor(String blockId) {
        return blockId.startsWith("bigreactors:reinforced_")
                ? REINFORCED_SHAFT : BASIC_SHAFT;
    }

    static String bladeFor(String blockId) {
        return blockId.startsWith("bigreactors:reinforced_")
                ? REINFORCED_BLADE : BASIC_BLADE;
    }

    static Set<String> states(String blockId) {
        return shaft(blockId) ? SHAFT_STATES : BLADE_STATES;
    }

    static Key blockKey(String blockId) {
        return Key.parse(blockId);
    }
}
