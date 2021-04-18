package pl.workonfire.bucik.generators.managers.utils;

import org.bukkit.command.CommandSender;

/**
 * A helper interface for a plugin command. Each command and subcommand implements it.
 */
public interface Command {

    String permission();

    String name();

    void run(CommandSender sender, String[] args);
}
