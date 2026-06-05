package fr.ipazu.advancedrealm;

import fr.ipazu.advancedrealm.commands.*;
import fr.ipazu.advancedrealm.events.EventManager;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInvsPlugin;

import java.lang.reflect.Field;
import fr.ipazu.advancedrealm.realm.Realm;
import fr.ipazu.advancedrealm.utils.ARExpansion;
import fr.ipazu.advancedrealm.utils.ConfigFiles;
import fr.ipazu.advancedrealm.utils.Metrics;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Callable;

public class Main extends JavaPlugin {
    private static Main instance;
    public Economy economy = null;
    public static Metrics metrics;
    private InventoryManager invManager;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        if (!checkDependencies()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        invManager = new InventoryManager(this);
        invManager.init();
        try {
            Field managerField = SmartInvsPlugin.class.getDeclaredField("invManager");
            managerField.setAccessible(true);
            managerField.set(null, invManager);
        } catch (Exception e) {
            getLogger().severe("Failed to initialize SmartInvs manager");
        }

        logOptionalPlugins();
        new ConfigFiles().init();
        new EventManager(this);
        getCommand("unclaim").setExecutor(new Unclaim());
        getCommand("claim").setExecutor(new Claim());
        getCommand("realm").setExecutor(new RealmCommand());
        getCommand("home").setExecutor(new Home());
        getCommand("visit").setExecutor(new Visit());
        getCommand("configrealm").setExecutor(new ConfigRealm());
        pushMetrics();
    }

    @Override
    public void onDisable() {
    }

    private boolean checkDependencies() {
        boolean allFound = true;
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            getLogger().severe("WorldEdit is required but not installed. Download WorldEdit from https://dev.bukkit.org/projects/worldedit");
            allFound = false;
        }
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Vault is required but not installed. Download Vault from https://www.spigotmc.org/resources/vault.34315/");
            allFound = false;
        }
        return allFound;
    }

    private void logOptionalPlugins() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getConsoleSender().sendMessage("§e[AdvancedRealm] PlaceholderAPI found - placeholders enabled");
        } else {
            Bukkit.getConsoleSender().sendMessage("§e[AdvancedRealm] PlaceholderAPI not found - placeholders disabled");
        }
        if (Bukkit.getPluginManager().getPlugin("Vault") != null && setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage("§e[AdvancedRealm] Economy plugin found (" + economy.getClass().getSimpleName() + ") - economy features enabled");
        } else if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            Bukkit.getConsoleSender().sendMessage("§e[AdvancedRealm] Vault not found - economy features disabled");
        } else {
            Bukkit.getConsoleSender().sendMessage("§e[AdvancedRealm] No economy plugin found - economy features disabled");
        }
    }

    public boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return (economy != null);
    }

    public static Metrics getMetrics() {
        return metrics;
    }

    private void pushMetrics() {
        metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.SingleLineChart("realms_created", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return Realm.allrealm.size();
            }
        }));
        getLogger().info("Metrics successfully pushed (" + Realm.allrealm.size() + " realms)");
    }
}
