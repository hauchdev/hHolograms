# Changelog

Notable changes to hHolograms are recorded here.

## [0.3.0] - Released

### Added

- Multi-page text holograms with per-page lines and shared `next`/`prev` navigation.
- Player-head display holograms configured with a player profile.
- Per-line click actions, plus hologram-wide click actions for displays.
- Player, UUID, world, and position placeholders in click commands.
- Explicit `console:` click actions protected by `hholograms.interact.console`.

### Improved

- Rainbow, pulse, and fade animations now work on MiniMessage-formatted lines.
- Hologram page data and line actions are persisted in YAML. Existing hologram files without page data continue to load as a single-page hologram.
- Duplicate and renamed holograms retain their page content and line actions.

### Compatibility notes

- The selected page and player-head profile are shared by viewers. Per-viewer hologram text and skins still require a player-specific renderer.
- Holograms continue to use Bukkit display entities and Paper's scheduler; Folia and packet-only rendering are not supported by this release.
