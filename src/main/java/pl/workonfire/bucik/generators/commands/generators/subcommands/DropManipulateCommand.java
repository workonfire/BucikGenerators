package pl.workonfire.bucik.generators.commands.generators.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.workonfire.bucik.generators.data.DropMultiplier;
import pl.workonfire.bucik.generators.managers.utils.CommandInterface;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;

public class DropManipulateCommand implements CommandInterface {

    @Override
    public boolean executableByConsole() {
        return true;
    }

    @Override
    public String permission() {
        return "bucik.generators.drop.manipulate";
    }

    @Override
    public String name() {
        return "drop";
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(permission())) {
            if (args.length == 1) sender.sendMessage(getPrefixedLanguageVariable("not-enough-arguments"));
            else {
                if (args[1].equalsIgnoreCase("getMultiplier"))
                    sender.sendMessage(getPrefixedLanguageVariable("current-drop-multiplier") + DropMultiplier.getDropMultiplier() + "x");
                else if (args[1].equalsIgnoreCase("setMultiplier")) {
                    if (args.length == 2) sender.sendMessage(getPrefixedLanguageVariable("not-enough-arguments"));
                    else {
                        try {
                            DropMultiplier.setDropMultiplier(Integer.parseInt(args[2].replaceAll("x", "")));
                            sender.sendMessage(getPrefixedLanguageVariable("set-drop-multiplier") + DropMultiplier.getDropMultiplier() + "x.");
                        }
                        catch (NumberFormatException exception) {
                            sender.sendMessage(getPrefixedLanguageVariable("argument-must-be-an-int"));
                        }
                    }
                }
            }
        }
        else sender.sendMessage(getPrefixedLanguageVariable("no-permission"));
    }
}
