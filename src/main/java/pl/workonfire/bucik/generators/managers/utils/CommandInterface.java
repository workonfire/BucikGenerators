package pl.workonfire.bucik.generators.managers.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandInterface {
    boolean executableByConsole();

    String permission();

    String name();

    void run(CommandSender sender, Command command, String label, String[] args);
}
