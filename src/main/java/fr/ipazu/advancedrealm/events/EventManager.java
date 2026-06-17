package fr.ipazu.advancedrealm.events;


import fr.ipazu.advancedrealm.utils.ChestConfigListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class EventManager {
    Plugin plugin;

    public EventManager(Plugin plugin) {
        this.plugin = plugin;
        registerEvents();
    }

    public void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new JoinEvent(),this.plugin);
        pm.registerEvents(new InterractEvent(),this.plugin);
        pm.registerEvents(new PerkEvent(),this.plugin);
        pm.registerEvents(new ChestConfigListener(),this.plugin);
    }
}
