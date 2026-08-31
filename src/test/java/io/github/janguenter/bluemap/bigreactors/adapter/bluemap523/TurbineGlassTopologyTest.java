/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.bigreactors.adapter.bluemap523;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TurbineGlassTopologyTest {

    @Test
    void allNeighborMasksProduceTheExact64StateNames() {
        Set<String> states = new HashSet<>();
        for (int connections = 0; connections < 64; connections++) {
            states.add(TurbineGlassTopology.state(connections));
        }

        assertEquals(64, states.size());
        assertEquals(GlassCatalog.STATES, states);
    }

    @Test
    void namesEveryConnectionFamilyExactly() {
        assertEquals("none", TurbineGlassTopology.state(0));
        assertEquals("face_d", TurbineGlassTopology.state(1));
        assertEquals("angle_de", TurbineGlassTopology.state(3));
        assertEquals("corner_den", TurbineGlassTopology.state(7));
        assertEquals("misc_dens", TurbineGlassTopology.state(15));
        assertEquals("opposite_du", TurbineGlassTopology.state(17));
        assertEquals("cshape_deu", TurbineGlassTopology.state(19));
        assertEquals("pipe_deuw", TurbineGlassTopology.state(51));
        assertEquals("pipeend_ensuw", TurbineGlassTopology.state(62));
        assertEquals("all", TurbineGlassTopology.state(63));
    }

    @Test
    void connectsOnlyTheExactSameTierGlass() {
        Blocks blocks = new Blocks()
                .at(1, 0, 0, GlassCatalog.BASIC)
                .at(0, 1, 0, GlassCatalog.REINFORCED);

        assertEquals(
                "face_e",
                TurbineGlassTopology.state(GlassCatalog.BASIC, blocks::id)
        );
    }

    private static final class Blocks {

        private final Map<Position, String> blocks = new HashMap<>();

        Blocks at(int x, int y, int z, String blockId) {
            blocks.put(new Position(x, y, z), blockId);
            return this;
        }

        String id(int x, int y, int z) {
            return blocks.getOrDefault(new Position(x, y, z), "minecraft:air");
        }
    }

    private record Position(int x, int y, int z) {
    }
}
