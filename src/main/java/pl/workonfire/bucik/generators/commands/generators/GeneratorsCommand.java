package pl.workonfire.bucik.generators.commands.generators;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.workonfire.bucik.generators.Main;
import pl.workonfire.bucik.generators.commands.generators.subcommands.DropManipulateCommand;
import pl.workonfire.bucik.generators.commands.generators.subcommands.ForceDestroyCommand;
import pl.workonfire.bucik.generators.commands.generators.subcommands.GetCommand;
import pl.workonfire.bucik.generators.commands.generators.subcommands.ReloadCommand;
import pl.workonfire.bucik.generators.managers.utils.CommandInterface;

public class GeneratorsCommand implements CommandExecutor, CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final ReloadCommand reloadCommand = new ReloadCommand();
        final GetCommand getCommand = new GetCommand();
        final DropManipulateCommand dropCommand = new DropManipulateCommand();
        final ForceDestroyCommand forceDestroyCommand = new ForceDestroyCommand();
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase(reloadCommand.name()))
                reloadCommand.run(sender, command, label, args);
            else if (args[0].equalsIgnoreCase(getCommand.name()))
                getCommand.run(sender, command, label, args);
            else if (args[0].equalsIgnoreCase(dropCommand.name()))
                dropCommand.run(sender, command, label, args);
            else if (args[0].equalsIgnoreCase(forceDestroyCommand.name()))
                forceDestroyCommand.run(sender, command, label, args);
        }
        else run(sender, command, label, args);
        return true;
    }

    @Override
    public boolean executableByConsole() {
        return true;
    }

    @Override
    public String permission() {
        return null;
    }

    @Override
    public String name() {
        return "generators";
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        final String header;
        if (!(sender instanceof Player)) header = "\n§c§m--------------\n"; // for console
        else header = "§c§m--------------\n";
        sender.sendMessage(header +
                "§bBucikGenerators §6" + Main.getPluginVersion() + "\n" +
                "§6by §c§lB§6§lu§e§lt§a§ly§b§l9§3§l3§9§l5\n" +
                "§6§ohttps://github.com/workonfire\n" +
                "§c§m--------------");
    }

}
