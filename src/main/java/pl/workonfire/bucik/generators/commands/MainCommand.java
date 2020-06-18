package pl.workonfire.bucik.generators.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.workonfire.bucik.generators.Main;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.data.DropMultiplier;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;
import pl.workonfire.bucik.generators.managers.ConfigManager;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("bucik.generators.reload")) {
                        ConfigManager.reloadAll();
                        sender.sendMessage(getPrefixedLanguageVariable("plugin-reloaded"));
                    }
                    else sender.sendMessage(getPrefixedLanguageVariable("no-permission"));
                }
                else if (args[0].equalsIgnoreCase("get")) {
                    if (sender.hasPermission("bucik.generators.get")) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
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
                                            return false;
                                        }
                                    }
                                    else player.getInventory().addItem(generator.getItemStack(1));
                                    player.sendMessage(getPrefixedLanguageVariable("generator-given") + generator.getId());
                                }
                                else player.sendMessage(getPrefixedLanguageVariable("generator-does-not-exist"));
                            }
                        }
                        else sender.sendMessage(getPrefixedLanguageVariable("cannot-open-from-console"));
                    }
                    else sender.sendMessage(getPrefixedLanguageVariable("no-permission"));
                }
                else if (args[0].equalsIgnoreCase("drop")) {
                    if (sender.hasPermission("bucik.generators.drop.manipulate")) {
                        if (args.length == 1) sender.sendMessage(getPrefixedLanguageVariable("not-enough-arguments"));
                        else {
                            if (args[1].equalsIgnoreCase("getMultiplier"))
                                sender.sendMessage(getPrefixedLanguageVariable("current-drop-multiplier") + DropMultiplier.getDropMultiplier() + "x");
                            else if (args[1].equalsIgnoreCase("setMultiplier")) {
                                if (args.length == 2) sender.sendMessage(getPrefixedLanguageVariable("not-enough-arguments"));
                                else {
                                    try {
                                        DropMultiplier.setDropMultiplier(Integer.parseInt(args[2]));
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
                else sender.sendMessage(getPrefixedLanguageVariable("subcommand-does-not-exist"));
            }
            else {
                final String header;
                if (!(sender instanceof Player)) header = "\n§c§m--------------\n"; // for console
                else header = "§c§m--------------\n";
                sender.sendMessage(header +
                        "§bBucikGenerators §6" + Main.getPluginVersion() + "\n" +
                        "§6by §c§lB§6§lu§e§lt§a§ly§b§l9§3§l3§9§l5\n" +
                        "§6§ohttps://github.com/workonfire\n" +
                        "§c§m--------------");
            }
            return true;
        }
        catch (Exception exception) {
            Util.handleErrors(sender, exception);
            return false;
        }
    }
}
