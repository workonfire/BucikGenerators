package pl.workonfire.bucik.generators.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import pl.workonfire.bucik.generators.data.Generator;
import pl.workonfire.bucik.generators.managers.BlockUtil;

public class PistonExtendListener implements Listener {

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (!event.getBlocks().isEmpty()) {
            for (Block block : event.getBlocks()) {
                final Location baseBlockLocation = block.getLocation().subtract(0, 1, 0);
                final Generator baseGenerator = BlockUtil.getGeneratorFromMaterial(baseBlockLocation.getBlock().getType());
                if (BlockUtil.isBlockAGenerator(block.getLocation(), block.getWorld()))
                    event.setCancelled(true);
                else if (baseGenerator != null && BlockUtil.isGeneratorDefined(baseGenerator.getId()))
                    event.setCancelled(true);
            }
        }
    }
}
