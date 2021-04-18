package pl.workonfire.bucik.generators.commands.generators.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.Command;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixLangVar;
import static pl.workonfire.bucik.generators.managers.utils.Util.sendMessage;

public class GetCommand implements Command {

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
            if (args.length == 1) sendMessage(sender, getPrefixLangVar("no-generator-name-specified"));
            else {
                String generatorName = args[1];
                if (Generator.isDefined(generatorName)) {
                    Generator generator = new Generator(generatorName);
                    if (args.length >= 3) {
                        try {
                            Player targetPlayer;
                            if (args.length == 4) {
                                targetPlayer = Bukkit.getServer().getPlayer(args[3]);
                                if (targetPlayer == null) {
                                    sendMessage(sender, getPrefixLangVar("this-player-does-not-exist"));
                                    return;
                                }
                            }
                            else {
                                if (Util.isPlayer(sender)) targetPlayer = (Player) sender;
                                else return;
                            }
                            targetPlayer.getInventory().addItem(generator.getItemStack(Integer.parseInt(args[2])));
                        }
                        catch (NumberFormatException exception) {
                            sendMessage(sender, getPrefixLangVar("argument-must-be-an-int"));
                            return;
                        }
                    }
                    else {
                        Player player;
                        if (Util.isPlayer(sender)) {
                            player = (Player) sender;
                            player.getInventory().addItem(generator.getItemStack(1));
                        }
                        else return;
                    }
                    sendMessage(sender, getPrefixLangVar("generator-given") + generator.getId());
                }
                else sendMessage(sender, getPrefixLangVar("generator-does-not-exist"));
            }
        }
    }
}
