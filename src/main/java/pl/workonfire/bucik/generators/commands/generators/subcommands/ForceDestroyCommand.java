package pl.workonfire.bucik.generators.commands.generators.subcommands;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;
import pl.workonfire.bucik.generators.managers.utils.CommandInterface;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;
import static pl.workonfire.bucik.generators.managers.utils.Util.sendMessage;

@SuppressWarnings("ConstantConditions")
public class ForceDestroyCommand implements CommandInterface {

    @Override
    public String permission() {
        return "bucik.generators.forcedestroy";
    }

    @Override
    public String name() {
        return "forceDestroy";
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission(permission())) {
                Block targetBlock;
                try {
                    targetBlock = player.getTargetBlockExact(5);
                }
                catch (NoSuchMethodError error) {
                    targetBlock = player.getTargetBlock(null, 5);
                }
                if (targetBlock != null && BlockUtil.isBlockAGenerator(targetBlock.getLocation(), targetBlock.getWorld())) {
                    Generator generator = BlockUtil.getGeneratorFromMaterial(targetBlock.getType());
                    generator.unregister(targetBlock.getLocation(), targetBlock.getWorld());
                    targetBlock.setType(Material.AIR);
                    targetBlock.getLocation().add(0, 1, 0).getBlock().setType(Material.AIR);
                    Util.playSound(player, Sound.ENTITY_WITHER_HURT);
                    Util.showParticle(player, targetBlock, Particle.SMOKE_LARGE, 7);
                    sendMessage(sender, getPrefixedLanguageVariable("base-generator-destroyed"));
                }
                else sendMessage(sender, getPrefixedLanguageVariable("force-destroy-block-is-not-a-generator"));
            }
            else sendMessage(sender, getPrefixedLanguageVariable("no-permission"));
        }
        else sendMessage(sender, getPrefixedLanguageVariable("cannot-open-from-console"));
    }
}
