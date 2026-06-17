package fr.ipazu.advancedrealm.utils;

import org.bukkit.entity.Player;

public class TitleUtils {

    public static void titlePacket(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
}
