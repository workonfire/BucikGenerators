package pl.workonfire.bucik.generators.listeners.blocks;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.workonfire.bucik.generators.Main;
import pl.workonfire.bucik.generators.data.generator.DropItem;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;
import pl.workonfire.bucik.generators.managers.ConfigManager;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;

@SuppressWarnings("ConstantConditions")
public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        try {
            final Block block = event.getBlock();

            final Location supposedGeneratorLocation = block.getLocation().add(0, 1, 0);
            final Location baseBlockLocation = block.getLocation().subtract(0, 1, 0);

            final Generator baseGenerator = BlockUtil.getGeneratorFromMaterial(baseBlockLocation.getBlock().getType());

            if (BlockUtil.isBlockAGenerator(block.getLocation(), block.getWorld())) {
                final Generator generator = BlockUtil.getGeneratorFromMaterial(block.getType());
                if (player.hasPermission(generator.getPermission())) {
                    generator.unregister(block.getLocation(), block.getWorld());
                    event.setCancelled(true);
                    supposedGeneratorLocation.getBlock().setType(Material.AIR);
                    block.setType(Material.AIR);
                    block.getWorld().dropItemNaturally(block.getLocation(), generator.getItemStack(1));
                    if (ConfigManager.areSoundsEnabled())
                        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
                    if (ConfigManager.areParticlesEnabled())
                        player.spawnParticle(Particle.SMOKE_LARGE, event.getBlock().getLocation(), 7);
                    player.sendMessage(getPrefixedLanguageVariable("base-generator-destroyed"));
                }
                else {
                    event.setCancelled(true);
                    player.sendMessage(getPrefixedLanguageVariable("no-permission"));
                }
            }
            else if (baseGenerator != null && BlockUtil.isGeneratorDefined(baseGenerator.getId())
                    && BlockUtil.isBlockAGenerator(baseBlockLocation, block.getWorld())) {
                event.setCancelled(true);
                if (player.hasPermission(baseGenerator.getPermission())) {
                    final int breakCooldown = baseGenerator.getBreakCooldown();
                    block.setType(Material.AIR);
                    Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                        if (baseBlockLocation.getBlock().getType() != Material.AIR && block.getType().equals(Material.AIR))
                            block.setType(baseGenerator.getGeneratorMaterial());
                    }, breakCooldown);
                    if (ConfigManager.areSoundsEnabled())
                         block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 1.0F, 1.0F);
                    for (String permission : baseGenerator.getGeneratorDropPermissionList()) {
                        if (player.hasPermission(Util.getPermission(permission))) {
                            for (String dropItemId : baseGenerator.getDropItemsIds(permission)) {
                                DropItem item = new DropItem(baseGenerator, permission, Integer.parseInt(dropItemId));
                                if (item.gotSelected()) {
                                    if (item.isAPotion() && item.getPotionEffectTypeName() != null) {
                                        final PotionEffect potionEffect = new PotionEffect(
                                                PotionEffectType.getByName(item.getPotionEffectTypeName()),
                                                item.getPotionEffectDuration(),
                                                item.getPotionEffectAmplifier());
                                        player.addPotionEffect(potionEffect);
                                    }
                                    else {
                                        if (baseGenerator.getItemDropMode() != null
                                                && baseGenerator.getItemDropMode().equalsIgnoreCase("inventory"))
                                            player.getInventory().addItem(item.getItemStack());
                                        else
                                            block.getWorld().dropItemNaturally(baseBlockLocation, item.getItemStack());
                                        if (item.getActionBarMessage() != null)
                                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                                    TextComponent.fromLegacyText(Util.formatColors(item.getActionBarMessage())));
                                    }
                                }
                            }
                        }
                    }
                }
                else player.sendMessage(getPrefixedLanguageVariable("no-permission"));
            }
        }
        catch (Exception exception) {
            Util.handleErrors(player, exception);
        }

    }
}