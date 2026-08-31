/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.bigreactors.adapter.bluemap523;

import de.bluecolored.bluemap.core.util.Key;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Exact turbine-glass block and state catalog for Extreme Reactors 2.4.28. */
final class GlassCatalog {

    static final String BASIC = "bigreactors:basic_turbineglass";
    static final String REINFORCED = "bigreactors:reinforced_turbineglass";
    static final List<String> BLOCK_IDS = List.of(BASIC, REINFORCED);
    static final Set<String> STATES = createStates();
    private static final Set<String> BLOCK_ID_SET = Set.copyOf(BLOCK_IDS);

    private GlassCatalog() {
    }

    static boolean owns(String blockId) {
        return BLOCK_ID_SET.contains(blockId);
    }

    static Key blockKey(String blockId) {
        return Key.parse(blockId);
    }

    private static Set<String> createStates() {
        Set<String> states = new LinkedHashSet<>();
        for (int connections = 0; connections < 64; connections++) {
            states.add(TurbineGlassTopology.state(connections));
        }
        if (states.size() != 64) {
            throw new IllegalStateException("glass topology states are not unique");
        }
        return Set.copyOf(states);
    }
}
