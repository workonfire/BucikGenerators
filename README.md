# BucikGenerators

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/567c8bfa9c6b432f8b872fd59b6e7d90)](https://www.codacy.com/manual/workonfire/BucikGenerators?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=workonfire/BucikGenerators&amp;utm_campaign=Badge_Grade)
![GitHub last commit](https://img.shields.io/github/last-commit/workonfire/BucikGenerators)
![GitHub license](https://img.shields.io/github/license/workonfire/BucikGenerators)
![Made with love in Poland](https://madewithlove.now.sh/pl?heart=true)

## Features
- Fully customizable block generators
- Custom permissions for almost everything
- Custom cooldown times
- Custom crafting recipes
- Fully customizable items that can be dropped
- Two item drop modes: `inventory` and `ground`
- Custom item drop odds based on permissions and luck (percentage system)
- Changeable action-bar messages
- Custom item drop multipliers (useful for events)
- Nice sound and particle effects
- Previewable generator drop details (`/drop`)
- World blacklist
- Database auto-save system

## Permissions
- `bucik.generators.reload`
- `bucik.generators.get`
- `bucik.generators.drop.see`
- `bucik.generators.drop.manipulate`
  
## Commands
- `/generators reload`
- `/generators drop <getMultiplier|setMultiplier>`
- `/generators get <generatorID>`
- `/drop`
  
## Screenshots
![Screenshot 1](https://i.imgur.com/XPQAbUr.png)

![Screenshot 2](https://i.imgur.com/QNcWJ8c.png)

![Screenshot 3](https://i.imgur.com/lMlBoOr.png)

![Screenshot 4](https://i.imgur.com/jdAO19o.png)

![Screenshot 5](https://i.imgur.com/SCI4KyO.png)

## Example generators config (`generators.yml`)
```yaml
generators:
  normal:
    break-cooldown: 1 # In ticks. 20 ticks = 1 second
    permission: bucik.generators.normal
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
            item: DIAMOND
            amount: 1
            name: "&6An.. sVIP diamond?"
            lore:
              - "&5Hmm.."
            action-bar-message: "&f&l+ &f&l[&b&lDiamond&f&l]"
```
You can customize more options in `config.yml`.

This is one of my first plugins, so feel free to create a pull request if you think something can be done better.