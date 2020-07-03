package pl.workonfire.bucik.generators.commands.drop;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.workonfire.bucik.generators.data.generator.DropItem;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.*;

import java.util.ArrayList;
import java.util.List;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getLanguageVariable;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;
import static pl.workonfire.bucik.generators.managers.utils.Util.sendMessage;

@SuppressWarnings("ConstantConditions")
public class DropPeekCommand implements CommandExecutor, CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        run(sender, args);
        return true;
    }

    @Override
    public String permission() {
        return "bucik.generators.drop.see";
    }

    @Override
    public String name() {
        return "drop";
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        try {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission(permission())) {
                    Block targetBlock;
                    try {
                        targetBlock = player.getTargetBlockExact(5);
                    }
                    catch (NoSuchMethodError error) {
                        Util.systemMessage(Logger.DEBUG, error.getClass().getSimpleName() + ": Using the non-exact target block method.");
                        targetBlock = player.getTargetBlock(null, 5);
                    }
                    if (targetBlock != null && BlockUtil.isBlockAGenerator(targetBlock.getLocation(), targetBlock.getWorld())) {
                        sendMessage(sender, getPrefixedLanguageVariable("items-drop-list"));
                        Generator generator = new Generator(BlockUtil.getGeneratorFromMaterial(targetBlock.getType()).getId());
                        for (String permission : generator.getGeneratorDropPermissionList()) {
                            if (player.hasPermission(Util.getPermission(permission))) {
                                sendMessage(sender, getLanguageVariable("items-for-permission") + Util.getPermission(permission) + "§f:");
                                for (String dropItemId : generator.getDropItemsIds(permission)) {
                                    DropItem dropItem = new DropItem(generator, permission, Integer.parseInt(dropItemId));
                                    sendMessage(sender, "§c§m--------------");
                                    try {
                                        ComponentBuilder componentBuilder = new ComponentBuilder();
                                        List<String> finalHoverMessage = new ArrayList<>();
                                        TextComponent dropHover = new TextComponent(getLanguageVariable("drop-item-material") + dropItem.getItemMaterialName());
                                        if (dropItem.getItemName() != null) {
                                            finalHoverMessage.add(getLanguageVariable("drop-item-name") + Util.formatColors(dropItem.getItemName()));
                                            finalHoverMessage.add("\n");
                                        }
                                        if (dropItem.getItemAmount() > 1) {
                                            finalHoverMessage.add(getLanguageVariable("drop-item-amount") + dropItem.getItemAmount());
                                            finalHoverMessage.add("\n");
                                        }
                                        if (dropItem.isAPotion()) {
                                            finalHoverMessage.add(getLanguageVariable("potion-type") + dropItem.getPotionEffectTypeName() + "\n");
                                            finalHoverMessage.add(getLanguageVariable("potion-amplifier") + dropItem.getPotionEffectAmplifier() + "x\n");
                                            finalHoverMessage.add(getLanguageVariable("potion-duration") + dropItem.getPotionEffectDuration() + "s");
                                            finalHoverMessage.add("\n");
                                        }
                                        if (dropItem.isMoney() && VaultHandler.getEconomy() != null) {
                                            finalHoverMessage.add(getLanguageVariable("drop-item-money-amount") + dropItem.getMoneyAmount());
                                            finalHoverMessage.add("\n");
                                        }
                                        if (!dropItem.getEnchantments().isEmpty()) {
                                            finalHoverMessage.add(getLanguageVariable("drop-item-enchantments"));
                                            finalHoverMessage.add("\n");
                                            for (String enchantment : dropItem.getEnchantments()) {
                                                finalHoverMessage.add(enchantment);
                                                finalHoverMessage.add("\n");
                                            }
                                        }
                                        if (!finalHoverMessage.isEmpty() && finalHoverMessage.get(finalHoverMessage.size() - 1).equals("\n"))
                                            finalHoverMessage.remove(finalHoverMessage.size() - 1);
                                        for (String message : finalHoverMessage) componentBuilder.append(message);
                                        dropHover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, componentBuilder.create()));
                                        player.spigot().sendMessage(dropHover);
                                    }
                                    catch (NoSuchMethodError error) {
                                        sendMessage(sender, getLanguageVariable("drop-item-material") + dropItem.getItemMaterialName());
                                        if (dropItem.getItemName() != null)
                                            sendMessage(sender, getLanguageVariable("drop-item-name") + Util.formatColors(dropItem.getItemName()));
                                        if (dropItem.getItemAmount() > 1)
                                            sendMessage(sender, getLanguageVariable("drop-item-amount") + dropItem.getItemAmount());
                                        if (dropItem.isAPotion()) {
                                            sendMessage(sender, getLanguageVariable("potion-type") + dropItem.getPotionEffectTypeName());
                                            sendMessage(sender, getLanguageVariable("potion-amplifier") + dropItem.getPotionEffectAmplifier() + "x");
                                            sendMessage(sender, getLanguageVariable("potion-duration") + dropItem.getPotionEffectDuration() + "s");
                                        }
                                        if (dropItem.isMoney() && VaultHandler.getEconomy() != null)
                                            sendMessage(sender, getLanguageVariable("drop-item-money-amount") + dropItem.getMoneyAmount());
                                        if (!dropItem.getEnchantments().isEmpty()) {
                                            sendMessage(sender, getLanguageVariable("drop-item-enchantments"));
                                            for (String enchantment : dropItem.getEnchantments()) player.sendMessage(enchantment);
                                        }
                                    }
                                    sendMessage(sender, getLanguageVariable("drop-item-chance") + dropItem.getDropChance() + "%");
                                }
                                sendMessage(sender, "§c§m--------------");
                            }
                            else {
                                sendMessage(sender, getPrefixedLanguageVariable("no-drop"));
                                return;
                            }
                        }
                    }
                    else sendMessage(sender, getPrefixedLanguageVariable("block-is-not-a-generator"));
                }
                else sendMessage(sender, getPrefixedLanguageVariable("no-permission"));
            }
            else sendMessage(sender, getPrefixedLanguageVariable("cannot-open-from-console"));
        }
        catch (Exception exception) {
            Util.handleErrors(sender, exception);
        }
    }
}
