package pl.workonfire.bucik.generators.commands.generators.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;
import pl.workonfire.bucik.generators.managers.utils.CommandInterface;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;
import static pl.workonfire.bucik.generators.managers.utils.Util.sendMessage;

public class GetCommand implements CommandInterface {

    @Override
    public String permission() {
        return "bucik.generators.get";
    }

    @Override
    public String name() {
        return "get";
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (Util.isAuthorized(sender, permission())) {
            if (Util.isPlayer(sender)) {
                Player player = (Player) sender;
                try {
                    if (args.length == 1) sendMessage(sender, getPrefixedLanguageVariable("no-generator-name-specified"));
                    else {
                        String generatorName = args[1];
                        if (BlockUtil.isGeneratorDefined(generatorName)) {
                            Generator generator = new Generator(generatorName);
                            if (args.length == 3) {
                                try {
                                    player.getInventory().addItem(generator.getItemStack(Integer.parseInt(args[2])));
                                }
                                catch (NumberFormatException exception) {
                                    sendMessage(sender, getPrefixedLanguageVariable("argument-must-be-an-int"));
                                }
                            }
                            else {
                                player.getInventory().addItem(generator.getItemStack(1));
                                sendMessage(sender, getPrefixedLanguageVariable("generator-given") + generator.getId());
                            }
                        }
                        else sendMessage(sender, getPrefixedLanguageVariable("generator-does-not-exist"));
                    }
                }
                catch (Exception exception) {
                    Util.handleErrors(player, exception);
                }
            }
        }
    }

}