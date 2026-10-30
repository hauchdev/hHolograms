# hHolograms

Premium hologram system for Paper servers (1.20+). Create, edit, animate, and manage in-world holograms.

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
- `/hh animation <name> <none|scroll|typewriter|rainbow|pulse> [speed]`
- `/hh pause <name>` / `/hh resume <name>`
- `/hh reload`
- `/hh status`

## Permissions
- `hholograms.*` (op) wildcard
- `hholograms.create`, `remove`, `move`, `duplicate`, `edit`, `reload`, `status`, `trustedformatting` (op default)
- `hholograms.list`, `info`, `teleport` (true default)

## Configuration
See `config.yml`. Messages in `lang/lang_en.yml`.

## Building
`mvn package` (Java 21). Output `target/hHolograms-1.0.0.jar`.