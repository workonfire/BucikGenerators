package pl.workonfire.bucik.generators.commands;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.workonfire.bucik.generators.data.generator.DropItem;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getLanguageVariable;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;

@SuppressWarnings("ConstantConditions")
public class DropCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                if (player.hasPermission("bucik.generators.drop.see")) {
                    final Block targetBlock = player.getTargetBlockExact(5);
                    if (targetBlock != null && BlockUtil.isBlockAGenerator(targetBlock.getLocation(), targetBlock.getWorld())) {
                        player.sendMessage(getPrefixedLanguageVariable("items-drop-list"));
                        final Generator generator = new Generator(BlockUtil.getGeneratorFromMaterial(targetBlock.getType()).getId());
                        for (String permission : generator.getGeneratorDropPermissionList()) {
                            if (player.hasPermission(Util.getPermission(permission))) {
                                sender.sendMessage(getLanguageVariable("items-for-permission") + Util.getPermission(permission) + "§f:");
                                for (String dropItemId : generator.getDropItemsIds(permission)) {
                                    final DropItem dropItem = new DropItem(generator, permission, Integer.parseInt(dropItemId));
                                    player.sendMessage("§c§m--------------");
                                    player.sendMessage(getLanguageVariable("drop-item-material") + dropItem.getItemMaterial());
                                    if (dropItem.getItemName() != null)
                                        player.sendMessage(getLanguageVariable("drop-item-name") + Util.formatColors(dropItem.getItemName()));
                                    if (dropItem.getItemAmount() > 1)
                                        player.sendMessage(getLanguageVariable("drop-item-amount") + dropItem.getItemAmount());
                                    if (!dropItem.getEnchantments().isEmpty()) {
                                        player.sendMessage(getLanguageVariable("drop-item-enchantments"));
                                        for (String enchantment : dropItem.getEnchantments()) player.sendMessage(enchantment);
                                    }
                                    player.sendMessage(getLanguageVariable("drop-item-chance") + dropItem.getDropChance() + "%");
                                }
                                player.sendMessage("§c§m--------------");
                            }
                            else {
                                player.sendMessage(getPrefixedLanguageVariable("no-drop"));
                                return false;
                            }
                        }
                    }
                    else player.sendMessage(getPrefixedLanguageVariable("block-is-not-a-generator"));
                }
                else player.sendMessage(getPrefixedLanguageVariable("no-permission"));
            }
            else sender.sendMessage(getPrefixedLanguageVariable("cannot-open-from-console"));
            return true;
        }
        catch (Exception exception) {
            if (sender instanceof Player) Util.handleErrors((Player) sender, exception);
            else {
                sender.sendMessage(Util.getDebugMessage());
                exception.printStackTrace();
            }
            return false;
        }
    }
}