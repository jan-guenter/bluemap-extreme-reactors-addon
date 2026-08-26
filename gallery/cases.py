#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Bounded, assembled basic and reinforced turbine fixtures."""

from __future__ import annotations

from dataclasses import dataclass


NAMESPACE = "bigreactors_gallery"
ENVELOPE = (164, 99, 162, 184, 105, 172)


@dataclass(frozen=True)
class Placement:
    case_id: str
    label: str
    x: int
    y: int
    z: int
    block_state: str
    expected: str


def turbine_fixture(prefix: str, tier: str, minimum_x: int) -> list[Placement]:
    """Create one valid 5x5x5 turbine with visible rotor and coil planes."""
    minimum_y = 100
    minimum_z = 164
    maximum_x = minimum_x + 4
    maximum_y = minimum_y + 4
    maximum_z = minimum_z + 4
    center_x = minimum_x + 2
    center_y = minimum_y + 2

    blocks: dict[tuple[int, int, int], Placement] = {}

    def place(
        case_id: str,
        label: str,
        x: int,
        y: int,
        z: int,
        block_state: str,
        expected: str,
    ) -> None:
        position = (x, y, z)
        blocks.pop(position, None)
        blocks[position] = Placement(
            f"{prefix}-{case_id}",
            f"{tier} {label}",
            x,
            y,
            z,
            block_state,
            expected,
        )

    casing = f"bigreactors:{tier}_turbinecasing"
    glass = f"bigreactors:{tier}_turbineglass"
    shaft = f"bigreactors:{tier}_turbinerotorshaft"
    blade = f"bigreactors:{tier}_turbinerotorblade"

    for x in range(minimum_x, maximum_x + 1):
        for y in range(minimum_y, maximum_y + 1):
            for z in range(minimum_z, maximum_z + 1):
                if (
                    x in (minimum_x, maximum_x)
                    or y in (minimum_y, maximum_y)
                    or z in (minimum_z, maximum_z)
                ):
                    place(
                        f"shell-{x}-{y}-{z}",
                        "assembled turbine shell",
                        x,
                        y,
                        z,
                        casing,
                        "assembled-turbine-shell",
                    )

    # Glass on three faces keeps the stopped rotor visible in both renderers.
    for x in range(minimum_x + 1, maximum_x):
        for z in range(minimum_z + 1, maximum_z):
            place(
                f"top-glass-{x}-{z}",
                "assembled turbine glass",
                x,
                maximum_y,
                z,
                glass,
                "assembled-turbine-shell",
            )
    for y in range(minimum_y + 1, maximum_y):
        for z in range(minimum_z + 1, maximum_z):
            place(
                f"side-glass-{y}-{z}",
                "assembled turbine glass",
                maximum_x,
                y,
                z,
                glass,
                "assembled-turbine-shell",
            )
    for x in range(minimum_x + 1, maximum_x):
        for y in range(minimum_y + 1, maximum_y):
            place(
                f"rear-glass-{x}-{y}",
                "assembled turbine glass",
                x,
                y,
                maximum_z,
                glass,
                "assembled-turbine-shell",
            )

    # The block opposite the bearing must specifically remain casing.
    place(
        "terminal-casing",
        "terminal rotor casing",
        center_x,
        center_y,
        maximum_z,
        casing,
        "assembled-turbine-shell",
    )

    for section, z in enumerate(range(minimum_z + 1, maximum_z), start=1):
        place(
            f"shaft-{section}",
            f"contextual shaft section {section}",
            center_x,
            center_y,
            z,
            shaft,
            "contextual-static-rotor",
        )
        if section < 3:
            for axis, x, y in (
                ("west", center_x - 1, center_y),
                ("east", center_x + 1, center_y),
                ("down", center_x, center_y - 1),
                ("up", center_x, center_y + 1),
            ):
                place(
                    f"blade-{section}-{axis}",
                    "contextual rotor blade",
                    x,
                    y,
                    z,
                    blade,
                    "contextual-static-rotor",
                )

    coil_z = maximum_z - 1
    for x in range(minimum_x + 1, maximum_x):
        for y in range(minimum_y + 1, maximum_y):
            if (x, y) != (center_x, center_y):
                place(
                    f"coil-{x}-{y}",
                    "ludicrite coil",
                    x,
                    y,
                    coil_z,
                    "bigreactors:ludicrite_block",
                    "stock-visible",
                )

    # Place the controller and bearing last so the completed shell validates.
    place(
        "controller",
        "assembled turbine controller",
        minimum_x + 1,
        center_y,
        minimum_z,
        f"bigreactors:{tier}_turbinecontroller",
        "assembled-turbine-shell",
    )
    place(
        "bearing",
        "assembled turbine rotor bearing",
        center_x,
        center_y,
        minimum_z,
        f"bigreactors:{tier}_turbinerotorbearing",
        "assembled-turbine-shell",
    )
    return list(blocks.values())


PLACEMENTS = tuple(
    turbine_fixture("basic", "basic", 164)
    + turbine_fixture("reinforced", "reinforced", 176)
    + [
        Placement(
            "stock-control",
            "stone stock rendering control",
            172,
            100,
            171,
            "minecraft:stone",
            "stock-visible",
        )
    ]
)
