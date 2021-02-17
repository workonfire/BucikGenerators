package pl.workonfire.bucik.generators.listeners.blocks;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import pl.workonfire.bucik.generators.data.GeneratorDurabilities;
import pl.workonfire.bucik.generators.data.GeneratorLocation;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;
import static pl.workonfire.bucik.generators.managers.utils.Util.sendMessage;

@SuppressWarnings("ConstantConditions")
public class BaseGeneratorBreakHandler {

    private final BlockBreakEvent event;
    private final Player player;
    private final Location generatorLocation;

    protected BaseGeneratorBreakHandler(BlockBreakEvent event, Player player, Location generatorLocation) {
        this.event = event;
        this.player = player;
        this.generatorLocation = generatorLocation;
    }

    protected void run() {
        Block block = event.getBlock();
        GeneratorLocation fullBlockLocation = BlockUtil.convertLocation(block.getLocation(), block.getWorld().getName());
        Generator generator = BlockUtil.getGeneratorFromMaterial(block.getType());
        if (player.hasPermission(generator.getPermission())) {
            if (generator.isDurabilityEnabled() && BlockUtil.hasDurabilityLeft(fullBlockLocation)) {
                int currentDurability = GeneratorDurabilities.getInstance().getValue(fullBlockLocation);
                if (currentDurability > 0) {
                    Util.playSound(player, Sound.ENTITY_BLAZE_HURT);
                    sendMessage(player, getPrefixedLanguageVariable("cannot-break-the-base") + currentDurability);
                    event.setCancelled(true);
                }
            }
            else {
                generator.unregister(block.getLocation(), block.getWorld());
                event.setCancelled(true);
                generatorLocation.getBlock().setType(Material.AIR);
                block.setType(Material.AIR);
                block.getWorld().dropItemNaturally(block.getLocation(), generator.getItemStack(1));
                Util.playSound(block, Sound.BLOCK_ANVIL_LAND);
                Util.showParticle(player, block, Particle.SMOKE_LARGE, 7);
                if (generator.isDurabilityEnabled())
                    GeneratorDurabilities.getInstance().unregister(fullBlockLocation);
                sendMessage(player, getPrefixedLanguageVariable("base-generator-destroyed"));
            }
        }
        else {
            event.setCancelled(true);
            sendMessage(player, getPrefixedLanguageVariable("no-permission"));
        }
    }
}
