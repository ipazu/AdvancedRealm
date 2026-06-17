package fr.ipazu.advancedrealm.gui;

import fr.ipazu.advancedrealm.realm.Realm;
import fr.ipazu.advancedrealm.realm.RealmConfig;
import fr.ipazu.advancedrealm.realm.RealmPlayer;
import fr.ipazu.advancedrealm.realm.themes.ThemeType;
import fr.ipazu.advancedrealm.utils.ItemsUtils;
import fr.ipazu.advancedrealm.utils.TitleUtils;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class PerkProvider implements InventoryProvider{
    private Player player;
    private RealmPlayer realmPlayer;
    private Realm realm;
    private ClickableItem xp,crops,basic,cancel;

    public PerkProvider(Player player,RealmPlayer realmPlayer) {
        this.player = player;
        this.realmPlayer = realmPlayer;
        setUpItems();
    }

    private void setUpItems() {
        xp = ClickableItem.of(new ItemsUtils(Material.EXPERIENCE_BOTTLE, "§bXP multiplicator", Arrays.asList("", "§7Get a 1.5x XP Booster In Your Realm")).toItemStack(), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            new WholeGUI().getPerkGui(player,realmPlayer).close(player);
            createRealm();
            realm.setPerk("xp");
            TitleUtils.titlePacket(player,20,30,20,"§bRealm claimed","§aGo to your Realm with §6/home");
        });

        crops = ClickableItem.of(new ItemsUtils(Material.WHEAT, "§bBetter Crops", Arrays.asList("", "§7Crops grow faster.")).toItemStack(), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            new WholeGUI().getPerkGui(player,realmPlayer).close(player);
            createRealm();
            realm.setPerk("crops");
            TitleUtils.titlePacket(player,20,30,20,"§bRealm claimed","§aGo to your Realm with §6/home");
        });
        cancel = ClickableItem.of(new ItemsUtils(Material.BLACK_DYE, "§cCancel",(byte)1, Arrays.asList("", "§7Cancel the creation of your realm")).toItemStack(), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
            new WholeGUI().getPerkGui(player,realmPlayer).close(player);
        });

        basic = ClickableItem.of(new ItemStack(Material.GRAY_STAINED_GLASS_PANE,1,(byte) 15), e -> e.setCancelled(true));
    }


    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        inventoryContents.fill(basic);
        inventoryContents.set(0,2,xp);
        inventoryContents.set(0,6,crops);
        inventoryContents.set(0,8,cancel);
    }

    private void createRealm(){
        Realm realm = new Realm(realmPlayer, ThemeType.allthemeTypes.get(0),new RealmConfig().getNewLocation(),1,0);
        realm.pasteIsland();
        realm.fillChest();
        new RealmConfig().updateRealm(realmPlayer.getOwned());
        this.realm = realm;
        //new RealmClaimEvent(realm,realmPlayer);
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }

}
