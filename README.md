# BucikGenerators

![GitHub Workflow Status](https://github.com/workonfire/BucikGenerators/workflows/Java%20CI%20with%20Maven/badge.svg)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/567c8bfa9c6b432f8b872fd59b6e7d90)](https://www.codacy.com/manual/workonfire/BucikGenerators?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=workonfire/BucikGenerators&amp;utm_campaign=Badge_Grade)
![Spigot downloads](https://img.shields.io/spiget/downloads/80180)
![Made with love in Poland](https://madewithlove.now.sh/pl?heart=true)

## Features
- Fully customizable block generators
- Custom permissions for almost everything
- Custom cooldown times
- Custom **crafting recipes**
- **Generator durability** system
- Fully customizable items that can be dropped, **including money** (requires `Vault`)
- Two item drop modes: `inventory` and `ground`
- Custom item drop odds based on **permissions** and **luck** (percentage system)
- Customizable **potion effects** that can be applied to player when mining
- Changeable action-bar messages
- Custom item **drop multipliers** (useful for events)
- Nice sound and particle effects
- Previewable **generator drop details** (`/drop`)
- World blacklist
- Database auto-save system

## Permissions
- `bucik.generators.reload`
- `bucik.generators.get`
- `bucik.generators.drop.see`
- `bucik.generators.drop.manipulate`
- `bucik.generators.forcedestroy`
- `bucik.generators.debug`
  
## Commands
- `/generators reload`
- `/generators drop <getMultiplier|setMultiplier>`
- `/generators get <generatorID>`
- `/generators forceDestroy`
- `/drop`
  
## Screenshots
![Screenshot 1](https://i.imgur.com/XPQAbUr.png)

![Screenshot 2](https://i.imgur.com/QNcWJ8c.png)

![Screenshot 3](https://i.imgur.com/nI1UY74.png)

![Screenshot 4](https://i.imgur.com/jdAO19o.png)

![Screenshot 5](https://i.imgur.com/SCI4KyO.png)

![Screenshot 6](https://i.imgur.com/13ChMpe.png)

## Example generators config (`generators.yml`)
```yaml
generators:
  normal:
    break-cooldown: 1 # In ticks. 20 ticks = 1 second
    permission: bucik.generators.normal
    durability:
      enabled: true
      value: 30
    custom-crafting-recipe:
      slot-A: AIR
      slot-B: COBBLESTONE
      slot-C: AIR
      slot-D: STONE
      slot-E: AIR
      slot-F: AIR
      slot-G: GOLD_INGOT
      slot-H: AIR
      slot-I: AIR
    world-blacklist:
      - world_the_end
      - world_nether
    enchantments:
      - "unbreaking:5"
    hide-enchantments: true
    base:
      item: END_STONE
      name: "&7&l&oGenerator"
      lore:
        - "&6It simply generates infinite"
        - "&6cobblestone."
    generator:
      item: STONE
      item-drop-mode: inventory # available modes: inventory, ground
      drop:
        # EXTREMELY IMPORTANT:
        # REPLACE "." with "-" IN PERMISSIONS.
        # Permissions:
        bucik-generators-stone-vip:
          1:
            chance: 100 # %
            item: COBBLESTONE
            enchantments:
              - "silk_touch:1"
            action-bar-message: "&f&l+ &f&l[&7&lCobblestone&f&l]"
          2:
            chance: 7 # %
            item: DIAMOND
            amount: 1
            name: "&bA not really rare diamond."
            lore:
              - "&5:D"
            action-bar-message: "&f&l+ &f&l[&b&lDiamond&f&l]"
          3:
            chance: 0.45 # %
            item: EMERALD
            amount: 1
            name: "&6This should remain empty."
            lore:
              - "&5This should remain empty as well."
            action-bar-message: "&f&l+ &f&l[&b&lEmerald&f&l]"
        bucik-generators-stone-svip:
          1:
            chance: 5 # %
            item: POTION
            potion:
              effect: FAST_DIGGING
              amplifier: 2
              duration: 30 # In seconds.
            action-bar-message: "&f&l+ &f&l[&5&lHaste II&f&l]"
          3:
            chance: 10 # %
            item: MONEY
            money-amount: 500
            action-bar-message: "&f&l+ &f&l[&2&l$&a&l500&f&l]"
```
You can customize more options in `config.yml`.

This is one of my first plugins, so feel free to create a pull request if you think something can be done better.