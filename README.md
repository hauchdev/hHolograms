# hHolograms

Premium hologram system for Paper servers (1.20+). Create, edit, animate, and manage in-world holograms built with Minecraft display entities.

See [CHANGELOG.md](CHANGELOG.md) for release notes.

## Highlights

- Text holograms with MiniMessage, per-line scale and spacing, alignment, line width, billboard mode, background color, shadow, see-through, brightness, rotation, and view distance.
- Transparent text backgrounds and custom `#RRGGBB` or `#AARRGGBB` colors. The eight-digit form uses alpha first; `#00000000` is fully transparent.
- Item, block, and player-head display holograms.
- Centralized animation and placeholder updates, with PlaceholderAPI kept optional.
- Player-specific visibility conditions resolved with PlaceholderAPI when installed.
- Text placeholders are refreshed in a shared hologram context; per-viewer text is not available with the current entity renderer.
- Player heads use a configured player profile and are shared by viewers; viewer-specific skins and text need per-player entity rendering.
- Hologram-wide or per-line click actions with player/world/position placeholders, a permission check, main-hand filtering, and per-player cooldown. Explicit `console:` actions require `hholograms.interact.console`.
- Multi-page holograms with clickable line actions for navigation. The selected page is shared by viewers of that hologram.
- Atomic YAML saves, input validation, and independent deep copies when duplicating holograms.

hHolograms currently uses Bukkit display entities and Paper's scheduler. Folia support and packet-only rendering are not claimed by this build.

## Commands
Root: `/hholograms` (alias `/hh`)

- `/hh help`
- `/hh create <name> [world x y z]`
- `/hh remove <name>`
- `/hh list`
- `/hh info <name>`
- `/hh move <name> [world x y z]`
- `/hh tp <name>`
- `/hh duplicate <source> <newName>`
- `/hh edit <name> add|set|insert|remove|clear|swap ...`
- `/hh edit <name> click <line-index> <command...|clear>`
- `/hh page <name> <next|prev|set|add|remove> [page]`; `next` and `prev` can be used by players with `hholograms.interact`
- Example page button: `/hh edit welcome click 2 hh page welcome next`
- `/hh type <name> <text|item|block|head>` and `/hh content <name> <player>` for heads
- `/hh spacing <name> <value>`
- `/hh billboard <name> <center|vertical|horizontal|fixed>`
- `/hh range <name> <blocks>`
- `/hh background <name> <transparent|#RRGGBB|#AARRGGBB>`
- `/hh scale <name> <factor>`
- `/hh align <name> <left|center|right>`
- `/hh linewidth <name> <1-4096>`
- `/hh animation <name> <none|scroll|typewriter|rainbow|pulse|wave|bounce|glitch|fade|slide|shake> [speed]`
- `/hh pause <name>` / `/hh resume <name>`
- `/hh reload`
- `/hh status`

## Permissions
- `hholograms.*` (op) wildcard
- `hholograms.create`, `remove`, `move`, `duplicate`, `edit`, `reload`, `status`, `trustedformatting` (op default)
- `hholograms.list`, `info`, `teleport` (true default)
- `hholograms.interact` (true default; required to use configured click actions)
- `hholograms.admin` (op default; grants access to all management commands)

## Configuration
See `config.yml`. Messages in `lang/lang_en.yml`.

## Building
`mvn package` (Java 21+). The shaded plugin jar is written to `target/hHolograms-<version>.jar`.
