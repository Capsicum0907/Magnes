# How it works

English | [日本語](how-it-works.ja.md)

What used to be written in the source, kept here instead so the code stays short and the
reasoning has one place to live.

## The sweep

`Magnetism.sweep(player)` is the whole of what the mod does. It is a plain method rather
than the body of an event handler, so a test can run one sweep and look at the result
instead of arranging for a real tick to happen.

It is called from `PlayerTickEvent.Post` — after the tick, so it sees where the player
finished. The interval is counted off the player's own `tickCount` rather than the
server's, so players fall on different ticks instead of all sweeping on the same one.

It does nothing on the client, for a dead player or for a spectator, when the master
switch is off, or when the player is not magnetic.

## Who is magnetic

Checked in this order:

1. `magnet.needed` is off — everyone is.
2. A switched-on magnet in either hand.
3. A switched-on magnet in a Curios slot, when Curios is installed.
4. `magnet.mustBeHeld` is on — stop here.
5. A switched-on magnet anywhere in the main inventory.

A worn magnet counts as held, not as one in a bag. Wearing it is a deliberate act of
putting it on, which is what `mustBeHeld` asks for; anything else would make the slot
useless to exactly the people who turned that setting on.

The requirement is stated loudly because the mod's first outing looked broken: it
described itself as "items come to the player", offered a master switch called
`enabled`, and then did nothing, waiting for an item nobody had been told existed.
`needed = false` is for people who wanted that reading to be the true one.

## What is left alone

An item still inside its pickup delay is skipped. That delay is what makes dropping
something possible at all — without it a magnet would take back everything the player
threw, the instant they threw it — and it is also what keeps another player's fresh death
drop out of reach.

## Picking up

Nothing reimplements picking something up. Items and experience orbs already know how to
give themselves to a player through `playerTouch`, which handles the pickup delay, whose
the item is, the sound, the advancement, the pickup events other mods listen for, and, for
an orb, mending before experience.

- `TAKEN` calls `playerTouch` straight away.
- `DRAWN` sets a velocity toward the player's eyes at `drawnSpeed` and lets the ordinary
  pickup happen. An entity already on top of the player is touched directly, since a
  direction that short would be noise.

Orbs are absorbed at the game's own rate, one every other tick.

## The switch

On or off is the `magnes:active` data component. Absent means on, so a magnet that has
never been touched works and carries no component at all.

The state is shown by texture — colour when on, grey when off — not by an enchantment
glint, which is nearly invisible over a red and blue item. The base model is the
switched-off one and the override is the working one, because a model override only
replaces the plain model when its predicate matches. The client registers the
`magnes:active` item property that the override reads; a model cannot say where that
number comes from by itself.

A right-click flips the component and plays a lever click, pitched up for on and down for
off. The tooltip shows the server's reach, and only once a config is loaded — the same
tooltip can be drawn on a title screen, where asking would throw.

## Settings are read live

The config is a SERVER config: where a dropped item is, is world state, and only the
server decides world state. A client gets the host's values. Every value is read on the
tick it is used, which is why they can be settings at all — unlike a tool's reach, which
is baked into a data component when the item is registered.

What each one does is in [config.md](config.md).

## Curios

Curios is optional, and the integration is one question — is a switched-on magnet being
worn — asked in `curios/Worn`. Nothing else in the mod names a Curios class.

The check that guards it lives in `Mods`, never in `Worn`. Calling a static method
initialises the class it is on, so a guard next to the code it protects would load the
very thing it was meant to avoid, and the mod would die during construction with
`NoClassDefFoundError`. Every use is a short-circuit, so the Curios side is only reached
when the answer is yes.

Two generated data files do the rest, and they answer different questions:

- `data/magnes/curios/entities/player.json` — may a player have a charm slot at all.
  Curios ships slot definitions but hands them to nobody; without this the Curios button
  appears and opens onto nothing, which is how it looked the first time it was played.
- `data/curios/tags/item/charm.json` (and `curio.json`) — does a magnet fit. Every slot is
  declared with `"validators": ["curios:tag"]` and looks for `curios:<slot>`.

Only `charm` is asked for. The magnet is tagged for the catch-all `curio` slot too, so it
fits where some other mod has already added one, but adding two slots to every player for
one item would take more room than needed. The tag files are written whether Curios is
installed or not: a tag naming a slot nobody declared is simply a tag nothing reads.

## Generated data

Everything under `src/generated/resources` comes from `data/MagnesDataGen` and
`data/TestStructures`; nothing there is written by hand. The recipe is laid out as the
thing it is — two arms and the bar that joins them.

The data run keeps per-provider hashes under `.cache`. That is bookkeeping for this
repository, and `build.gradle` excludes it from the jar.

## Tests

Run with `gradlew runGameTestServer`.

Three of the tests are refusals — beyond the reach, inside the pickup delay, switched off
— because a magnet that takes too much is worse than one that takes too little.

- The stage is a 20 × 5 × 20 floor written as NBT by `TestStructures`, with the data
  version taken from the game so it cannot drift. It is wide because the mod is about
  distance. Every cell is listed, air included, so one test cannot leave something behind
  for the next.
- `leavesItemsBeyondReach` sets the reach instead of assuming it. It once failed against a
  config where the reach had been raised to thirty-two.
- `theTickReachesTheSweep` posts a real tick event. Every other test calls `sweep`
  directly, which left the listener between the game and the sweep unchecked; a mod wired
  to nothing passes every test about what it would do.
- `aMagnetCanBeWorn` checks the Curios tags. Whether a magnet can be equipped is a fact
  about a data file, and a typo in a directory name would leave the code working against a
  slot nothing can be put into. It runs without Curios, which also shows a tag file in a
  namespace no mod owns upsets nothing.
- The tests use `makeMockServerPlayerInLevel`, which is marked for removal with no
  replacement. An experience orb only gives itself to a `ServerPlayer`, so there is nothing
  else to test the experience half with. When it goes, that is what has to be answered.
