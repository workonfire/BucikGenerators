package pl.workonfire.bucik.generators.listeners.blocks;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.workonfire.bucik.generators.BucikGenerators;
import pl.workonfire.bucik.generators.data.GeneratorDurabilities;
import pl.workonfire.bucik.generators.data.GeneratorLocation;
import pl.workonfire.bucik.generators.data.generator.DropItem;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.Util;
import pl.workonfire.bucik.generators.managers.VaultHandler;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixLangVar;
import static pl.workonfire.bucik.generators.managers.utils.Util.sendMessage;

@SuppressWarnings("ConstantConditions")
public class GeneratorBreakHandler {

    private final BlockBreakEvent   event;
    private final Player            player;
    private final Generator         baseGenerator;
    private final Location          baseBlockLocation;
    private final GeneratorLocation fullBlockLocation;

    protected GeneratorBreakHandler(BlockBreakEvent event,
                                    Player player,
                                    Generator baseGenerator,
                                    Location baseBlockLocation) {
        this.event             = event;
        this.player            = player;
        this.baseGenerator     = baseGenerator;
        this.baseBlockLocation = baseBlockLocation;
        this.fullBlockLocation = GeneratorLocation.from(
                this.baseBlockLocation, this.baseBlockLocation.getWorld().getName()
        );
    }

    private void showActionBar(String text) {
        if (text != null)
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Util.formatColors(text)));
    }

    private void generate(Block block, Material material) {
        Bukkit.getScheduler().runTaskLater(BucikGenerators.getInstance(), () -> {
            if (baseBlockLocation.getBlock().getType() != Material.AIR && block.getType() == Material.AIR)
                block.setType(material);
        }, baseGenerator.getBreakCooldown());
    }

    protected void run() {
        Block block = event.getBlock();
        event.setCancelled(!baseGenerator.getItemDropMode().equalsIgnoreCase("vanilla"));
        boolean breakable = true;
        if (baseGenerator.isWhitelistOn()) {
            // checking if we can break the generator with the current tool
            breakable = false;
            for (String materialName : baseGenerator.getWhitelistedItems()) {
                Material material = Material.getMaterial(materialName);
                if (player.getInventory().getItemInMainHand().getType().equals(material)) {
                    breakable = true;
                    break;
                }
            }
        }
        if (player.hasPermission(baseGenerator.getPermission()) && breakable) {
            block.setType(Material.AIR);
            // breaking the generator and applying the cooldown
            generate(block, baseGenerator.getGeneratorMaterial());
            if (baseGenerator.isDurabilityOn() && Generator.hasDurabilityLeft(fullBlockLocation)) {
                // handling the durability system
                int currentDurability = GeneratorDurabilities.getInstance().getValue(fullBlockLocation);
                if (currentDurability == 1) {
                    baseBlockLocation.getBlock().setType(Material.AIR);
                    baseGenerator.unregister(baseBlockLocation, baseBlockLocation.getWorld());
                    sendMessage(player, getPrefixLangVar("generator-has-worn-out"));
                    Util.playSound(block, Sound.ENTITY_WITHER_HURT);
                    Util.showParticle(player, block, Particle.SMOKE_LARGE, 7);
                }
                else GeneratorDurabilities.getInstance().update(fullBlockLocation, currentDurability - 1);
            }
            Sound breakSound = Util.isServerLegacy() ? Sound.ENTITY_BLAZE_HURT : Sound.ENTITY_ENDER_DRAGON_HURT;
            Util.playSound(block, breakSound);
            if (baseGenerator.isAffectPxDurability()) {
                // subtracting durability from the item, if enabled
                ItemStack currentItem = player.getInventory().getItemInMainHand();
                ItemMeta currentItemMeta = player.getInventory().getItemInMainHand().getItemMeta();
                if (Util.isDamageable(currentItem)) {
                    int currentDamage = ((Damageable) currentItemMeta).getDamage();
                    if (currentDamage >= currentItem.getType().getMaxDurability()) {
                        player.getInventory().setItemInMainHand(null);
                        block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                    }
                    else {
                        int dropDivider = 1;
                        if (currentItem.containsEnchantment(Enchantment.DURABILITY))
                            dropDivider = currentItem.getEnchantmentLevel(Enchantment.DURABILITY);
                        currentDamage += baseGenerator.getAffectPxDurabilityValue() / dropDivider;
                        ((Damageable) currentItemMeta).setDamage(currentDamage);
                        currentItem.setItemMeta(currentItemMeta);
                        player.getInventory().setItemInMainHand(currentItem);
                    }
                }
            }
            for (String permission : baseGenerator.getGeneratorDropPermissions()) {
                if (player.hasPermission(Util.getPermission(permission))) {
                    for (String dropItemId : baseGenerator.getDropItemsIds(permission)) {
                        DropItem item = new DropItem(baseGenerator.getId(), permission, Integer.parseInt(dropItemId));
                        // dropping the items, if the user has permission and the item got selected etc.
                        if (item.gotSelected(player.getInventory().getItemInMainHand(), baseGenerator.isRespectPickaxeFortune())) {
                            // checking for each item type (potion, money, exp etc.)
                            if (item.isPotion() && item.getPotionEffectTypeName() != null) {
                                PotionEffect potionEffect = new PotionEffect(
                                        PotionEffectType.getByName(item.getPotionEffectTypeName()),
                                        item.getPotionEffectDuration() * 20,
                                        item.getPotionEffectAmplifier()
                                );
                                player.addPotionEffect(potionEffect);
                            }
                            else if (item.isMoney() && item.getMoneyAmount() != 0 && VaultHandler.getEconomy() != null) {
                                VaultHandler.getEconomy().depositPlayer(player, item.getMoneyAmount());
                                if (!Util.isServerLegacy()) Util.showParticle(player, block, Particle.TOTEM, 1);
                            }
                            else if (item.isExp() && item.getExpAmount() != 0)
                                player.giveExp(item.getExpAmount());
                            else {
                                if (baseGenerator.getItemDropMode() != null && baseGenerator.getItemDropMode().equalsIgnoreCase("inventory")) {
                                    if (player.getInventory().firstEmpty() == -1) // whether the inventory is full
                                        block.getWorld().dropItemNaturally(baseBlockLocation, item.getItemStack());
                                    else player.getInventory().addItem(item.getItemStack());
                                }
                                else
                                    block.getWorld().dropItemNaturally(baseBlockLocation, item.getItemStack());
                            }
                            showActionBar(item.getActionBarMessage());
                        }
                    }
                }
            }
        }
        else sendMessage(player, getPrefixLangVar("no-permission"));
    }
}
