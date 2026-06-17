package fr.ipazu.advancedrealm.utils;

import fr.ipazu.advancedrealm.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChestConfigListener implements Listener {
    private static Set<UUID> editing = new HashSet<>();

    public static void setEditing(Player player, Inventory inv) {
        editing.add(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        if (!editing.remove(player.getUniqueId())) return;
        new ConfigFiles().setRealmchest(event.getInventory());
        player.sendMessage("§aRealm chest saved successfully!");
    }
}