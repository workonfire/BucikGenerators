package pl.workonfire.bucik.generators.listeners;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import pl.workonfire.bucik.generators.data.Generator;
import pl.workonfire.bucik.generators.managers.BlockUtil;
import pl.workonfire.bucik.generators.managers.ConfigManager;
import pl.workonfire.bucik.generators.managers.Util;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;

@SuppressWarnings("ConstantConditions")
public class BlockPlaceListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        try {
            final Block block = event.getBlock();

            if (BlockUtil.isHeldBlockAGenerator(event.getItemInHand())) {
                final Generator baseGenerator = BlockUtil.getGeneratorFromMaterial(block.getType());
                for (String worldName : baseGenerator.getWorldBlacklist()) {
                    if (block.getWorld().getName().equals(worldName)) {
                        event.setCancelled(true);
                        player.sendMessage(getPrefixedLanguageVariable("cannot-place-in-this-world"));
                        if (ConfigManager.areSoundsEnabled())
                            player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1.0F, 1.0F);
                        return;
                    }
                }
                if (player.hasPermission(baseGenerator.getPermission())) {
                    if (ConfigManager.areSoundsEnabled())
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0F, 1.0F);
                    if (ConfigManager.areParticlesEnabled())
                        player.spawnParticle(Particle.END_ROD, block.getLocation(), 25);
                    player.sendMessage(getPrefixedLanguageVariable("generator-placed") + baseGenerator.getId());
                    baseGenerator.register(block.getLocation(), block.getWorld());
                    final Location generatorLocation = block.getLocation().add(0, 1, 0);
                    generatorLocation.getBlock().setType(baseGenerator.getGeneratorMaterial());
                }
                else {
                    event.setCancelled(true);
                    player.sendMessage(getPrefixedLanguageVariable("no-permission"));
                }
            }
        }
        catch (Exception exception) {
            Util.handleErrors(player, exception);
        }
    }
}
