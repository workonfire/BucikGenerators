package pl.workonfire.bucik.generators.commands.drop;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.workonfire.bucik.generators.data.generator.DropItem;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.*;

import java.util.ArrayList;
import java.util.List;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getLangVar;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixLangVar;
import static pl.workonfire.bucik.generators.managers.utils.Util.sendMessage;

@SuppressWarnings("ConstantConditions")
public class DropPeekCommand implements CommandExecutor, Command {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
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
                        sendMessage(sender, getPrefixLangVar("items-drop-list"));
                        Generator generator = new Generator(BlockUtil.getGeneratorFromMaterial(targetBlock.getType()).getId());
                        for (String permission : generator.getGeneratorDropPermissions()) {
                            if (player.hasPermission(Util.getPermission(permission))) {
                                sendMessage(sender, getLangVar("items-for-permission") + Util.getPermission(permission) + "§f:");
                                for (String dropItemId : generator.getDropItemsIds(permission)) {
                                    DropItem dropItem = new DropItem(generator.getId(), permission, Integer.parseInt(dropItemId));
                                    sendMessage(sender, "§c§m--------------");
                                    try {
                                        ComponentBuilder componentBuilder = new ComponentBuilder();
                                        List<String> hoverMsg = new ArrayList<>();
                                        TextComponent dropHover = new TextComponent(getLangVar("drop-item-material") + dropItem.getMaterialName());
                                        if (dropItem.getItemName() != null) {
                                            hoverMsg.add(getLangVar("drop-item-name") + Util.formatColors(dropItem.getItemName()));
                                            hoverMsg.add("\n");
                                        }
                                        if (dropItem.getAmount() > 1) {
                                            hoverMsg.add(getLangVar("drop-item-amount") + dropItem.getAmount());
                                            hoverMsg.add("\n");
                                        }
                                        if (dropItem.isPotion()) {
                                            hoverMsg.add(getLangVar("potion-type") + dropItem.getPotionEffectTypeName() + "\n");
                                            hoverMsg.add(getLangVar("potion-amplifier") + dropItem.getPotionEffectAmplifier() + "x\n");
                                            hoverMsg.add(getLangVar("potion-duration") + dropItem.getPotionEffectDuration() + "s");
                                            hoverMsg.add("\n");
                                        }
                                        else if (dropItem.isMoney() && VaultHandler.getEconomy() != null) {
                                            hoverMsg.add(getLangVar("drop-item-money-amount") + dropItem.getMoneyAmount());
                                            hoverMsg.add("\n");
                                        }
                                        else if (dropItem.isExp()) {
                                            hoverMsg.add(getLangVar("drop-item-exp-amount") + dropItem.getExpAmount());
                                            hoverMsg.add("\n");
                                        }
                                        if (!dropItem.getEnchantments().isEmpty()) {
                                            hoverMsg.add(getLangVar("drop-item-enchantments"));
                                            hoverMsg.add("\n");
                                            for (String enchantment : dropItem.getEnchantments()) {
                                                hoverMsg.add(enchantment);
                                                hoverMsg.add("\n");
                                            }
                                        }
                                        if (!hoverMsg.isEmpty() && hoverMsg.get(hoverMsg.size() - 1).equals("\n"))
                                            hoverMsg.remove(hoverMsg.size() - 1);
                                        for (String message : hoverMsg) componentBuilder.append(message);
                                        dropHover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, componentBuilder.create()));
                                        player.spigot().sendMessage(dropHover);
                                    }
                                    catch (NoSuchMethodError error) {
                                        sendMessage(sender, getLangVar("drop-item-material") + dropItem.getMaterialName());
                                        if (dropItem.getItemName() != null)
                                            sendMessage(sender, getLangVar("drop-item-name") + Util.formatColors(dropItem.getItemName()));
                                        if (dropItem.getAmount() > 1)
                                            sendMessage(sender, getLangVar("drop-item-amount") + dropItem.getAmount());
                                        if (dropItem.isPotion()) {
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
                                sendMessage(sender, getPrefixLangVar("no-drop"));
                                return;
                            }
                        }
                    }
                    else sendMessage(sender, getPrefixLangVar("block-is-not-a-generator"));
                }
                catch (Exception exception) {
                    Util.handleErrors(sender, exception);
                }
            }
        }
    }
}
