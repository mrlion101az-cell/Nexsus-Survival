# NexusSurvival v0.1.0

Four survival systems built together on one shared tick loop: thirst,
radiation zones (with an oxygen bar), armor hygiene, and disease/cure.

## What's built

- **Thirst** -- a 0-20 bar (shown as a boss bar) that decays over time.
  Drink a Water Bottle to restore it. Hits 0 -> periodic damage + Nausea
  until you drink again.
- **Radiation zones** -- define cuboid zones with a wand tool. Players
  inside one drain a separate "Rad-O2" bar (also a boss bar, only shown
  while relevant) unless wearing a Hazmat Mask. Hits 0 -> periodic
  suffocation damage.
- **Armor hygiene** -- wearing any armor slowly raises a hidden
  "dirtiness" stat. At 100, each check has a chance to infect you with a
  random disease. Sneak + right-click a water cauldron to wash it back to 0.
- **Disease + cures** -- 4 diseases (Rattling Cough, Fever Rot,
  Glowsickness, Bone Chill), each with its own symptom effects and its
  own dedicated cure potion. Wrong cure = no effect, not an error.

## Commands
- `/nexussurvival give waterbottle` -- water bottle
- `/nexussurvival give gasmask` -- Hazmat Mask (blocks radiation drain)
- `/nexussurvival give wand` -- radiation zone wand
- `/nexussurvival give cure <DISEASE>` -- e.g. `RATTLING_COUGH`,
  `FEVER_ROT`, `GLOWSICKNESS`, `BONE_CHILL`
- `/nexussurvival radiation wand` -- same as give wand
- `/nexussurvival radiation create <name>` -- after setting both wand
  corners (left-click = corner 1, right-click = corner 2)
- `/nexussurvival radiation remove <name>`
- `/nexussurvival radiation list`
- `/nexussurvival status` -- see your own thirst/rad-O2/dirtiness/infection
- `/nexussurvival resetme` -- panic button for your own stats
- `/nexussurvival removeall` -- wipes all tracked player state server-wide

All subcommands sit behind `nexussurvival.admin` (default: op) for now,
same as the other Nexus plugins during testing -- see rough edges below.

## Setup (same flow as before)
1. Push this folder as its own repo (or folder) -- keep it a separate
   plugin/jar from NexusMechanica and NexusDrones.
2. Open in a Codespace, confirm Java 21 (`java -version` /
   `sdk use java 21.0.11-amzn` if needed).
3. `mvn clean package`
4. Grab `target/NexusSurvival-0.1.0.jar`, drop it in `plugins/`, restart.

## Known rough edges to expect (first pass, un-tested in-game)
- **This has not been through a real compile/run cycle yet** -- same
  disclaimer as every other first drop in this family. Send me the
  `mvn clean package` output and I'll fix whatever it flags, same loop
  as the ghost door / getServer() fixes.
- **No persistence** -- thirst, rad-O2, dirtiness, infection, and
  radiation zones all reset on server restart. Zones especially will
  want saving to config.yml once the mechanics feel right.
- **All commands are admin-gated** -- there's no real crafting recipe
  for water bottles, gas masks, or cures yet; everything is handed out
  via `/nexussurvival give` for testing. Wiring up actual recipes/loot
  is a natural next step once you've playtested the balance.
- **Hygiene is a flat timer, not activity-based** -- dirtiness climbs
  just from wearing armor, not from what you're doing (mining, fighting,
  swimming in mud, etc.). Fine for a first pass; easy to make more
  granular later.
- **Washing doesn't consume the cauldron's water level** -- infinite
  reuse for now. One-line fix if you want it to drain a level per wash.
- **No team/ally distinction anywhere** -- not relevant here the way it
  was for drones/turrets, just flagging that these systems don't check
  factions either.
- **Radiation zones can overlap** -- if they do, a player just counts
  as "in radiation," drain doesn't stack. No visual boundary/warning
  when approaching a zone edge yet.
- **One disease at a time** -- you can't catch a second disease while
  already infected with one. Simpler to reason about for v0.1; a
  "compounding infections" version is a bigger design conversation.

Deploy it, break it, send me the console output or in-game behavior
that looks wrong -- same loop as always.
