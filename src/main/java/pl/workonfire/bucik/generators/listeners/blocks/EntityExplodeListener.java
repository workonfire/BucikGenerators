package pl.workonfire.bucik.generators.listeners.blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;

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
                    generator.unregister(block.getLocation(), block.getWorld());
                    System.out.println("Wyrejestrowano generator.");
                    //event.setCancelled(true);
                }
                else if (baseGenerator != null && BlockUtil.isGeneratorDefined(baseGenerator.getId())
                        && BlockUtil.isBlockAGenerator(block.getLocation(), block.getWorld())) {
                    baseGenerator.unregister(baseBlockLocation, block.getWorld());
                    System.out.println("Wyrejestrowano generator.");
                }
                    //event.setCancelled(true);
            }
        }
    }
}
