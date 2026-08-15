# Magnes

Items and experience come to you.

*Magnes* is Latin for a magnet, and the lodestone it is cut from.

> **Status: scaffold only.** The mod loads and does nothing.

## Target

| | |
|---|---|
| Minecraft | 1.21.1 |
| Loader | NeoForge 21.1.248 |
| Java | 21 |

1.21.1 is the version large tech mods stayed on, so it is where this mod is useful.

## Design

Dropped items and experience orbs near a player move to the player rather than
waiting to be walked over.

**The pull happens on the server and nowhere else.** Where a dropped item is, is
world state, and only the server decides world state. A client that moved items on
its own would disagree with the server about who picked up what.

**It never takes what is somebody else's.** An item entity carries a pickup delay
and, briefly after a player dies, an owner. Both are respected: a magnet that
reached across the room and pulled a stranger's dropped inventory to itself would
be a grief tool rather than a convenience.

Two ways to bring something in, and they are not the same trade:

- put it straight into the inventory — certain, and undramatic
- give it a velocity toward the player and let the ordinary pickup happen — better
  to watch, and it can miss

Which one is a setting, not a decision made here.

Reach, on and off, and what kinds of thing are pulled are settings too. This is the
mod where a number written into the code would be the first thing anybody wants to
change.

## Build

```
run.bat                   # compile and launch a dev client - double-clickable
gradlew build             # produce the jar
gradlew runGameTestServer # run every game test, headless, then exit
gradlew runData           # regenerate models, recipes and language
```

`JAVA_HOME` must point at a JDK 21, or `java` must be on `PATH`.

## Roadmap

- [x] **0** — scaffold; the mod loads
- [ ] **1** — the feature above, in a form that can be watched
- [ ] **2** — checked by game tests rather than by eye

## Related

One of a set of small, independent mods, each doing one thing and depending on
none of the others: [Fodina](https://github.com/Capsicum0907/Fodina),
[Trivium](https://github.com/Capsicum0907/Trivium),
[Magnes](https://github.com/Capsicum0907/Magnes),
[Cella](https://github.com/Capsicum0907/Cella),
[Acervus](https://github.com/Capsicum0907/Acervus),
[Fornax](https://github.com/Capsicum0907/Fornax),
[Accumulator](https://github.com/Capsicum0907/Accumulator).

## License

Not decided yet. Until it is, the metadata says All Rights Reserved.
