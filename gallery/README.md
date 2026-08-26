# Turbine rotor gallery

This generated gallery places stopped basic and reinforced turbine rotors. Each
fixture has four shaft sections, long blade spans, and both perpendicular blade
axes. A stone at `(175, 100, 171)` remains the stock rendering control.

The fixtures are deliberately unassembled. Extreme Reactors' client derives the
same visible shaft and blade models from their topology, while the add-on does
the equivalent reconstruction for BlueMap without animation or live state.

Stable commands:

```bash
python gallery/generate.py
python gallery/generate.py --check
python gallery/lint.py
bash gallery/package.sh /tmp/bigreactors-gallery.zip
```

Keep gallery generation deterministic, bounded, synthetic where practical, and
free of candidate assets or captured meshes.
