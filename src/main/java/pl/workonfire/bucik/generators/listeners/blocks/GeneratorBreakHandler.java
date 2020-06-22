package pl.workonfire.bucik.generators.listeners.blocks;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.workonfire.bucik.generators.Main;
import pl.workonfire.bucik.generators.data.generator.DropItem;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.Util;
import pl.workonfire.bucik.generators.managers.utils.VaultHandler;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;

@SuppressWarnings("ConstantConditions")
public class GeneratorBreakHandler {

    private final BlockBreakEvent event;
    private final Player player;
    private final Generator baseGenerator;
    private final Location baseBlockLocation;

    protected GeneratorBreakHandler(BlockBreakEvent event, Player player, Generator baseGenerator, Location baseBlockLocation) {
        this.event = event;
        this.player = player;
        this.baseGenerator = baseGenerator;
        this.baseBlockLocation = baseBlockLocation;
    }

    protected void run() {
        Block block = event.getBlock();
        event.setCancelled(true);
        if (player.hasPermission(baseGenerator.getPermission())) {
            block.setType(Material.AIR);
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                if (baseBlockLocation.getBlock().getType() != Material.AIR && block.getType() == Material.AIR)
                    block.setType(baseGenerator.getGeneratorMaterial());
            }, baseGenerator.getBreakCooldown());
            if (baseGenerator.isDurabilityEnabled() && baseBlockLocation.getBlock().hasMetadata("durability")) {
                int currentDurability = 0;
                for (MetadataValue value : baseBlockLocation.getBlock().getMetadata("durability"))
                    currentDurability = value.asInt();
                if (currentDurability == 1) {
                    baseBlockLocation.getBlock().setType(Material.AIR);
                    baseGenerator.unregister(baseBlockLocation, baseBlockLocation.getWorld());
                    player.sendMessage(getPrefixedLanguageVariable("generator-has-worn-out"));
                    Util.playSound(block, Sound.ENTITY_WITHER_HURT);
                    Util.showParticle(player, block, Particle.SMOKE_LARGE, 7);
                }
                else {
                    baseBlockLocation.getBlock().removeMetadata("durability", Main.getPlugin());
                    baseBlockLocation.getBlock().setMetadata("durability",
                            new FixedMetadataValue(Main.getPlugin(), currentDurability - 1));
                }
            }
            Sound breakSound = Util.isServerLegacy() ? Sound.ENTITY_BLAZE_HURT : Sound.ENTITY_ENDER_DRAGON_HURT;
            Util.playSound(block, breakSound);
            if (baseGenerator.isAffectPickaxeDurabilityEnabled()) {
                ItemStack currentItem = player.getInventory().getItemInMainHand();
                ItemMeta currentItemMeta = player.getInventory().getItemInMainHand().getItemMeta();
                if (currentItem.getType() == Material.DIAMOND_PICKAXE
                        || currentItem.getType() == Material.GOLDEN_PICKAXE
                        || currentItem.getType() == Material.IRON_PICKAXE
                        || currentItem.getType() == Material.STONE_PICKAXE
                        || currentItem.getType() == Material.WOODEN_PICKAXE) {
                    int currentDamage = ((Damageable) currentItemMeta).getDamage();
                    if (currentDamage >= currentItem.getType().getMaxDurability()) {
                        player.getInventory().setItemInMainHand(null);
                        block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                    }
                    else {
                        ((Damageable) currentItemMeta).setDamage(currentDamage + baseGenerator.getAffectPickaxeDurabilityValue());
                        currentItem.setItemMeta(currentItemMeta);
                        player.getInventory().setItemInMainHand(currentItem);
                    }
                }
            }
            for (String permission : baseGenerator.getGeneratorDropPermissionList()) {
                if (player.hasPermission(Util.getPermission(permission))) {
                    for (String dropItemId : baseGenerator.getDropItemsIds(permission)) {
                        DropItem item = new DropItem(baseGenerator, permission, Integer.parseInt(dropItemId));
                        if (item.gotSelected()) {
                            if (item.isAPotion() && item.getPotionEffectTypeName() != null) {
                                PotionEffect potionEffect = new PotionEffect(
                                        PotionEffectType.getByName(item.getPotionEffectTypeName()),
                                        item.getPotionEffectDuration() * 20,
                                        item.getPotionEffectAmplifier());
                                player.addPotionEffect(potionEffect);
                            }
                            else if (item.isMoney() && item.getMoneyAmount() != 0 && VaultHandler.getEconomy() != null) {
                                VaultHandler.getEconomy().depositPlayer(player, item.getMoneyAmount());
                                if (!Util.isServerLegacy()) Util.showParticle(player, block, Particle.TOTEM, 1);
                            }
                            else {
                                if (baseGenerator.getItemDropMode() != null
                                        && baseGenerator.getItemDropMode().equalsIgnoreCase("inventory"))
                                    player.getInventory().addItem(item.getItemStack());
                                else
                                    block.getWorld().dropItemNaturally(baseBlockLocation, item.getItemStack());
                            }
                            if (item.getActionBarMessage() != null)
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                        TextComponent.fromLegacyText(Util.formatColors(item.getActionBarMessage())));
                        }
                    }
                }
            }
        }
        else player.sendMessage(getPrefixedLanguageVariable("no-permission"));
    }
}
