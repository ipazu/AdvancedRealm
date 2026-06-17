package fr.ipazu.advancedrealm.gui;
import fr.ipazu.advancedrealm.Main;
import fr.ipazu.advancedrealm.realm.Realm;
import fr.ipazu.advancedrealm.utils.ItemsUtils;
import fr.ipazu.advancedrealm.utils.TitleUtils;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

public class RemoveProvider implements InventoryProvider {
    private Player player;
    private Realm realm;
    private ClickableItem yes,no,basic;

    public RemoveProvider(Player player, Realm realm) {
        this.player = player;
        this.realm = realm;
        setUpItems();
    }

    private void setUpItems() {
        yes = ClickableItem.of(new ItemsUtils(Material.RED_TERRACOTTA, "§cUnclaim Realm",(byte) 0, Arrays.asList("§7Unclaiming a realm will ", "§7remove your Realm and any ","§7progress on that Realm.")).toItemStack(), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_LAND, 1, 1);
            e.getWhoClicked().closeInventory();
            //new RealmUnclaimEvent(realm, RealmPlayer.getPlayer(player.getUniqueId().toString()),player);
            realm.delete();
            TitleUtils.titlePacket(player,20,30,20,"§bRealm unclaimed","§aClaim a new one with §6/claim");
        });

        no = ClickableItem.of(new ItemsUtils(Material.GREEN_TERRACOTTA, "§aKeep Realm",(byte) 0, Collections.singletonList("§7Cancel unclaim request.")).toItemStack(), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
            e.getWhoClicked().closeInventory();
        });

        basic = ClickableItem.of(new ItemStack(Material.GRAY_STAINED_GLASS_PANE,1,(byte) 15), e -> e.setCancelled(true));
    }


    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        inventoryContents.fill(basic);
        inventoryContents.set(1,2,yes);
        inventoryContents.set(1,6,no);
    }



    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
