# BlueMap Extreme Reactors Add-on

A Java 21 BlueMap add-on for the exact `bigreactors-1.21.1-2.4.28` profile in All the Mons
`1.2.0` / Minecraft `1.21.1`.

Status: visual-test prototype. The exact artifact gate admits only Extreme
Reactors `1.21.1-2.4.28` with ZeroCore `1.21.1-2.4.21`. For basic and reinforced
turbine shafts and blades, the renderer reconstructs the client-only neighbor
state and displays the installed model in a deterministic stopped pose.

## Build

```bash
gradle --no-daemon -PbluemapSourcePath=../bluemap-backport clean check build
```

`check` is the quick Java/checkstyle/archive gate. `prototypeCheck` additionally
requires every exact candidate JAR property and validates the placeholder
gallery. See `provenance/upstreams.json` for immutable artifact identities and
the [execution guide](docs/EXECUTION.md) for the prototype-to-release loop.

## Install

Place the production JAR in BlueMap's add-on pack directory and restart the
BlueMap JVM. Removal plus one restart restores stock behavior; the add-on
creates no custom world state.

Set `-Dbluemap.bigreactors.disabled=true` to leave the exact profile inactive.

## Scope boundary

The initial implementation is limited to the missing contextual turbine rotor.
Live contents, fill levels, activity overlays, particles, and animation phase
stay stock or deterministic-neutral unless the owner explicitly expands scope.

No Extreme Reactors binary, source, class, asset, captured mesh, or gallery is
bundled in the add-on.
