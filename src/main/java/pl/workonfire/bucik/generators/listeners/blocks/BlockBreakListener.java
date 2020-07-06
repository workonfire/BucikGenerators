package pl.workonfire.bucik.generators.listeners.blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;
import pl.workonfire.bucik.generators.managers.utils.Util;

public class BlockBreakListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        try {
            Block block = event.getBlock();
            Location supposedGeneratorLocation = block.getLocation().add(0, 1, 0);
            Location baseBlockLocation = block.getLocation().subtract(0, 1, 0);
            Generator baseGenerator = BlockUtil.getGeneratorFromMaterial(baseBlockLocation.getBlock().getType());

            if (BlockUtil.isBlockAGenerator(block.getLocation(), block.getWorld()))
                new BaseGeneratorBreakHandler(event, player, supposedGeneratorLocation).run();
            else if (baseGenerator != null && BlockUtil.isGeneratorDefined(baseGenerator.getId())
                    && BlockUtil.isBlockAGenerator(baseBlockLocation, block.getWorld()))
                new GeneratorBreakHandler(event, player, baseGenerator, baseBlockLocation).run();
        }
        catch (Exception exception) {
            Util.handleErrors(player, exception);
        }

    }
}
