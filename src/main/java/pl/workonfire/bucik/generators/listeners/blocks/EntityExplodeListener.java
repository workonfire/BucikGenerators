package pl.workonfire.bucik.generators.listeners.blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;
import pl.workonfire.bucik.generators.managers.utils.Logger;
import pl.workonfire.bucik.generators.managers.utils.Util;

@SuppressWarnings("ConstantConditions")
public class EntityExplodeListener implements Listener {

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (!event.blockList().isEmpty()) {
            for (Block block : event.blockList()) {
                final Location baseBlockLocation = block.getLocation().subtract(0, 1, 0);
                final Generator baseGenerator = BlockUtil.getGeneratorFromMaterial(baseBlockLocation.getBlock().getType());
                if (BlockUtil.isBlockAGenerator(block.getLocation(), block.getWorld())) {
                    final Generator generator = new Generator(BlockUtil.getGeneratorFromMaterial(block.getType()).getId());
                    if (generator.isDurabilityEnabled()) {
                        Util.systemMessage(Logger.DEBUG, event.getEventName() + ": This generator has durability enabled. Cancelling the event.");
                        event.setCancelled(true);
                    }
                    else generator.unregister(block.getLocation(), block.getWorld());
                }
                else if (baseGenerator != null && BlockUtil.isBlockAGenerator(baseBlockLocation, baseBlockLocation.getWorld())
                        && !baseGenerator.isDurabilityEnabled())
                    baseGenerator.unregister(baseBlockLocation, block.getWorld());
            }
        }
    }
}
