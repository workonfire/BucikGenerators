package pl.workonfire.bucik.generators.listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import pl.workonfire.bucik.generators.managers.BlockUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            final List<String> commands = new ArrayList<>();
            if (sender.hasPermission("bucik.generators.reload")) commands.add("reload");
            if (sender.hasPermission("bucik.generators.get")) commands.add("get");
            if (sender.hasPermission("bucik.generators.drop.manipulate")) commands.add("drop");
            return StringUtil.copyPartialMatches(args[0], commands, new ArrayList<>());
        }
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("get")) {
                if (sender.hasPermission("bucik.generators.get")) {
                    final List<String> generatorIds = new ArrayList<>(BlockUtil.getGeneratorsIds());
                    return StringUtil.copyPartialMatches(args[1], generatorIds, new ArrayList<>());
                }
                else return null;
            }
            else if (args[0].equalsIgnoreCase("drop")) {
                final List<String> dropCommands = new ArrayList<>(Arrays.asList("getMultiplier", "setMultiplier"));
                return StringUtil.copyPartialMatches(args[1], dropCommands, new ArrayList<>());
            }
            else return null;
        }
        else return null;
    }
}
