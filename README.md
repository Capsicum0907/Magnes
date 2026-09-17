# Magnes

Items and experience come to you.

*Magnes* is Latin for a magnet, and the lodestone it is cut from.

> **Status: released, 1.0.0.** Verified in a client, and nine game tests pass
> headlessly.

## Target

| | |
|---|---|
| Minecraft | 1.21.1 |
| Loader | NeoForge 21.1.248 |
| Java | 21 |

1.21.1 is the version large tech mods stayed on, so it is where this mod is useful.

## Using one

**Craft a magnet and carry it.** Four iron and a block of redstone, laid out as the
thing it is:

```
I I
I I
 R
```

It works from anywhere in the inventory — the point of it is not having to hold it —
and is switched off and on by right-clicking. It glows while it is on.

**Nothing happens without one.** That sentence is here because the mod's first
outing looked broken: it described itself as "items come to the player", offered a
master switch called `enabled`, and then sat there, waiting for an item nobody had
been told existed. If everybody being magnetic is the wanted reading, set
`needsMagnet = false` and it becomes the true one.

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
change. They *can* be settings — unlike a tool's mining reach, which is baked into
a data component when the item is registered — because every one of them is read on
the tick it is used.

**Nothing here reimplements picking something up.** Both an item and an experience
orb already know how to give themselves to a player, through `playerTouch`, which
handles the pickup delay, whose the item is, the sound, the advancement, the pickup
events other mods listen for, and, for an orb, mending before experience. Calling it
is the difference between this mod having opinions about pickup and having none.

One consequence worth knowing: orbs are absorbed at the rate the game absorbs them
when walked over, which is one every other tick. A magnet gathers experience; it
does not hurry it.

The magnet works from anywhere in the inventory, not only in a hand — the point of
the item is not having to hold it — and is switched off and on by right-clicking.
Off is stored on the stack; a magnet that has never been touched works, so a fresh
one carries no state at all and still stacks with another.

## Build

```
run.bat                   # compile and launch a dev client - double-clickable
gradlew build             # produce the jar
gradlew runGameTestServer # run every game test, headless, then exit
gradlew runData           # regenerate models, recipes, language, test structures
python tools/make_textures.py   # regenerate the item sprite
```

`JAVA_HOME` must point at a JDK 21, or `java` must be on `PATH`.

## Roadmap

- [x] **0** — scaffold; the mod loads
- [x] **1** — the magnet: items and experience, a switch, settings, a recipe
- [x] **2** — six game tests. Three of them are refusals — beyond the reach, still
  inside the pickup delay, and switched off — because a magnet that takes too much
  is worse than one that takes too little. The DRAWN setting is still one no test
  exercises
- [x] **Release** — 1.0.0, after being played with in a client
- [ ] **3** — open questions below

## Open questions

- Whether one magnet with a configurable reach is right, or whether reach should be
  something the player upgrades.
- Whether DRAWN should aim at the player's feet rather than their eyes. It currently
  aims at the eyes, which looks right for items and slightly wrong for orbs.
- Whether to filter by what is being pulled — a whitelist would be a second kind of
  setting and needs a reason before it earns one.

## Related

One of a set of small, independent mods, each doing one thing and depending on
none of the others: [Fodina](https://github.com/Capsicum0907/Fodina),
[Trivium](https://github.com/Capsicum0907/Trivium),
[Magnes](https://github.com/Capsicum0907/Magnes),
[Cella](https://github.com/Capsicum0907/Cella),
[Acervus](https://github.com/Capsicum0907/Acervus),
[Fornax](https://github.com/Capsicum0907/Fornax),
[Caldarium](https://github.com/Capsicum0907/Caldarium).

## License

MIT. Decided on 2026-09-17.

MIT is the choice that puts the fewest obstacles in front of a modpack: All Rights
Reserved would have meant pack authors quietly leaving it out. It also matches the
rest of the set, so nobody has to check which of them is which.
