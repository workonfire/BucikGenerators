package pl.workonfire.bucik.generators.listeners.blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;
import pl.workonfire.bucik.generators.managers.utils.Logger;
import pl.workonfire.bucik.generators.managers.utils.Util;

public class PistonExtendListener implements Listener {

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (!event.getBlocks().isEmpty()) {
            for (Block block : event.getBlocks()) {
                final Location baseBlockLocation = block.getLocation().subtract(0, 1, 0);
                final Generator baseGenerator = BlockUtil.getGeneratorFromMaterial(baseBlockLocation.getBlock().getType());
                if (BlockUtil.isBlockAGenerator(block.getLocation(), block.getWorld())) {
                    Util.systemMessage(Logger.DEBUG, event.getEventName() + ": This block is a generator. Cancelling the event.");
                    event.setCancelled(true);
                }
                else if (baseGenerator != null && BlockUtil.isBlockAGenerator(baseBlockLocation, baseBlockLocation.getWorld())) {
                    Util.systemMessage(Logger.DEBUG, event.getEventName() + ": The block underneath this block is a generator. Cancelling the event.");
                    event.setCancelled(true);
                }
            }
        }
    }
}
