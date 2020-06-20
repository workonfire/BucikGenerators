# BucikGenerators

![GitHub Workflow Status](https://github.com/workonfire/BucikGenerators/workflows/Java%20CI%20with%20Maven/badge.svg)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/567c8bfa9c6b432f8b872fd59b6e7d90)](https://www.codacy.com/manual/workonfire/BucikGenerators?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=workonfire/BucikGenerators&amp;utm_campaign=Badge_Grade)
![Spigot downloads](https://img.shields.io/spiget/downloads/80180)
![Made with love in Poland](https://madewithlove.now.sh/pl?heart=true)

[English version here](README.md)

## Funkcje
- W pełni dostosowywalne generatory bloków
- Niestandardowe permisje do prawie wszystkiego
- Dostosowywalne czasy odnowienia generatorów
- Dostosowywalne **craftingi**
- System **wytrzymałości generatorów**
- W pełni dostosowywalne przedmioty z dropu, **w tym pieniądze** (wymaga `Vault`a)
- Dwa tryby dropu przedmiotów: `inventory` (do ekwipunku) i `ground` (na ziemię)
- Niestandardowe szanse na drop przedmiotów bazowane na **uprawnieniach** i systemie procentowym
- Dostosowywalne **efekty mikstur**, które mogą zostać dane graczowi podczas kopania
- Zmienialne komunikaty action-bar'a
- Własne **mnożniki dropu** (przydatne na eventach)
- Ładne efekty dźwiękowe i cząsteczkowe
- Podgląd **dropu generatora** (`/drop`)
- Czarna lista światów
- System automatycznego zapisywania bazy danych

## Uprawnienia
- `bucik.generators.reload`
- `bucik.generators.get`
- `bucik.generators.drop.see`
- `bucik.generators.drop.manipulate`
- `bucik.generators.forcedestroy`
- `bucik.generators.debug`
  
## Komendy
- `/generators reload`
- `/generators drop <getMultiplier|setMultiplier>`
- `/generators get <generatorID>`
- `/generators forceDestroy`
- `/drop`
  
## Zrzuty ekranu
![Screenshot 1](https://i.imgur.com/XPQAbUr.png)

![Screenshot 2](https://i.imgur.com/QNcWJ8c.png)

![Screenshot 3](https://i.imgur.com/nI1UY74.png)

![Screenshot 4](https://i.imgur.com/jdAO19o.png)

![Screenshot 5](https://i.imgur.com/SCI4KyO.png)

![Screenshot 6](https://i.imgur.com/13ChMpe.png)

## Przykładowa konfiguracja (`generators.yml`)
```yaml
generators:
  normal:
    break-cooldown: 1 # W tickach. 20 ticków = 1 sekunds
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
      item-drop-mode: inventory # dostępne tryby: inventory, ground
      drop:
        # BARDZO WAŻNE:
        # ZAMIEŃ "." NA "-" W NAZWACH PERMISJI.
        # Uprawnienia:
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
              duration: 30 # W sekundach.
            action-bar-message: "&f&l+ &f&l[&5&lHaste II&f&l]"
          3:
            chance: 10 # %
            item: MONEY
            money-amount: 500
            action-bar-message: "&f&l+ &f&l[&2&l$&a&l500&f&l]"
```
Możesz dostosować więcej opcji w pliku `config.yml`.

Jest to jeden z moich pierwszych pluginów, więc możesz utworzyć pull request'a, jeśli uważasz, że coś można zrobić lepiej.
