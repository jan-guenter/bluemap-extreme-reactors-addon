#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Lint the generated assembled-turbine gallery without starting Minecraft."""

from __future__ import annotations

import json
from pathlib import Path
import re
import sys

sys.dont_write_bytecode = True
import cases
import generate


ROOT = Path(__file__).resolve().parent


def main() -> int:
    for relative, payload in generate.generated_files().items():
        path = ROOT / relative
        if not path.is_file() or path.read_bytes() != payload:
            raise ValueError(f"generated file differs: {relative}")

    json.loads((ROOT / "datapack/pack.mcmeta").read_text(encoding="utf-8"))
    load_tag = json.loads(
        (ROOT / "datapack/data/minecraft/tags/function/load.json").read_text(
            encoding="utf-8"
        )
    )
    if load_tag != {"values": [f"{cases.NAMESPACE}:load"]}:
        raise ValueError("load tag differs from the exact namespace")
    if len(cases.PLACEMENTS) != 235:
        raise ValueError("gallery must contain exactly 235 bounded placements")
    if len({placement.case_id for placement in cases.PLACEMENTS}) != 235:
        raise ValueError("gallery case IDs must be unique")
    if len({(p.x, p.y, p.z) for p in cases.PLACEMENTS}) != 235:
        raise ValueError("gallery coordinates must be unique")
    stock = [
        placement
        for placement in cases.PLACEMENTS
        if placement.case_id == "stock-control"
    ]
    if len(stock) != 1 or stock[0].block_state != "minecraft:stone":
        raise ValueError("gallery must retain one honest stone stock control")
    contextual = [
        placement
        for placement in cases.PLACEMENTS
        if placement.expected == "contextual-static-rotor"
    ]
    if len(contextual) != 22:
        raise ValueError("gallery must contain 22 contextual rotor blocks")
    assembled_shell = [
        placement
        for placement in cases.PLACEMENTS
        if placement.expected == "assembled-turbine-shell"
    ]
    if len(assembled_shell) != 196:
        raise ValueError("gallery must contain two complete 98-block shells")
    for tier in ("basic", "reinforced"):
        for part in ("turbinecontroller", "turbinerotorbearing"):
            block = f"bigreactors:{tier}_{part}"
            if sum(p.block_state == block for p in cases.PLACEMENTS) != 1:
                raise ValueError(f"gallery must contain one {block}")
    if sum(
        p.block_state == "bigreactors:ludicrite_block"
        for p in cases.PLACEMENTS
    ) != 16:
        raise ValueError("gallery must contain two eight-block coil rings")
    minimum_x, minimum_y, minimum_z, maximum_x, maximum_y, maximum_z = (
        cases.ENVELOPE
    )
    if not all(
        minimum_x <= placement.x <= maximum_x
        and minimum_y <= placement.y <= maximum_y
        and minimum_z <= placement.z <= maximum_z
        for placement in cases.PLACEMENTS
    ):
        raise ValueError("gallery placement escaped its bounded envelope")

    function_root = ROOT / f"datapack/data/{cases.NAMESPACE}/function"
    functions = "\n".join(
        path.read_text(encoding="utf-8")
        for path in sorted(function_root.glob("*.mcfunction"))
    )
    if len(re.findall(r"^setblock ", functions, re.MULTILINE)) != 235:
        raise ValueError("gallery must place exactly 235 blocks")
    lowered = functions.lower()
    for forbidden in ("summon ", "data merge", "op ", "deop ", "stop "):
        if forbidden in lowered:
            raise ValueError(f"forbidden gallery command: {forbidden}")
    print("turbine gallery lint passed: two assembled fixtures and one control")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError) as error:
        print(f"gallery lint failed: {error}", file=sys.stderr)
        raise SystemExit(1)
