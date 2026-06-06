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
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UpgradeConfirmProvider implements InventoryProvider {
    private Player player;
    private Realm realm;
    private ClickableItem confirm, cancel, info, basic;
    private YamlConfiguration config;

    public UpgradeConfirmProvider(Player player, Realm realm) {
        this.player = player;
        this.realm = realm;
        this.config = Config.ASPECT.getConfig();
        setUpItems();
    }

    private void setUpItems() {
        RealmLevel nextlevel = RealmLevel.getLevel(realm.getLevel().getNumber() + 1);

        info = ClickableItem.of(new ItemsUtils(
                Config.getMaterial(config.getString("gui.upgradeconfirm.info.item")),
                Config.getStringWithReplacementRealm(config.getString("gui.upgradeconfirm.info.name"), realm),
                (byte) 0,
                Config.getListWithReplacementRealm(config.getStringList("gui.upgradeconfirm.info.lore"), realm)
        ).toItemStack(), e -> e.setCancelled(true));

        confirm = ClickableItem.of(new ItemsUtils(
                Config.getMaterial(config.getString("gui.upgradeconfirm.confirm.item")),
                Config.getStringWithReplacementRealm(config.getString("gui.upgradeconfirm.confirm.name"), realm),
                (byte) 0,
                Config.getListWithReplacementRealm(config.getStringList("gui.upgradeconfirm.confirm.lore"), realm)
        ).toItemStack(), e -> {
            e.setCancelled(true);
            if (RealmPlayer.getPlayer(player.getUniqueId().toString()).getRankByRealm(realm) == RealmRank.MEMBER ||
                    RealmPlayer.getPlayer(player.getUniqueId().toString()).getRankByRealm(realm) == RealmRank.GUARD) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                player.sendMessage(Config.pushColor(config.getString("messages.upgrade.noperm")));
                return;
            }
            if (Main.getInstance().economy.getBalance(player) < nextlevel.getPrice()) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                player.sendMessage(Config.pushColor(config.getString("messages.upgrade.notenoughmoney")));
                String balanceinfo = Config.pushColor(config.getString("messages.upgrade.balanceinfo"));
                balanceinfo = balanceinfo.replace("%balance%", String.valueOf(Main.getInstance().economy.getBalance(player)));
                balanceinfo = balanceinfo.replace("%cost%", String.valueOf(nextlevel.getPrice()));
                player.sendMessage(balanceinfo);
                return;
            }

            Main.getInstance().economy.withdrawPlayer(player, nextlevel.getPrice());
            realm.upgrade(realm.getLevel().getNumber() + 1);

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

            String broadcast = Config.pushColor(config.getString("messages.upgrade.successbroadcast"));
            broadcast = broadcast.replace("%player%", player.getName());
            broadcast = broadcast.replace("%owner%", realm.getOwner().getName());
            broadcast = broadcast.replace("%level%", String.valueOf(realm.getLevel().getNumber()));
            for (RealmPlayer s : realm.getRealmMembers()) {
                if (Bukkit.getPlayer(s.getName()) != null)
                    Bukkit.getPlayer(s.getName()).sendMessage(broadcast);
            }

            player.closeInventory();
        });

        cancel = ClickableItem.of(new ItemsUtils(
                Config.getMaterial(config.getString("gui.upgradeconfirm.cancel.item")),
                Config.getStringWithReplacementRealm(config.getString("gui.upgradeconfirm.cancel.name"), realm),
                (byte) 0,
                Config.getListWithReplacementRealm(config.getStringList("gui.upgradeconfirm.cancel.lore"), realm)
        ).toItemStack(), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            new WholeGUI().openRealmGui(player, realm, true);
        });

        basic = ClickableItem.of(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1, (byte) 15), e -> e.setCancelled(true));
    }

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        inventoryContents.fill(basic);
        inventoryContents.set(0, 4, info);
        inventoryContents.set(1, 2, confirm);
        inventoryContents.set(1, 6, cancel);
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {
    }
}
