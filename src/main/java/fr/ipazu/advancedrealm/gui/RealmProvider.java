package fr.ipazu.advancedrealm.gui;

import fr.ipazu.advancedrealm.Main;
import fr.ipazu.advancedrealm.realm.Realm;
import fr.ipazu.advancedrealm.realm.RealmLevel;
import fr.ipazu.advancedrealm.realm.RealmPlayer;
import fr.ipazu.advancedrealm.realm.RealmRank;
import fr.ipazu.advancedrealm.utils.Config;
import fr.ipazu.advancedrealm.utils.ItemsUtils;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class RealmProvider implements InventoryProvider {
    private Player player;
    private Realm realm;
    private ClickableItem banned, teleport, theme, upgrade, members, privacy, basic, back;
    private boolean from;
    private YamlConfiguration config;

    public RealmProvider(Player player, Realm realms, boolean from) {
        this.player = player;
        this.realm = realms;
        this.from = from;
        this.config = Config.ASPECT.getConfig();
        setUpItems();
    }

    public void setUpItems() {
        teleport = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.realmgui.home.item")), Config.getStringWithReplacementRealm(config.getString("gui.realmgui.home.name"),realm), (byte) config.getInt("gui.realmgui.home.data"),Config.getListWithReplacementRealm(config.getStringList("gui.realmgui.home.lore"),realm)).toItemStack(), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
            realm.teleportToSpawn(player);
            player.sendMessage(Config.getStringWithReplacementRealm(config.getString("gui.realmgui.home.clickmessage"),realm));
            e.getWhoClicked().closeInventory();
        });
        banned = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.realmgui.banned.item")), Config.getStringWithReplacementRealm(config.getString("gui.realmgui.banned.name"),realm), (byte) config.getInt("gui.realmgui.banned.data"), Config.getListWithReplacementRealm(config.getStringList("gui.realmgui.banned.lore"),realm)).toItemStack(), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
            e.getWhoClicked().closeInventory();
            new WholeGUI().openBanned(player, realm);
        });

        privacy = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.realmgui.privacy.item")), Config.getStringWithReplacementRealm(config.getString("gui.realmgui.privacy.name"),realm), (byte) config.getInt("gui.realmgui.privacy.data") ,Config.getListWithReplacementRealm(config.getStringList("gui.realmgui.privacy.lore"),realm)).toItemStack(), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_LEVER_CLICK, 1, 1);
            if (RealmPlayer.getPlayer(player.getUniqueId().toString()).getRankByRealm(realm) == RealmRank.MANAGER || RealmPlayer.getPlayer(player.getUniqueId().toString()).getRankByRealm(realm) == RealmRank.OWNER) {
                realm.setPrivacy(!realm.getPrivacy());
                player.sendMessage(Config.getStringWithReplacementRealm(config.getString("gui.realmgui.privacy.clickmessage"),realm));
                new WholeGUI().openRealmGui(player, realm, from);
            } else
                player.sendMessage("§cOnly the manager and the owner of the Realm can do this !");
        });

        members = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.realmgui.members.item")), Config.getStringWithReplacementRealm(config.getString("gui.realmgui.members.name"),realm), (byte) config.getInt("gui.realmgui.members.data"),Config.getListWithReplacementRealm(config.getStringList("gui.realmgui.members.lore"),realm)).toItemStack(), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
            e.getWhoClicked().closeInventory();
            new WholeGUI().openMembersGui(player, realm);
        });

        theme = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.realmgui.theme.item")), Config.getStringWithReplacementRealm(config.getString("gui.realmgui.theme.name"),realm), (byte) config.getInt("gui.realmgui.theme.data"),Config.getListWithReplacementRealm(config.getStringList("gui.realmgui.theme.lore"),realm)).toItemStack(), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
            e.getWhoClicked().closeInventory();
            new WholeGUI().openThemeGui(player, realm);
        });
        if (realm.getLevel().getNumber() >= 20) {
            upgrade = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.realmgui.upgrade.item")), Config.getStringWithReplacementRealm(config.getString("gui.realmgui.upgrade.name"),realm), (byte) config.getInt("gui.realmgui.upgrade.data"), Config.getListWithReplacementRealm(config.getStringList("gui.realmgui.upgrade.maxlevellore"),realm)).toItemStack(), e -> {
                e.setCancelled(true);
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1, 1);
                player.sendMessage(Config.pushColor(config.getString("messages.upgrade.maxlevel")));
                player.closeInventory();
            });

    }
        else {
            RealmLevel nextlevel = RealmLevel.getLevel(realm.getLevel().getNumber() + 1);
            if (!Main.getInstance().setupEconomy()) {
                upgrade = ClickableItem.of(new ItemsUtils(Material.BARRIER, "§cUpgrade unavailable", (byte) 0, Arrays.asList("§7This feature is currently disabled.")).toItemStack(), e -> {
                    e.setCancelled(true);
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1, 1);
                    player.sendMessage(Config.pushColor(config.getString("messages.upgrade.unavailable")));
                });
            } else {
                upgrade = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.realmgui.upgrade.item")), Config.getStringWithReplacementRealm(config.getString("gui.realmgui.upgrade.name"),realm), (byte) config.getInt("gui.realmgui.upgrade.data"), Config.getListWithReplacementRealm(config.getStringList("gui.realmgui.upgrade.lore"),realm)).toItemStack(), e -> {
                    e.setCancelled(true);
                    if (RealmPlayer.getPlayer(player.getUniqueId().toString()).getRankByRealm(realm) == RealmRank.MEMBER || RealmPlayer.getPlayer(player.getUniqueId().toString()).getRankByRealm(realm) == RealmRank.GUARD) {
                        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1, 1);
                        player.sendMessage(Config.pushColor(config.getString("messages.upgrade.noperm")));
                        return;
                    }
                    if (Main.getInstance().economy.getBalance(player) < nextlevel.getPrice()) {
                        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1, 1);
                        player.sendMessage(Config.pushColor(config.getString("messages.upgrade.notenoughmoney")));
                        String balanceinfo = Config.pushColor(config.getString("messages.upgrade.balanceinfo"));
                        balanceinfo = balanceinfo.replace("%balance%", String.valueOf(Main.getInstance().economy.getBalance(player)));
                        balanceinfo = balanceinfo.replace("%cost%", String.valueOf(nextlevel.getPrice()));
                        player.sendMessage(balanceinfo);
                        return;
                    }
                    player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
                    player.closeInventory();
                    new WholeGUI().openUpgradeConfirmGui(player, realm);
                });
            }
            back = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.back.item")), Config.getStringWithReplacementRealm(config.getString("gui.back.name"),realm), (byte) config.getInt("gui.back.data"), Config.getListWithReplacementRealm(config.getStringList("gui.back.lore"),realm)).toItemStack(), e -> {
                e.setCancelled(true);
                player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
                player.closeInventory();
                new WholeGUI().openAllRealmGUI(player);
            });
            basic = ClickableItem.of(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1, (byte) 15), e -> e.setCancelled(true));
        }
    }

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        inventoryContents.fill(basic);
        inventoryContents.set(Config.getRowFromInt(config.getInt("gui.realmgui.home.slot")), Config.getCollumFromInt(config.getInt("gui.realmgui.home.slot")), teleport);
        inventoryContents.set(Config.getRowFromInt(config.getInt("gui.realmgui.privacy.slot")), Config.getCollumFromInt(config.getInt("gui.realmgui.privacy.slot")), privacy);
        inventoryContents.set(Config.getRowFromInt(config.getInt("gui.realmgui.upgrade.slot")), Config.getCollumFromInt(config.getInt("gui.realmgui.upgrade.slot")), upgrade);
        inventoryContents.set(Config.getRowFromInt(config.getInt("gui.realmgui.theme.slot")), Config.getCollumFromInt(config.getInt("gui.realmgui.theme.slot")), theme);
        inventoryContents.set(Config.getRowFromInt(config.getInt("gui.realmgui.members.slot")), Config.getCollumFromInt(config.getInt("gui.realmgui.members.slot")), members);
        inventoryContents.set(Config.getRowFromInt(config.getInt("gui.realmgui.banned.slot")), Config.getCollumFromInt(config.getInt("gui.realmgui.banned.slot")), banned);
        if (from) {
            inventoryContents.set(Config.getRowFromInt(config.getInt("gui.back.slot")), Config.getCollumFromInt(config.getInt("gui.back.slot")), back);
        }
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }

}
