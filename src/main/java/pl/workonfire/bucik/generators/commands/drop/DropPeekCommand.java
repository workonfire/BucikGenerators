package pl.workonfire.bucik.generators.commands.drop;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.workonfire.bucik.generators.data.generator.DropItem;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.utils.*;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getLanguageVariable;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;

@SuppressWarnings("ConstantConditions")
public class DropPeekCommand implements CommandExecutor, CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        run(sender, command, label, args);
        return true;
    }

    @Override
    public boolean executableByConsole() {
        return false;
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
    public void run(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
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
                        player.sendMessage(getPrefixedLanguageVariable("items-drop-list"));
                        final Generator generator = new Generator(BlockUtil.getGeneratorFromMaterial(targetBlock.getType()).getId());
                        for (String permission : generator.getGeneratorDropPermissionList()) {
                            if (player.hasPermission(Util.getPermission(permission))) {
                                sender.sendMessage(getLanguageVariable("items-for-permission") + Util.getPermission(permission) + "§f:");
                                for (String dropItemId : generator.getDropItemsIds(permission)) {
                                    final DropItem dropItem = new DropItem(generator, permission, Integer.parseInt(dropItemId));
                                    player.sendMessage("§c§m--------------");
                                    player.sendMessage(getLanguageVariable("drop-item-material") + dropItem.getItemMaterialName());
                                    if (dropItem.getItemName() != null)
                                        player.sendMessage(getLanguageVariable("drop-item-name") + Util.formatColors(dropItem.getItemName()));
                                    if (dropItem.getItemAmount() > 1)
                                        player.sendMessage(getLanguageVariable("drop-item-amount") + dropItem.getItemAmount());
                                    if (dropItem.isAPotion()) {
                                        player.sendMessage(getLanguageVariable("potion-type") + dropItem.getPotionEffectTypeName());
                                        player.sendMessage(getLanguageVariable("potion-amplifier") + dropItem.getPotionEffectAmplifier());
                                        player.sendMessage(getLanguageVariable("potion-duration") + dropItem.getPotionEffectDuration() + "s");
                                    }
                                    if (dropItem.isMoney() && VaultHandler.getEconomy() != null)
                                        player.sendMessage(getLanguageVariable("drop-item-money-amount") + dropItem.getMoneyAmount());
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
                                return;
                            }
                        }
                    }
                    else player.sendMessage(getPrefixedLanguageVariable("block-is-not-a-generator"));
                }
                else player.sendMessage(getPrefixedLanguageVariable("no-permission"));
            }
            else sender.sendMessage(getPrefixedLanguageVariable("cannot-open-from-console"));
        }
        catch (Exception exception) {
            Util.handleErrors(sender, exception);
        }
    }
}
