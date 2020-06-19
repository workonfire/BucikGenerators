package pl.workonfire.bucik.generators.managers.utils.interfaces;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface SubCommandInterface {
    boolean executableByConsole();

    String permission();

    String name();

    void run(CommandSender sender, Command command, String label, String[] args);
}
