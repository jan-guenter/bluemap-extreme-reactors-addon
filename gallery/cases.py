#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Bounded basic and reinforced turbine-rotor comparison fixtures."""

from __future__ import annotations

from dataclasses import dataclass


NAMESPACE = "bigreactors_gallery"
ENVELOPE = (166, 99, 162, 184, 105, 172)


@dataclass(frozen=True)
class Placement:
    case_id: str
    label: str
    x: int
    y: int
    z: int
    block_state: str
    expected: str


def rotor_fixture(prefix: str, tier: str, center_x: int) -> list[Placement]:
    """Create four stopped rotor sections with both perpendicular blade axes."""
    shaft = f"bigreactors:{tier}_turbinerotorshaft"
    blade = f"bigreactors:{tier}_turbinerotorblade"
    bearing = f"bigreactors:{tier}_turbinerotorbearing"
    placements = [
        Placement(
            f"{prefix}-bearing",
            f"{tier} rotor bearing control",
            center_x,
            102,
            164,
            bearing,
            "stock-visible",
        )
    ]
    for section, z in enumerate(range(165, 169), start=1):
        placements.append(
            Placement(
                f"{prefix}-shaft-{section}",
                f"{tier} contextual shaft section {section}",
                center_x,
                102,
                z,
                shaft,
                "contextual-static-rotor",
            )
        )
        axes = ("x", "y") if section == 3 else (
            ("x",) if section % 2 else ("y",)
        )
        for axis in axes:
            for sign in (-1, 1):
                for distance in (1, 2):
                    x = center_x + sign * distance if axis == "x" else center_x
                    y = 102 + sign * distance if axis == "y" else 102
                    placements.append(
                        Placement(
                            f"{prefix}-blade-{section}-{axis}-{sign}-{distance}",
                            f"{tier} contextual blade span",
                            x,
                            y,
                            z,
                            blade,
                            "contextual-static-rotor",
                        )
                    )
    return placements


PLACEMENTS = tuple(
    rotor_fixture("basic", "basic", 170)
    + rotor_fixture("reinforced", "reinforced", 180)
    + [
        Placement(
            "stock-control",
            "stone stock rendering control",
            175,
            100,
            171,
            "minecraft:stone",
            "stock-visible",
        )
    ]
)
