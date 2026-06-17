package fr.ipazu.advancedrealm.commands;

import fr.ipazu.advancedrealm.utils.ChestConfigListener;
import fr.ipazu.advancedrealm.utils.ConfigFiles;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ConfigRealm implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("The command is only executable by a player !");
            return false;
        }
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("configrealm")) {
            if(!player.hasPermission("realm.config")){
                player.sendMessage("§cYou don't have the permission to do that !");
                return false;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("chest")) {
                Inventory inv = Bukkit.createInventory(null, 27, "§7Set realm starter chest");
                if (ConfigFiles.getRealmchest() != null) {
                    inv.setContents(ConfigFiles.getRealmchest().getContents());
                } else {
                    inv.setItem(2, new ItemStack(Material.CARROT));
                    inv.setItem(3, new ItemStack(Material.SUGAR_CANE));
                    inv.setItem(5, new ItemStack(Material.MELON));
                    inv.setItem(6, new ItemStack(Material.ICE));
                    inv.setItem(13, new ItemStack(Material.TORCH, 8));
                    inv.setItem(20, new ItemStack(Material.POTATO));
                    inv.setItem(21, new ItemStack(Material.CACTUS));
                    inv.setItem(23, new ItemStack(Material.PUMPKIN));
                    inv.setItem(24, new ItemStack(Material.LAVA_BUCKET));
                }
                ChestConfigListener.setEditing(player, inv);
                player.openInventory(inv);
                player.sendMessage("§eSet the items and close the inventory to save.");
            }
        }
    return true;
    }
}
