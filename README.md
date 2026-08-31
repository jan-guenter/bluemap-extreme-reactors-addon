# BlueMap Extreme Reactors Add-on

A Java 21 BlueMap add-on for the exact `bigreactors-1.21.1-2.4.28` profile in All the Mons
`1.2.0` / Minecraft `1.21.1`.

Status: owner-accepted `0.1.0-alpha.2` BlueMap 5.23 release candidate. It
inherits the accepted `0.1.0-alpha.1` turbine and gallery behavior; only the
BlueMap adapter boundary and shared bootstrap helpers changed. The exact artifact
gate admits only Extreme Reactors `1.21.1-2.4.28` with ZeroCore `1.21.1-2.4.21`.
For basic and reinforced turbine shafts, blades, and glass, the renderer
reconstructs the client-only neighbor state. It displays the installed rotor
models in a deterministic stopped pose and the installed 64-way
connected-glass models.

## Build

Clone with `--recurse-submodules`, or initialize an existing checkout with
`git submodule update --init --recursive`. The settings preflight accepts only
the committed toolkit and Adapter API gitlinks, including Adapter API commit
`e81f08bc4bfbf02d810ec8949a019130e2e61634` and source tree
`2f974c9bb2ba13888d69682f86f30f58922d30eb`, and rejects uninitialized,
changed, or dirty submodule checkouts.

```bash
gradle --no-daemon -PbluemapSourcePath=/tmp/bluemap-backport-523-source \
  clean check build
```

The four Adapter API sources are compiled directly into this standalone add-on;
no Adapter API JAR is needed at runtime. `check` is the quick
Java/checkstyle/archive gate. `prototypeCheck` additionally
requires every exact candidate JAR property and validates the placeholder
gallery. See `provenance/upstreams.json` for immutable artifact identities and
the [execution guide](docs/EXECUTION.md) for the prototype-to-release loop.

## Install

Place the production JAR in BlueMap's add-on pack directory and restart the
BlueMap JVM. Removal plus one restart restores stock behavior; the add-on
creates no custom world state.

Set `-Dbluemap.bigreactors.disabled=true` to leave the exact profile inactive.

## Scope boundary

The initial implementation is limited to the contextual turbine rotor and
connected turbine glass. Live contents, fill levels, activity overlays,
particles, and animation phase stay stock or deterministic-neutral unless the
owner explicitly expands scope.

No Extreme Reactors binary, source, class, asset, captured mesh, or gallery is
bundled in the add-on.
