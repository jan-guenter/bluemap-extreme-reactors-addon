/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.bigreactors.adapter.bluemap523;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RotorTopologyTest {

    @Test
    void isolatedShaftUsesExactVerticalFallback() {
        assertEquals(
                "y_noblades",
                RotorTopology.state(RotorCatalog.BASIC_SHAFT, empty())
        );
    }

    @Test
    void shaftCombinesPerpendicularBladeAxes() {
        Blocks blocks = new Blocks()
                .at(0, 1, 0, RotorCatalog.BASIC_SHAFT)
                .at(1, 0, 0, RotorCatalog.BASIC_BLADE)
                .at(-1, 0, 0, RotorCatalog.BASIC_BLADE)
                .at(0, 0, 1, RotorCatalog.BASIC_BLADE)
                .at(0, 0, -1, RotorCatalog.BASIC_BLADE);

        assertEquals(
                "y_xz",
                RotorTopology.state(RotorCatalog.BASIC_SHAFT, blocks::id)
        );
    }

    @Test
    void shaftInfersHorizontalRotorAxis() {
        Blocks xAxis = new Blocks()
                .at(1, 0, 0, RotorCatalog.REINFORCED_SHAFT)
                .at(0, 1, 0, RotorCatalog.REINFORCED_BLADE);
        Blocks zAxis = new Blocks()
                .at(0, 0, -1, RotorCatalog.REINFORCED_SHAFT)
                .at(-1, 0, 0, RotorCatalog.REINFORCED_BLADE);

        assertEquals(
                "x_y",
                RotorTopology.state(RotorCatalog.REINFORCED_SHAFT, xAxis::id)
        );
        assertEquals(
                "z_x",
                RotorTopology.state(RotorCatalog.REINFORCED_SHAFT, zAxis::id)
        );
    }

    @Test
    void outerBladeWalksTheFullSpanToItsShaft() {
        Blocks blocks = new Blocks()
                .at(-1, 0, 0, RotorCatalog.BASIC_BLADE)
                .at(-2, 0, 0, RotorCatalog.BASIC_SHAFT)
                .at(-2, 0, -1, RotorCatalog.BASIC_SHAFT);

        assertEquals(
                "z_x_pos",
                RotorTopology.state(RotorCatalog.BASIC_BLADE, blocks::id)
        );
    }

    @Test
    void bladeSignPointsAwayFromTheShaft() {
        Blocks blocks = new Blocks()
                .at(1, 0, 0, RotorCatalog.REINFORCED_SHAFT)
                .at(1, 0, 1, RotorCatalog.REINFORCED_SHAFT);

        assertEquals(
                "z_x_neg",
                RotorTopology.state(RotorCatalog.REINFORCED_BLADE, blocks::id)
        );
    }

    @Test
    void missingMatchingTierFallsBackWithoutCrossTierGuessing() {
        Blocks blocks = new Blocks()
                .at(1, 0, 0, RotorCatalog.REINFORCED_SHAFT)
                .at(-1, 0, 0, RotorCatalog.REINFORCED_BLADE);

        assertEquals(
                "z_x_pos",
                RotorTopology.state(RotorCatalog.BASIC_BLADE, blocks::id)
        );
    }

    private static RotorTopology.NeighborLookup empty() {
        return (x, y, z) -> "minecraft:air";
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
