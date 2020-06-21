package pl.workonfire.bucik.generators.listeners.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                List<String> commands = new ArrayList<>();
                if (sender.hasPermission("bucik.generators.reload")) commands.add("reload");
                if (sender.hasPermission("bucik.generators.get")) commands.add("get");
                if (sender.hasPermission("bucik.generators.drop.manipulate")) commands.add("drop");
                if (sender.hasPermission("bucik.generators.forcedestroy")) commands.add("forceDestroy");
                return StringUtil.copyPartialMatches(args[0], commands, new ArrayList<>());
            case 2:
                if (args[0].equalsIgnoreCase("get") && sender.hasPermission("bucik.generators.get"))
                    return StringUtil.copyPartialMatches(args[1], BlockUtil.getGeneratorsIds(), new ArrayList<>());
                else if (args[0].equalsIgnoreCase("drop") && sender.hasPermission("bucik.generators.drop.manipulate"))
                    return StringUtil.copyPartialMatches(args[1], Arrays.asList("getMultiplier", "setMultiplier"), new ArrayList<>());
                else return new ArrayList<>();
            case 3:
                if (args[0].equalsIgnoreCase("get") && sender.hasPermission("bucik.generators.get"))
                    return new ArrayList<>(Arrays.asList("1", "32", "64"));
                else if (args[1].equalsIgnoreCase("setMultiplier") && sender.hasPermission("bucik.generators.drop.manipulate"))
                    return new ArrayList<>(Arrays.asList("1x", "2x", "4x"));
            default:
                return new ArrayList<>();
        }
    }
}
