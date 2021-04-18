package pl.workonfire.bucik.generators.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import pl.workonfire.bucik.generators.commands.generators.subcommands.DropManipulateCommand;
import pl.workonfire.bucik.generators.data.generator.DropItem;

/**
 * A class that represents the global {@link #dropMultiplier}, used by {@link DropManipulateCommand}.
 * <p>
 *     When the player breaks a generator, the default drop value defined in {@link DropItem#getDropChance()} is always
 *     multiplied by {@link #dropMultiplier}.
 * </p>
 */
@UtilityClass
public final class DropMultiplier {
    @Getter @Setter private static int dropMultiplier = 1;
}
