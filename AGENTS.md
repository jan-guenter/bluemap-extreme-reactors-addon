# Agent guide for the Extreme Reactors BlueMap add-on

This is an independent public add-on repository generated from the private
All the Mons orchestration scaffold. Read this file and `README.md` before
changing it.

## Exact baseline

- All the Mons `1.2.0`, pack commit `c7bb230f21d14d26859d0b92548f089b3a493ad9`
- Minecraft `1.21.1`
- NeoForge `21.1.248`
- Java `21`
- BlueMap `5.22-feature.backport-5.23-stateless-java-web-server-46`, commit
  `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac`
- BlueMap API commit `285c9a60eff3ac2b0cab308ce1058d1565be0971`
- Exact profile `bigreactors-1.21.1-2.4.28`

This is a standalone BlueMap add-on, not a NeoForge mod. Do not add client
classes, candidate binaries/assets/source, nested JARs, Minecraft classes,
Mixins, or world state.

## Development contract

- Preserve stock rendering while the runtime/profile is absent, duplicated,
  unsupported, malformed, disabled, or not yet implemented.
- Keep the BlueMap internal API behind `adapter/bluemap523`.
- Compile the four shared bootstrap sources only from the exact Adapter API
  gitlink pinned in `settings.gradle`; never bundle its standalone JAR.
- Keep exact candidate identities and resource contracts in the profile.
- Keep state/NBT decoding, normalized data, and mesh emission separate.
- Unknown family data gets one bounded diagnostic and stock fallback.
- Use installed resources only after exact-artifact admission.
- Gallery cases and renderer facts are family-owned; do not move them back to
  the generic scaffold.

The temporary unimplemented scaffold marker is permitted only during the fast
prototype phase. The release gate rejects it.

## Commands

Compile and test the safe seed:

```bash
gradle --no-daemon -PbluemapSourcePath=/tmp/bluemap-backport-523-source \
  clean check build
```

Verify a prototype with exact candidate JAR properties:

- `-PbigReactorsJar=/path/to/ExtremeReactors2-1.21.1-2.4.28.jar`
- `-PzeroCoreJar=/path/to/ZeroCore2-1.21.1-2.4.21.jar`

Pass those properties to Gradle and run `prototypeCheck`. Run
`verifyReleaseCandidate -PreleaseTag=v<version>` only after owner visual
acceptance and release sealing. Follow `docs/EXECUTION.md` for the reusable
prototype, acceptance, promotion and publication sequence.

Never stage or commit generated build output, candidate JARs, galleries, worlds,
credentials, logs, or research evidence.
