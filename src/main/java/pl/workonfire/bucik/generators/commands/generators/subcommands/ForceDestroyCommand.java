package pl.workonfire.bucik.generators.commands.generators.subcommands;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.ConfigManager;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;
import pl.workonfire.bucik.generators.managers.utils.CommandInterface;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;

public class ForceDestroyCommand implements CommandInterface {
    @Override
    public boolean executableByConsole() {
        return false;
    }

    @Override
    public String permission() {
        return "bucik.generators.forcedestroy";
    }

    @Override
    public String name() {
        return "forceDestroy";
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && !executableByConsole()) {
            final Player player = (Player) sender;
            if (player.hasPermission(permission())) {
                final Block targetBlock = player.getTargetBlockExact(5);
                if (targetBlock != null && BlockUtil.isBlockAGenerator(targetBlock.getLocation(), targetBlock.getWorld())) {
                    final Generator generator = BlockUtil.getGeneratorFromMaterial(targetBlock.getType());
                    generator.unregister(targetBlock.getLocation(), targetBlock.getWorld());
                    targetBlock.setType(Material.AIR);
                    targetBlock.getLocation().add(0, 1, 0).getBlock().setType(Material.AIR);
                    if (ConfigManager.areSoundsEnabled())
                        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_HURT, 1.0F, 1.0F);
                    player.sendMessage(getPrefixedLanguageVariable("base-generator-destroyed"));
                }
                else player.sendMessage(getPrefixedLanguageVariable("force-destroy-block-is-not-a-generator"));
            }
            else player.sendMessage(getPrefixedLanguageVariable("no-permission"));
        }
        else sender.sendMessage(getPrefixedLanguageVariable("cannot-open-from-console"));
    }
}
