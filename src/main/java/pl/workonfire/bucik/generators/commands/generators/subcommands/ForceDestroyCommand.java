package pl.workonfire.bucik.generators.commands.generators.subcommands;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.workonfire.bucik.generators.data.GeneratorDurabilities;
import pl.workonfire.bucik.generators.data.GeneratorLocation;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.Command;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixLangVar;
import static pl.workonfire.bucik.generators.managers.utils.Util.sendMessage;

public class ForceDestroyCommand implements Command {

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
        if (Util.isAuthorized(sender, permission())) {
            if (Util.isPlayer(sender)) {
                Player player = (Player) sender;
                Block targetBlock;
                try {
                    targetBlock = player.getTargetBlockExact(5);
                }
                catch (NoSuchMethodError error) {
                    targetBlock = player.getTargetBlock(null, 5);
                }
                if (targetBlock != null && Generator.isGenerator(targetBlock.getLocation(), targetBlock.getWorld())) {
                    Generator generator = Generator.from(targetBlock.getType());
                    //noinspection ConstantConditions
                    generator.unregister(targetBlock.getLocation(), targetBlock.getWorld());
                    GeneratorLocation generatorLocation = GeneratorLocation.from(
                            targetBlock.getLocation(), targetBlock.getWorld().getName()
                    );
                    GeneratorDurabilities.getInstance().unregister(generatorLocation);
                    targetBlock.setType(Material.AIR);
                    targetBlock.getLocation().add(0, 1, 0).getBlock().setType(Material.AIR);
                    Util.playSound(player, Sound.ENTITY_WITHER_HURT);
                    Util.showParticle(player, targetBlock, Particle.SMOKE_LARGE, 7);
                    sendMessage(sender, getPrefixLangVar("base-generator-destroyed"));
                }
                else sendMessage(sender, getPrefixLangVar("force-destroy-block-is-not-a-generator"));
            }
        }
    }
}
