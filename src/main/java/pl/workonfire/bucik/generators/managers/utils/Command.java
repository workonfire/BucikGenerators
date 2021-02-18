package pl.workonfire.bucik.generators.managers.utils;

import org.bukkit.command.CommandSender;

public interface Command {

    String permission();

    String name();

    void run(CommandSender sender, String[] args);
}
