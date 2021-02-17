package pl.workonfire.bucik.generators.listeners.blocks;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import pl.workonfire.bucik.generators.data.GeneratorDurabilities;
import pl.workonfire.bucik.generators.data.GeneratorLocation;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;
import static pl.workonfire.bucik.generators.managers.utils.Util.sendMessage;

@SuppressWarnings("ConstantConditions")
public class BlockPlaceListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        try {
            Block block = event.getBlock();

            if (BlockUtil.isItemAGenerator(event.getItemInHand())) {
                Generator generator = BlockUtil.getGeneratorFromMaterial(block.getType());
                for (String worldName : generator.getWorldBlacklist()) {
                    if (block.getWorld().getName().equals(worldName)) {
                        event.setCancelled(true);
                        sendMessage(player, getPrefixedLanguageVariable("cannot-place-in-this-world"));
                        Sound placeSound = Util.isServerLegacy() ? Sound.ENTITY_BAT_DEATH : Sound.ITEM_TRIDENT_THUNDER;
                        Util.playSound(player, placeSound);
                        return;
                    }
                }
                if (player.hasPermission(generator.getPermission())) {
                    Location supposedBaseGeneratorLocation = block.getLocation().subtract(0, 1, 0);
                    if (BlockUtil.isBlockAGenerator(supposedBaseGeneratorLocation, supposedBaseGeneratorLocation.getWorld()))
                        event.setCancelled(true);
                    else {
                        Sound placeSound = Util.isServerLegacy() ? Sound.ITEM_FIRECHARGE_USE : Sound.BLOCK_BEACON_ACTIVATE;
                        Util.playSound(block, placeSound);
                        Util.showParticle(player, block, Particle.END_ROD, 25);
                        sendMessage(player, getPrefixedLanguageVariable("generator-placed") + generator.getId());
                        Location generatorLocation = block.getLocation().add(0, 1, 0);
                        if (generatorLocation.getBlock().getType() == Material.BEDROCK
                                || BlockUtil.isBlockAGenerator(generatorLocation, generatorLocation.getWorld())) {
                            event.setCancelled(true);
                            sendMessage(player, getPrefixedLanguageVariable("no-permission"));
                            return;
                        }
                        generator.register(block.getLocation(), block.getWorld());
                        generatorLocation.getBlock().setType(generator.getGeneratorMaterial());
                        if (generator.isDurabilityEnabled() && generator.getDurability() != 0) {
                            GeneratorLocation fullLocation =
                                    BlockUtil.convertLocation(block.getLocation(), block.getWorld().getName());
                            GeneratorDurabilities.getInstance().update(fullLocation, generator.getDurability());
                        }
                    }
                }
                else {
                    event.setCancelled(true);
                    sendMessage(player, getPrefixedLanguageVariable("no-permission"));
                }
            }
        }
        catch (Exception exception) {
            Util.handleErrors(player, exception);
        }
    }
}
