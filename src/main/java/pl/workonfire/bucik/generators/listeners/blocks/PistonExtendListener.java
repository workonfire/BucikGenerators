package pl.workonfire.bucik.generators.listeners.blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;

public class PistonExtendListener implements Listener {

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (!event.getBlocks().isEmpty()) {
            for (Block block : event.getBlocks()) {
                final Location baseBlockLocation = block.getLocation().subtract(0, 1, 0);
                final Generator baseGenerator = BlockUtil.getGeneratorFromMaterial(baseBlockLocation.getBlock().getType());
                if (BlockUtil.isBlockAGenerator(block.getLocation(), block.getWorld()))
                    event.setCancelled(true);
                else if (baseGenerator != null && BlockUtil.isGeneratorDefined(baseGenerator.getId())
                        && BlockUtil.isBlockAGenerator(block.getLocation(), block.getWorld()))
                    event.setCancelled(true);
            }
        }
    }
}
