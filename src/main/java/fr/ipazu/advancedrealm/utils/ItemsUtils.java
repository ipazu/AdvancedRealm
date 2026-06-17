package fr.ipazu.advancedrealm.utils;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemsUtils {
    Material material;
    String name;
    List<String> lore;
    private ItemStack item;

    public ItemsUtils(Material material, String name, byte b, List<String> sl) {
        this.item = new ItemStack(material);
        ItemMeta itemMeta = this.item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(sl);
        this.item.setItemMeta(itemMeta);
        this.material = material;
        this.name = name;
    }

    public ItemsUtils(Material material, boolean b) {
        this.item = new ItemStack(material);
        this.material = material;
        if (b) setUnbreakable();
    }

    public ItemsUtils(Material material, String name) {
        this.item = new ItemStack(material);
        ItemMeta itemMeta = this.item.getItemMeta();
        itemMeta.setDisplayName(name);
        this.item.setItemMeta(itemMeta);
        this.material = material;
        this.name = name;
    }

    public ItemsUtils(Material material, String name, List<String> sl) {
        this.item = new ItemStack(material);
        ItemMeta itemMeta = this.item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(sl);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        this.item.setItemMeta(itemMeta);
        this.material = material;
        this.name = name;
    }

    public ItemsUtils(ItemStack items, String name) {
        this.item = items;
        ItemMeta itemMeta = this.item.getItemMeta();
        itemMeta.setDisplayName(name);
        this.item.setItemMeta(itemMeta);
        this.name = name;
    }

    public ItemsUtils(Material material, int amount, String name, boolean b) {
        this.item = new ItemStack(material);
        ItemMeta itemMeta = this.item.getItemMeta();
        itemMeta.setDisplayName(name);
        this.item.setItemMeta(itemMeta);
        this.item.setAmount(amount);
        this.material = material;
        this.name = name;
        if (b) setUnbreakable();
    }

    public ItemStack toItemStack() {
        if (this.lore != null) {
            ItemMeta itemMeta = this.item.getItemMeta();
            itemMeta.setLore(this.lore);
            this.item.setItemMeta(itemMeta);
        }
        return this.item;
    }

    public void setUnbreakable() {
        ItemMeta im = this.item.getItemMeta();
        im.setUnbreakable(true);
        this.item.setItemMeta(im);
    }

    public static ItemStack setMeta(ItemStack i, String name, List<String> s) {
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(name);
        im.setLore(s);
        i.setItemMeta(im);
        return i;
    }

    public static ItemStack setItemFlag(ItemStack i, ItemFlag fl) {
        ItemMeta im = i.getItemMeta();
        im.addItemFlags(fl);
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        i.setItemMeta(im);
        return i;
    }

    public Material getMaterial() {
        return this.material;
    }

    public static int getAmount(Material material, Player player) {
        ItemStack[] inv = player.getInventory().getContents();
        int quantity = 0;
        for (ItemStack itemStack : inv) {
            if (itemStack != null && itemStack.getType() == material) {
                quantity += itemStack.getAmount();
            }
        }
        return quantity;
    }

    public static ItemStack getColoredArmor(Material m, Color c, String name, List<String> s) {
        ItemStack i = new ItemStack(m, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta) i.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(s);
        meta.setColor(c);
        i.setItemMeta(meta);
        return i;
    }

    public static ItemStack getHead(String playername, String name, List<String> lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwnerProfile(Bukkit.createPlayerProfile(playername));
        meta.setDisplayName(name);
        meta.setLore(lore);
        head.setItemMeta(meta);
        return head;
    }
}
