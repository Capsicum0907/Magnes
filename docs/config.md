# Settings

English | [日本語](config.ja.md)

A server config: `config/magnes-server.toml` in the game folder. A copy placed at
`serverconfig/magnes-server.toml` inside a world overrides it for that world. Every value is
read on the tick it is used.

| Key | Default | Range | What it does |
|---|---|---|---|
| `enabled` | `true` | | Master switch. Off means nothing is pulled to anybody, whatever they carry. |
| `magnet.needed` | `true` | | Whether a player has to carry a magnet. Off makes every player magnetic, always, and the item decoration. |
| `magnet.mustBeHeld` | `false` | | Whether the magnet only works in a hand (or a Curios slot) rather than anywhere in the inventory. |
| `pull.radius` | `6.0` | 1.0–32.0 | How far a magnet reaches, in blocks, measured out from the player's bounding box. |
| `pull.mode` | `TAKEN` | `TAKEN`, `DRAWN` | `TAKEN` puts things straight into the inventory. `DRAWN` pushes them toward the player, to be picked up as usual — better to watch, and it can miss. |
| `pull.drawnSpeed` | `0.35` | 0.05–2.0 | Blocks per tick under `DRAWN`. Ignored by `TAKEN`. |
| `pull.includesExperience` | `true` | | Whether experience orbs are pulled as well as items. Orbs are absorbed at the rate the game absorbs them when walked over. |
| `cost.tickInterval` | `1` | 1–40 | Ticks between sweeps. `1` is every tick. Raising it costs smoothness under `DRAWN` and buys back scanning time. |
