package pl.workonfire.bucik.generators.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.workonfire.bucik.generators.managers.Util;

import static java.lang.String.format;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getGeneratorsConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("ConstantConditions")
public class DropItem {
    private final double dropChance;
    private final Material itemMaterial;
    private final int itemAmount;
    private final String itemName;
    private final List<String> itemLore;
    private final String actionBarMessage;

    public DropItem(Generator generator, String permission, int id) {
        dropChance = getGeneratorsConfig().getDouble(format("generators.%s.generator.drop.%s.%d.chance", generator.getId(), permission, id));
        itemMaterial = Material.getMaterial(getGeneratorsConfig().getString(format("generators.%s.generator.drop.%s.%d.item", generator.getId(), permission, id)));
        itemAmount = getGeneratorsConfig().getInt(format("generators.%s.generator.drop.%s.%d.amount", generator.getId(), permission, id));
        itemName = getGeneratorsConfig().getString(format("generators.%s.generator.drop.%s.%d.name", generator.getId(), permission, id));
        itemLore = getGeneratorsConfig().getStringList(format("generators.%s.generator.drop.%s.%d.lore", generator.getId(), permission, id));
        actionBarMessage = getGeneratorsConfig().getString(format("generators.%s.generator.drop.%s.%d.action-bar-message", generator.getId(), permission, id));
    }

    /**
     * Creates an ItemStack from a dropped item.
     * @since 1.0.0
     * @return ItemStack object
     */
    public ItemStack getItemStack() {
        final ItemStack item = new ItemStack(getItemMaterial());
        final ItemMeta itemMeta = item.getItemMeta();
        if (itemName != null) itemMeta.setDisplayName(Util.formatColors(getItemName()));
        if (itemLore != null) itemMeta.setLore(getItemLore());
        item.setItemMeta(itemMeta);
        item.setAmount(getItemAmount());
        return item;
    }

    public boolean gotSelected() {
        return Math.round(new Random().nextDouble() * 10000.0) / 100.0 <= getDropChance();
    }

    public double getDropChance() {
        return dropChance * DropMultiplier.getDropMultiplier();
    }

    public Material getItemMaterial() {
        return itemMaterial;
    }

    public int getItemAmount() {
        return (itemAmount == 0) ? 1 : itemAmount;
    }

    public String getItemName() {
        return itemName;
    }

    public List<String> getItemLore() {
        final List<String> formattedLore = new ArrayList<>();
        for (String loreLine : itemLore) formattedLore.add(Util.formatColors(loreLine));
        return formattedLore;
    }

    public String getActionBarMessage() {
        return actionBarMessage;
    }
}
