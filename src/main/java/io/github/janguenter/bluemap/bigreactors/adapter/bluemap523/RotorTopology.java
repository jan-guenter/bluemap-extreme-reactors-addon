/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.bigreactors.adapter.bluemap523;

import java.util.EnumSet;

/** Reconstructs the client-only rotor model state from stable neighboring blocks. */
final class RotorTopology {

    private static final int MAX_BLADE_SPAN = 64;

    private RotorTopology() {
    }

    static String state(String blockId, NeighborLookup neighbors) {
        if (!RotorCatalog.owns(blockId)) {
            throw new IllegalArgumentException("unsupported rotor block");
        }
        return RotorCatalog.shaft(blockId)
                ? shaftState(blockId, neighbors, 0, 0, 0)
                : bladeState(blockId, neighbors);
    }

    private static String shaftState(
            String blockId,
            NeighborLookup neighbors,
            int originX,
            int originY,
            int originZ
    ) {
        String shaftId = RotorCatalog.shaftFor(blockId);
        Direction rotorDirection = Direction.UP;
        for (Direction direction : Direction.values()) {
            if (shaftId.equals(idAt(
                    neighbors, originX, originY, originZ, direction, 1
            ))) {
                rotorDirection = direction;
                break;
            }
        }

        String bladeId = RotorCatalog.bladeFor(blockId);
        EnumSet<Axis> bladeAxes = EnumSet.noneOf(Axis.class);
        for (Direction direction : Direction.values()) {
            if (direction.axis != rotorDirection.axis
                    && bladeId.equals(idAt(
                            neighbors, originX, originY, originZ, direction, 1
                    ))) {
                bladeAxes.add(direction.axis);
            }
        }
        return shaftState(rotorDirection.axis, bladeAxes);
    }

    private static String bladeState(String blockId, NeighborLookup neighbors) {
        String shaftId = RotorCatalog.shaftFor(blockId);
        for (Direction direction : Direction.values()) {
            if (shaftId.equals(idAt(neighbors, 0, 0, 0, direction, 1))) {
                String shaft = shaftState(
                        shaftId,
                        neighbors,
                        direction.dx,
                        direction.dy,
                        direction.dz
                );
                return bladeState(shaft, direction);
            }
        }

        String bladeId = RotorCatalog.bladeFor(blockId);
        for (Direction direction : Direction.values()) {
            if (!bladeId.equals(idAt(neighbors, 0, 0, 0, direction, 1))) {
                continue;
            }
            for (int step = 2; step <= MAX_BLADE_SPAN; step++) {
                String candidate = idAt(neighbors, 0, 0, 0, direction, step);
                if (shaftId.equals(candidate)) {
                    String shaft = shaftState(
                            shaftId,
                            neighbors,
                            direction.dx * step,
                            direction.dy * step,
                            direction.dz * step
                    );
                    return bladeState(shaft, direction);
                }
                if (!bladeId.equals(candidate)) {
                    break;
                }
            }
        }
        return "z_x_pos";
    }

    private static String shaftState(Axis rotorAxis, EnumSet<Axis> bladeAxes) {
        String suffix;
        switch (rotorAxis) {
            case X -> suffix = axes(bladeAxes, Axis.Y, Axis.Z);
            case Y -> suffix = axes(bladeAxes, Axis.X, Axis.Z);
            case Z -> suffix = axes(bladeAxes, Axis.X, Axis.Y);
            default -> throw new IllegalStateException("unknown rotor axis");
        }
        return rotorAxis.serialized + '_' + suffix;
    }

    private static String axes(EnumSet<Axis> axes, Axis first, Axis second) {
        if (axes.contains(first) && axes.contains(second)) {
            return first.serialized + second.serialized;
        }
        if (axes.contains(first)) {
            return first.serialized;
        }
        if (axes.contains(second)) {
            return second.serialized;
        }
        return "noblades";
    }

    private static String bladeState(String shaftState, Direction towardShaft) {
        String suffix = towardShaft.positive ? "neg" : "pos";
        return shaftState.charAt(0) + "_" + towardShaft.axis.serialized + '_'
                + suffix;
    }

    private static String idAt(
            NeighborLookup neighbors,
            int originX,
            int originY,
            int originZ,
            Direction direction,
            int step
    ) {
        return neighbors.blockId(
                originX + direction.dx * step,
                originY + direction.dy * step,
                originZ + direction.dz * step
        );
    }

    @FunctionalInterface
    interface NeighborLookup {
        String blockId(int x, int y, int z);
    }

    private enum Axis {
        X("x"),
        Y("y"),
        Z("z");

        private final String serialized;

        Axis(String serialized) {
            this.serialized = serialized;
        }
    }

    private enum Direction {
        UP(0, 1, 0, Axis.Y, true),
        DOWN(0, -1, 0, Axis.Y, false),
        NORTH(0, 0, -1, Axis.Z, false),
        SOUTH(0, 0, 1, Axis.Z, true),
        WEST(-1, 0, 0, Axis.X, false),
        EAST(1, 0, 0, Axis.X, true);

        private final int dx;
        private final int dy;
        private final int dz;
        private final Axis axis;
        private final boolean positive;

        Direction(int dx, int dy, int dz, Axis axis, boolean positive) {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
            this.axis = axis;
            this.positive = positive;
        }
    }
}
