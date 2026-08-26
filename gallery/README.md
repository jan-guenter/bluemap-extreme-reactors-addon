# Assembled turbine gallery

This generated gallery places matching, valid 5x5x5 basic and reinforced
turbines. Each fixture has a complete same-tier shell, controller, bearing,
three shaft sections, two four-blade planes, a Ludicrite coil ring, and glass
on three faces. A stone at `(172, 100, 171)` remains the stock rendering
control.

The compact geometry stays within the Basic turbine's exact five-block X/Z
limit. The opposite rotor endpoint remains casing, blades precede the coil
plane, and the bearing is placed last so both fixtures assemble before review.
The client and BlueMap therefore compare the same valid multiblock state.

Stable commands:

```bash
python gallery/generate.py
python gallery/generate.py --check
python gallery/lint.py
bash gallery/package.sh /tmp/bigreactors-gallery.zip
```

Keep gallery generation deterministic, bounded, synthetic where practical, and
free of candidate assets or captured meshes.
