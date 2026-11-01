# hHolograms

Premium hologram system for Paper servers (1.20+). Create, edit, animate, and manage in-world holograms built with Minecraft display entities.

## Highlights

- Text holograms with MiniMessage, per-line scale and spacing, alignment, line width, billboard mode, background color, shadow, see-through, brightness, rotation, and view distance.
- Transparent text backgrounds and custom `#RRGGBB` or `#AARRGGBB` colors. The eight-digit form uses alpha first; `#00000000` is fully transparent.
- Item and block display holograms.
- Centralized animation and placeholder updates, with PlaceholderAPI kept optional.
- Player-specific visibility conditions resolved with PlaceholderAPI when installed.
- Text placeholders are refreshed in a shared hologram context; per-viewer text is not available with the current entity renderer.
- Click actions with a permission check, main-hand filtering, and per-player cooldown.
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
