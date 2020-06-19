package pl.workonfire.bucik.generators.commands.generators.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;
import pl.workonfire.bucik.generators.managers.utils.interfaces.SubCommandInterface;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;

public class GetCommand implements SubCommandInterface {
    @Override
    public boolean executableByConsole() {
        return false;
    }

    @Override
    public String permission() {
        return "bucik.generators.get";
    }

    @Override
    public String name() {
        return "get";
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && !executableByConsole()) {
            if (sender.hasPermission(permission())) {
                final Player player = (Player) sender;
                if (args.length == 1) sender.sendMessage(getPrefixedLanguageVariable("no-generator-name-specified"));
                else {
                    final String generatorName = args[1];
                    if (BlockUtil.isGeneratorDefined(generatorName)) {
                        final Generator generator = new Generator(generatorName);
                        if (args.length == 3) {
                            try {
                                player.getInventory().addItem(generator.getItemStack(Integer.parseInt(args[2])));
                            }
                            catch (NumberFormatException exception) {
                                sender.sendMessage(getPrefixedLanguageVariable("argument-must-be-an-int"));
                                return;
                            }
                        }
                        else player.getInventory().addItem(generator.getItemStack(1));
                        player.sendMessage(getPrefixedLanguageVariable("generator-given") + generator.getId());
                    }
                    else player.sendMessage(getPrefixedLanguageVariable("generator-does-not-exist"));
                }
            }
            else sender.sendMessage(getPrefixedLanguageVariable("no-permission"));
        }
        else sender.sendMessage(getPrefixedLanguageVariable("cannot-open-from-console"));
    }

}