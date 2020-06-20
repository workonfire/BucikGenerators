package pl.workonfire.bucik.generators.listeners.blocks;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import pl.workonfire.bucik.generators.Main;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;
import pl.workonfire.bucik.generators.managers.ConfigManager;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;

@SuppressWarnings("ConstantConditions")
public class BlockPlaceListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        try {
            final Block block = event.getBlock();

            if (BlockUtil.isHeldBlockAGenerator(event.getItemInHand())) {
                final Generator generator = BlockUtil.getGeneratorFromMaterial(block.getType());
                for (String worldName : generator.getWorldBlacklist()) {
                    if (block.getWorld().getName().equals(worldName)) {
                        event.setCancelled(true);
                        player.sendMessage(getPrefixedLanguageVariable("cannot-place-in-this-world"));
                        if (ConfigManager.areSoundsEnabled()) {
                            final Sound placeSound = Util.isServerLegacy() ? Sound.ENTITY_BAT_DEATH : Sound.ITEM_TRIDENT_THUNDER;
                            player.playSound(player.getLocation(), placeSound, 1.0F, 1.0F);
                        }
                        return;
                    }
                }
                if (player.hasPermission(generator.getPermission())) {
                    final Location supposedBaseGeneratorLocation = block.getLocation().subtract(0, 1, 0);
                    if (BlockUtil.isBlockAGenerator(supposedBaseGeneratorLocation, supposedBaseGeneratorLocation.getWorld()))
                        event.setCancelled(true);
                    else {
                        if (ConfigManager.areSoundsEnabled()) {
                            final Sound placeSound = Util.isServerLegacy() ? Sound.ITEM_FIRECHARGE_USE : Sound.BLOCK_BEACON_ACTIVATE;
                            block.getWorld().playSound(player.getLocation(), placeSound, 1.0F, 1.0F);
                        }
                        if (ConfigManager.areParticlesEnabled())
                            player.spawnParticle(Particle.END_ROD, block.getLocation(), 25);
                        player.sendMessage(getPrefixedLanguageVariable("generator-placed") + generator.getId());
                        generator.register(block.getLocation(), block.getWorld());
                        final Location generatorLocation = block.getLocation().add(0, 1, 0);
                        generatorLocation.getBlock().setType(generator.getGeneratorMaterial());
                        if (generator.isDurabilityEnabled() && generator.getDurability() != 0)
                            block.setMetadata("durability", new FixedMetadataValue(Main.getPlugin(), generator.getDurability()));
                    }
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
