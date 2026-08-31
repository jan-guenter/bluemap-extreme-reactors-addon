/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.bigreactors.adapter.bluemap523;

/** Reconstructs the client-only connected-glass state from exact neighbors. */
final class TurbineGlassTopology {

    private TurbineGlassTopology() {
    }

    static String state(
            String blockId,
            RotorTopology.NeighborLookup neighbors
    ) {
        if (!GlassCatalog.owns(blockId)) {
            throw new IllegalArgumentException("unsupported turbine glass block");
        }
        int connections = 0;
        for (Direction direction : Direction.values()) {
            if (blockId.equals(neighbors.blockId(
                    direction.dx, direction.dy, direction.dz
            ))) {
                connections |= direction.bit;
            }
        }
        return state(connections);
    }

    static String state(int connections) {
        int count = Integer.bitCount(connections);
        if (count == 0) {
            return "none";
        }
        if (count == 6) {
            return "all";
        }

        String prefix = switch (count) {
            case 1 -> "face";
            case 2 -> oppositePairs(connections) == 1 ? "opposite" : "angle";
            case 3 -> oppositePairs(connections) == 1 ? "cshape" : "corner";
            case 4 -> oppositePairs(connections) == 2 ? "pipe" : "misc";
            case 5 -> "pipeend";
            default -> throw new IllegalStateException("invalid connection count");
        };
        StringBuilder state = new StringBuilder(prefix).append('_');
        for (Direction direction : Direction.values()) {
            if ((connections & direction.bit) != 0) {
                state.append(direction.serialized);
            }
        }
        return state.toString();
    }

    private static int oppositePairs(int connections) {
        int pairs = 0;
        if (connected(connections, Direction.DOWN)
                && connected(connections, Direction.UP)) {
            pairs++;
        }
        if (connected(connections, Direction.EAST)
                && connected(connections, Direction.WEST)) {
            pairs++;
        }
        if (connected(connections, Direction.NORTH)
                && connected(connections, Direction.SOUTH)) {
            pairs++;
        }
        return pairs;
    }

    private static boolean connected(int connections, Direction direction) {
        return (connections & direction.bit) != 0;
    }

    private enum Direction {
        DOWN(0, -1, 0, 'd', 1),
        EAST(1, 0, 0, 'e', 2),
        NORTH(0, 0, -1, 'n', 4),
        SOUTH(0, 0, 1, 's', 8),
        UP(0, 1, 0, 'u', 16),
        WEST(-1, 0, 0, 'w', 32);

        private final int dx;
        private final int dy;
        private final int dz;
        private final char serialized;
        private final int bit;

        Direction(int dx, int dy, int dz, char serialized, int bit) {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
            this.serialized = serialized;
            this.bit = bit;
        }
    }
}
