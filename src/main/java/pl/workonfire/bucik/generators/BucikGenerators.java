/*
 *     Copyright (C) 2020-2021 workonfire
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pl.workonfire.bucik.generators;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.workonfire.bucik.generators.data.GeneratorDurabilities;
import pl.workonfire.bucik.generators.data.Metrics;
import pl.workonfire.bucik.generators.managers.ConfigManager;
import pl.workonfire.bucik.generators.managers.utils.Logger;
import pl.workonfire.bucik.generators.managers.utils.Util;
import pl.workonfire.bucik.generators.managers.VaultHandler;

/**
 * A customizable plugin for setting up block generators.
 * Made with ♥
 *
 * @author  workonfire, aka Buty935
 * @version 1.3.0
 * @since   2020-06-13
 */


public final class BucikGenerators extends JavaPlugin {
    @Getter private static BucikGenerators instance;
    @Getter private static String          pluginVersion;

    @Override
    public void onEnable() {
        instance = this;
        pluginVersion = getInstance().getDescription().getVersion();
        getInstance().saveDefaultConfig();
        ConfigManager.initializeConfig();
        ConfigManager.initializeDb();

        Util.registerEvents();
        Util.registerCommands();
        VaultHandler.setupEconomy();

        Util.systemMessage(Logger.INFO, "&fBucikGenerators &6" + getPluginVersion() + " &fby Buty935. Discord: &9workonfire#8262");
        Util.systemMessage(Logger.DEBUG, "Debug mode enabled. IF YOU ENCOUNTER ANY BUGS, PLEASE REPORT THEM.");
        Util.systemMessage(Logger.DEBUG, "Economy setup: " + VaultHandler.getEconomy());

        int dataSaveInterval = ConfigManager.getConfig().getInt("options.auto-save-interval");
        if (dataSaveInterval != 0)
            Bukkit.getScheduler().scheduleSyncRepeatingTask(getInstance(), ConfigManager::updateDb, 0, dataSaveInterval);
        Util.registerRecipes();

        if (ConfigManager.getConfig().getBoolean("options.metrics")) {
            new Metrics(getInstance(), 7854);
            Util.systemMessage(Logger.INFO,
                    "bStats service has been &2enabled&r! Set &6metrics &rto &cfalse &rin " +
                            "&f&nconfig.yml&r in order to disable metrics.");
        }

        if (Util.isServerLegacy()) Util.systemMessage(Logger.WARN,
                "Although this plugin works on some versions older than 1.13, the support for legacy versions" +
                        " is very limited.\nDon't expect everything to work fine. For example, not all of the item types" +
                        " are going to be recognized. Don't report this kind of bugs.");
    }

    @Override
    @SneakyThrows
    public void onDisable() {
        ConfigManager.updateDb();
        Util.unregisterRecipes();
        GeneratorDurabilities.getInstance().serialize();
    }
}
