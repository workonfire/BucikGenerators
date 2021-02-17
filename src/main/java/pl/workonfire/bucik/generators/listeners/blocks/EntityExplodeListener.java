package pl.workonfire.bucik.generators.listeners.blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.workonfire.bucik.generators.BucikGenerators;
import pl.workonfire.bucik.generators.data.GeneratorLocation;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;

@SuppressWarnings("ConstantConditions")
public class EntityExplodeListener implements Listener {

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (!event.blockList().isEmpty()) {
            for (Block block : event.blockList()) {
                Location baseBlockLocation = block.getLocation().subtract(0, 1, 0);
                Generator baseGenerator = BlockUtil.getGeneratorFromMaterial(baseBlockLocation.getBlock().getType());
                if (BlockUtil.isBlockAGenerator(block.getLocation(), block.getWorld())) {
                    Generator generator = new Generator(BlockUtil.getGeneratorFromMaterial(block.getType()).getId());
                    generator.unregister(block.getLocation(), block.getWorld());
                    GeneratorLocation fullLocation = BlockUtil.convertLocation(block.getLocation(), block.getWorld().getName());
                    BucikGenerators.getGeneratorDurabilities().unregister(fullLocation);
                }
                else if (baseGenerator != null && BlockUtil.isBlockAGenerator(baseBlockLocation, baseBlockLocation.getWorld())
                        && !baseGenerator.isDurabilityEnabled())
                    baseGenerator.unregister(baseBlockLocation, block.getWorld());
            }
        }
    }
}
