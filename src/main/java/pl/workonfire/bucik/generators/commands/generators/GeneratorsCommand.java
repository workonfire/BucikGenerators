package pl.workonfire.bucik.generators.commands.generators;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.workonfire.bucik.generators.BucikGenerators;
import pl.workonfire.bucik.generators.commands.generators.subcommands.DropManipulateCommand;
import pl.workonfire.bucik.generators.commands.generators.subcommands.ForceDestroyCommand;
import pl.workonfire.bucik.generators.commands.generators.subcommands.GetCommand;
import pl.workonfire.bucik.generators.commands.generators.subcommands.ReloadCommand;
import pl.workonfire.bucik.generators.managers.ConfigManager;
import pl.workonfire.bucik.generators.managers.utils.Command;
import pl.workonfire.bucik.generators.managers.utils.Util;

public class GeneratorsCommand implements CommandExecutor, Command {

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             org.bukkit.command.@NotNull Command command,
                             @NotNull String label,
                             String[] args) {

        Command reloadCommand       = new ReloadCommand();
        Command getCommand          = new GetCommand();
        Command dropCommand         = new DropManipulateCommand();
        Command forceDestroyCommand = new ForceDestroyCommand();

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase(reloadCommand.name()))
                reloadCommand.run(sender, args);
            else if (args[0].equalsIgnoreCase(getCommand.name()))
                getCommand.run(sender, args);
            else if (args[0].equalsIgnoreCase(dropCommand.name()))
                dropCommand.run(sender, args);
            else if (args[0].equalsIgnoreCase(forceDestroyCommand.name()))
                forceDestroyCommand.run(sender, args);
            else Util.sendMessage(sender, ConfigManager.getPrefixLangVar("subcommand-does-not-exist"));
        }
        else run(sender, args);
        return true;
    }

    @Override
    public @Nullable String permission() {
        return null;
    }

    @Override
    public String name() {
        return "generators";
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        String line = "§c§m--------------";
        if (!(sender instanceof Player)) line = "\n" + line; // for console
        sender.sendMessage(line + "\n" +
                "§bBucikGenerators §6" + BucikGenerators.getPluginVersion() + "\n" +
                "§6by §c§lB§6§lu§e§lt§a§ly§b§l9§3§l3§9§l5\n" +
                "§6§ohttps://github.com/workonfire\n" +
                "§c§lWARNING: §fThis plugin is not supported anymore.\n" +
                line.replace("\n", ""));
    }

}
