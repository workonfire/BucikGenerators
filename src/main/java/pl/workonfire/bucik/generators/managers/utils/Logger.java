package pl.workonfire.bucik.generators.managers.utils;

import org.bukkit.ChatColor;

import java.io.PrintStream;

public enum Logger {

    INFO(ChatColor.WHITE, System.out),
    WARN(ChatColor.RED, System.err),
    DEBUG(ChatColor.YELLOW, System.out);

    public final ChatColor color;
    private final PrintStream stream;

    Logger(ChatColor color, PrintStream stream) {
        this.color = color;
        this.stream = stream;
    }

    public PrintStream getStream() {
        return stream;
    }

    public ChatColor getColor() {
        return !Util.isServerLegacy() ? color : null;
    }
}
