# DeepResonance

Deep Resonance: a powergen mod for Minecraft involving crystals

## Building

Use Java 25. With the sibling MultiWorkspace checkout, run `./gradlew :DeepResonance:build` from `Multi_neo` or `./gradlew build` from this checkout. Regenerate resources with `./gradlew :DeepResonance:runClientData` from MultiWorkspace.

Run the adapted McJty Mod Lab and test setup command GameTests with `./gradlew -PdeepResonanceTests :DeepResonance:runGameTestServer` from MultiWorkspace.

## In-game test setups

Operators with permission level 2 can run `/deepresonance testsetup <id>` as a player.
Stand at the south edge of a clear area. The command builds north from your
feet and puts a quartz floor one block below you. It checks the whole footprint
before placing blocks and replaces the ordinary ground in that footprint.

| ID | Quartz floor | What to test |
| --- | --- | --- |
| `purification` | 11 × 8 | The upper tank feeds the preloaded purifier. Turn on the three laser levers to improve strength, efficiency, and purity. The station includes spare filters, catalysts, crystals, and a liquid injector. |
| `generation` | 11 × 11 | Enter the radiation-shielded room and turn on the generator controller's lever. Three pedestals hold powerful crystals and carry spares. The chest outside contains a radiation suit and monitor. |
| `crystal` | 7 × 7 | The crystallizer draws prepared RCL from the tank below and forms a crystal at the configured processing rate. |
| `smelter` | 7 × 7 | The smelter uses the half-full lower lava tank to convert preloaded resonating ore into RCL in the upper tank. The chest holds extra ore and lava. |

If RFTools Power is installed, the command configures creative dimensional cells
to power the lasers, crystallizer, and smelter, and sets a regular cell to
receive power from the generator. Otherwise, connect an FE source to the
powered machines.
