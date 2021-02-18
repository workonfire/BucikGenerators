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

import static pl.workonfire.bucik.generators.managers.ConfigManager.getLangVar;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLangVar;
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
        if (Util.isAuthorized(sender, permission())) {
            if (Util.isPlayer(sender)) {
                try {
                    Player player = (Player) sender;
                    Block targetBlock;
                    try {
                        targetBlock = player.getTargetBlockExact(5);
                    }
                    catch (NoSuchMethodError error) {
                        Util.systemMessage(Logger.DEBUG, error.getClass().getSimpleName() + ": Using the non-exact target block method.");
                        targetBlock = player.getTargetBlock(null, 5);
                    }
                    if (targetBlock != null && BlockUtil.isBlockAGenerator(targetBlock.getLocation(), targetBlock.getWorld())) {
                        sendMessage(sender, getPrefixedLangVar("items-drop-list"));
                        Generator generator = new Generator(BlockUtil.getGeneratorFromMaterial(targetBlock.getType()).getId());
                        for (String permission : generator.getGeneratorDropPermissions()) {
                            if (player.hasPermission(Util.getPermission(permission))) {
                                sendMessage(sender, getLangVar("items-for-permission") + Util.getPermission(permission) + "§f:");
                                for (String dropItemId : generator.getDropItemsIds(permission)) {
                                    DropItem dropItem = new DropItem(generator.getId(), permission, Integer.parseInt(dropItemId));
                                    sendMessage(sender, "§c§m--------------");
                                    try {
                                        ComponentBuilder componentBuilder = new ComponentBuilder();
                                        List<String> finalHoverMessage = new ArrayList<>();
                                        TextComponent dropHover = new TextComponent(getLangVar("drop-item-material") + dropItem.getMaterialName());
                                        if (dropItem.getItemName() != null) {
                                            finalHoverMessage.add(getLangVar("drop-item-name") + Util.formatColors(dropItem.getItemName()));
                                            finalHoverMessage.add("\n");
                                        }
                                        if (dropItem.getAmount() > 1) {
                                            finalHoverMessage.add(getLangVar("drop-item-amount") + dropItem.getAmount());
                                            finalHoverMessage.add("\n");
                                        }
                                        if (dropItem.isAPotion()) {
                                            finalHoverMessage.add(getLangVar("potion-type") + dropItem.getPotionEffectTypeName() + "\n");
                                            finalHoverMessage.add(getLangVar("potion-amplifier") + dropItem.getPotionEffectAmplifier() + "x\n");
                                            finalHoverMessage.add(getLangVar("potion-duration") + dropItem.getPotionEffectDuration() + "s");
                                            finalHoverMessage.add("\n");
                                        }
                                        else if (dropItem.isMoney() && VaultHandler.getEconomy() != null) {
                                            finalHoverMessage.add(getLangVar("drop-item-money-amount") + dropItem.getMoneyAmount());
                                            finalHoverMessage.add("\n");
                                        }
                                        else if (dropItem.isExp()) {
                                            finalHoverMessage.add(getLangVar("drop-item-exp-amount") + dropItem.getExpAmount());
                                            finalHoverMessage.add("\n");
                                        }
                                        if (!dropItem.getEnchantments().isEmpty()) {
                                            finalHoverMessage.add(getLangVar("drop-item-enchantments"));
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
                                        sendMessage(sender, getLangVar("drop-item-material") + dropItem.getMaterialName());
                                        if (dropItem.getItemName() != null)
                                            sendMessage(sender, getLangVar("drop-item-name") + Util.formatColors(dropItem.getItemName()));
                                        if (dropItem.getAmount() > 1)
                                            sendMessage(sender, getLangVar("drop-item-amount") + dropItem.getAmount());
                                        if (dropItem.isAPotion()) {
                                            sendMessage(sender, getLangVar("potion-type") + dropItem.getPotionEffectTypeName());
                                            sendMessage(sender, getLangVar("potion-amplifier") + dropItem.getPotionEffectAmplifier() + "x");
                                            sendMessage(sender, getLangVar("potion-duration") + dropItem.getPotionEffectDuration() + "s");
                                        }
                                        if (dropItem.isMoney() && VaultHandler.getEconomy() != null)
                                            sendMessage(sender, getLangVar("drop-item-money-amount") + dropItem.getMoneyAmount());
                                        if (!dropItem.getEnchantments().isEmpty()) {
                                            sendMessage(sender, getLangVar("drop-item-enchantments"));
                                            for (String enchantment : dropItem.getEnchantments()) player.sendMessage(enchantment);
                                        }
                                    }
                                    sendMessage(sender, getLangVar("drop-item-chance") + dropItem.getDropChance() + "%");
                                }
                                sendMessage(sender, "§c§m--------------");
                            }
                            else {
                                sendMessage(sender, getPrefixedLangVar("no-drop"));
                                return;
                            }
                        }
                    }
                    else sendMessage(sender, getPrefixedLangVar("block-is-not-a-generator"));
                }
                catch (Exception exception) {
                    Util.handleErrors(sender, exception);
                }
            }
        }
    }
}
