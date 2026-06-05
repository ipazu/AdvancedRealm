package fr.ipazu.advancedrealm.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldBorder {
    public static void sendBorder(Location center, int size, Player player) {
        org.bukkit.WorldBorder border = Bukkit.createWorldBorder();
        border.setCenter(center.getBlockX(), center.getBlockZ());
        border.setSize(size);
        border.setWarningDistance(3);
        player.setWorldBorder(border);
    }
}
