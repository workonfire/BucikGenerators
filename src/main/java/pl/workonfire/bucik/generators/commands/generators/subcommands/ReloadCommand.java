package pl.workonfire.bucik.generators.commands.generators.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.workonfire.bucik.generators.managers.ConfigManager;
import pl.workonfire.bucik.generators.managers.utils.CommandInterface;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;

public class ReloadCommand implements CommandInterface {

    @Override
    public boolean executableByConsole() {
        return true;
    }

    @Override
    public String permission() {
        return "bucik.generators.reload";
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(permission())) {
            ConfigManager.reloadAll();
            sender.sendMessage(getPrefixedLanguageVariable("plugin-reloaded"));
        }
        else sender.sendMessage(getPrefixedLanguageVariable("no-permission"));
    }

}
