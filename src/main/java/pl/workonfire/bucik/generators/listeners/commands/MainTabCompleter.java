package pl.workonfire.bucik.generators.listeners.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainTabCompleter implements TabCompleter {
    private boolean authorize(String[] args, int arg, String command, String permission, CommandSender sender) {
        return args[arg].equalsIgnoreCase(command) && sender.hasPermission(permission);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                List<String> commands = new ArrayList<>();
                if (sender.hasPermission("bucik.generators.reload"))          commands.add("reload");
                if (sender.hasPermission("bucik.generators.get"))             commands.add("get");
                if (sender.hasPermission("bucik.generators.drop.manipulate")) commands.add("drop");
                if (sender.hasPermission("bucik.generators.forcedestroy"))    commands.add("forceDestroy");
                return StringUtil.copyPartialMatches(args[0], commands, new ArrayList<>());
            case 2:
                if (authorize(args, 0, "get", "bucik.generators.get", sender))
                    return StringUtil.copyPartialMatches(args[1], BlockUtil.getGeneratorsIds(), new ArrayList<>());
                else if (authorize(args, 0, "drop", "bucik.generators.drop.manipulate", sender))
                    return StringUtil.copyPartialMatches(
                            args[1], Arrays.asList("getMultiplier", "setMultiplier"), new ArrayList<>()
                    );
                else return new ArrayList<>();
            case 3:
                if (authorize(args, 0, "get", "bucik.generators.get", sender))
                    return new ArrayList<>(Arrays.asList("1", "32", "64"));
                else if (authorize(args, 1, "setMultiplier", "bucik.generators.drop.manipulate", sender))
                    return new ArrayList<>(Arrays.asList("1x", "2x", "4x"));
            case 4:
                if (authorize(args, 0, "get", "bucik.generators.get", sender)) {
                    List<String> playerNames = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers()) playerNames.add(player.getName());
                    return StringUtil.copyPartialMatches(args[3], playerNames, new ArrayList<>());
                }
            default:
                return new ArrayList<>();
        }
    }
}
