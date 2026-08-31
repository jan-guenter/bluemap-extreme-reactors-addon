# Changelog

## 0.1.0-alpha.2 - 2026-08-31

- Target only BlueMap feature-backport commit
  `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac` and API commit
  `285c9a60eff3ac2b0cab308ce1058d1565be0971`.
- Move the local adapter boundary from `bluemap522` to `bluemap523`.
- Compile the four shared bootstrap helpers from Adapter API
  `0.1.0-alpha.2` and remove the three duplicate local helpers.
- Keep the exact Extreme Reactors/ZeroCore profile, 180 routed turbine model
  variants, 235-case gallery, topology rules, and stock fallback unchanged.

## 0.1.0-alpha.1 - 2026-08-26

- Generated a fail-closed Java 21 BlueMap add-on seed for `bigreactors-1.21.1-2.4.28`.
- Restored the contextual basic and reinforced turbine shaft and blade models
  from stable neighbor topology in a deterministic stopped pose.
- Restored all exact basic and reinforced connected turbine-glass variants from
  same-tier neighbor topology, removing internal panel frames and their color
  shift over the coil ring.
- Added exact installed-resource admission, pure topology tests, and bounded
  assembled basic/reinforced turbine comparison fixtures.
