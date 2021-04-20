package pl.workonfire.bucik.generators.listeners.blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.workonfire.bucik.generators.BucikGenerators;
import pl.workonfire.bucik.generators.data.GeneratorLocation;
import pl.workonfire.bucik.generators.data.generator.Generator;

public class EntityExplodeListener implements Listener {

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (!event.blockList().isEmpty()) {
            for (Block block : event.blockList()) {
                Location baseBlockLocation = block.getLocation().subtract(0, 1, 0);
                Generator baseGenerator = Generator.from(baseBlockLocation.getBlock().getType());
                if (Generator.isGenerator(block.getLocation(), block.getWorld())) {
                    Generator generator = Generator.from(block.getType());
                    generator.unregister(block.getLocation(), block.getWorld());
                    GeneratorLocation fullLocation = GeneratorLocation.from(block.getLocation(), block.getWorld().getName());
                    BucikGenerators.getGeneratorDurabilities().unregister(fullLocation);
                }
                else if (baseGenerator != null && Generator.isGenerator(baseBlockLocation, baseBlockLocation.getWorld())
                        && !baseGenerator.isDurabilityOn())
                    baseGenerator.unregister(baseBlockLocation, block.getWorld());
            }
        }
    }
}
