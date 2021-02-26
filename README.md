# BucikGenerators

![GitHub Workflow Status](https://github.com/workonfire/BucikGenerators/workflows/Java%20CI%20with%20Maven/badge.svg)
![Spiget](https://img.shields.io/spiget/downloads/80180)
![Wzium](https://img.shields.io/badge/wzium-true-ff69b4)
![JavaDoc](https://img.shields.io/badge/JavaDoc-here!-orange?logo=java&link=https://workonfi.re/bucik/generators/apidocs)
![Made with love in Poland](https://madewithlove.now.sh/pl?heart=true)

[Polish version here](README.pl.md)

## Features
- Fully customizable block generators
- Custom permissions for almost everything
- Custom cooldown times
- Custom **crafting recipes**
- **Generator durability** system
- Fully customizable items that can be dropped, **including money** (requires `Vault`), and **experience**
- Two item drop modes: `inventory` and `ground`
- Custom item drop odds based on **permissions** and **luck** (percentage system)
- Customizable **pickaxe damage**
- Customizable **potion effects** that can be applied to player when mining
- Changeable action-bar messages
- Custom item **drop multipliers** (useful for events)
- Nice sound and particle effects
- Previewable **generator drop details** (`/drop`)
- World blacklist
- Database auto-save system
- A whitelist of items that can be used to destroy the generator

## Permissions
- `bucik.generators.reload`
- `bucik.generators.get`
- `bucik.generators.drop.see`
- `bucik.generators.drop.manipulate`
- `bucik.generators.forcedestroy`
- `bucik.generators.debug`
  
## Commands
- `/generators reload`
- `/generators drop <getMultiplier|setMultiplier> [value]`
- `/generators get <generatorID> [amount] [player]`
- `/generators forceDestroy`
- `/drop`
  
## Screenshots
![Screenshot 1](https://i.imgur.com/XPQAbUr.png)

![Screenshot 2](https://i.imgur.com/QNcWJ8c.png)

![Screenshot 3](https://i.imgur.com/nI1UY74.png)

![Screenshot 4](https://i.imgur.com/kMYCdYR.png)

![Screenshot 5](https://i.imgur.com/jdAO19o.png)

![Screenshot 6](https://i.imgur.com/SCI4KyO.png)

![Screenshot 7](https://i.imgur.com/13ChMpe.png)

## Example generators config (`generators.yml`)
```yaml
generators:
  normal:
    break-cooldown: 1 # In ticks. 20 ticks = 1 second
    permission: bucik.generators.normal
    respect-pickaxe-fortune: true
    durability:
      enabled: true
      value: 30
    affect-pickaxe-durability:
      enabled: true
      value: 25
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
    whitelist:
      enabled: false
      items:
        - DIAMOND_PICKAXE
    base:
      item: END_STONE
      name: "&7&l&oGenerator"
      lore:
        - "&6It simply generates infinite"
        - "&6cobblestone."
    generator:
      item: STONE
      item-drop-mode: inventory # available modes: inventory, ground, vanilla
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
            hide-enchantments: true
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
          4:
            chance: 5 # %
            item: EXP
            exp-amount: 50
```
You can customize more options in `config.yml`.

This is one of my first plugins, so feel free to create a pull request if you think something can be done better.