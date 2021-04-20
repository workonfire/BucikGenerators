package pl.workonfire.bucik.generators.managers;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

import static org.bukkit.Bukkit.getServer;

/**
 * A bridge between Vault and this plugin.
 */
public final class VaultHandler {
    private static Economy economy = null;

    public static void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return;
        RegisteredServiceProvider<Economy> serviceProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (serviceProvider == null) return;
        economy = serviceProvider.getProvider();
    }

    public static @Nullable Economy getEconomy() {
        return economy;
    }
}
