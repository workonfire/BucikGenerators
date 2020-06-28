package pl.workonfire.bucik.generators.managers.utils;

import org.bukkit.command.CommandSender;

public interface CommandInterface {

    String permission();

    String name();

    void run(CommandSender sender, String[] args);
}
