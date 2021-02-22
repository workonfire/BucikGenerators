package pl.workonfire.bucik.generators.commands.generators.subcommands;

import org.bukkit.command.CommandSender;
import pl.workonfire.bucik.generators.data.DropMultiplier;
import pl.workonfire.bucik.generators.managers.utils.Command;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixLangVar;
import static pl.workonfire.bucik.generators.managers.utils.Util.sendMessage;

public class DropManipulateCommand implements Command {

    @Override
    public String permission() {
        return "bucik.generators.drop.manipulate";
    }

    @Override
    public String name() {
        return "drop";
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (Util.isAuthorized(sender, permission())) {
            if (args.length == 1) sender.sendMessage(getPrefixLangVar("not-enough-arguments"));
            else {
                if (args[1].equalsIgnoreCase("getMultiplier"))
                    sendMessage(sender, getPrefixLangVar("current-drop-multiplier")
                            + DropMultiplier.getDropMultiplier() + "x");
                else if (args[1].equalsIgnoreCase("setMultiplier")) {
                    if (args.length == 2) sendMessage(sender, getPrefixLangVar("not-enough-arguments"));
                    else {
                        try {
                            DropMultiplier.setDropMultiplier(Integer.parseInt(args[2].replaceAll("x", "")));
                            sendMessage(sender, getPrefixLangVar("set-drop-multiplier")
                                    + DropMultiplier.getDropMultiplier() + "x.");
                        }
                        catch (NumberFormatException exception) {
                            sendMessage(sender, getPrefixLangVar("argument-must-be-an-int"));
                        }
                    }
                }
                else sendMessage(sender, getPrefixLangVar("subcommand-does-not-exist"));
            }
        }
    }
}
