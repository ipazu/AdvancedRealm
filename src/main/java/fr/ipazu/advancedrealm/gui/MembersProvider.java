package fr.ipazu.advancedrealm.gui;

import fr.ipazu.advancedrealm.Main;
import fr.ipazu.advancedrealm.realm.Realm;
import fr.ipazu.advancedrealm.realm.RealmPlayer;
import fr.ipazu.advancedrealm.utils.Config;
import fr.ipazu.advancedrealm.utils.ItemsUtils;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class MembersProvider implements InventoryProvider {
    private Player player;
    private Realm realm;
    private YamlConfiguration config;

    public MembersProvider(Player player, Realm realms) {
        this.player = player;
        this.realm = realms;
        this.config = Config.ASPECT.getConfig();
    }


    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        ClickableItem basic = ClickableItem.of(new ItemStack(Material.GRAY_STAINED_GLASS_PANE,1,(byte) 15), e -> e.setCancelled(true));
        inventoryContents.fill(basic);
        for (RealmPlayer rp : realm.getRealmMembers()) {
            setItem(inventoryContents,ClickableItem.of(ItemsUtils.getHead(rp.getName(), Config.getStringWithReplacementPlayer(config.getString("gui.membersgui.player.name"),realm,rp), Config.getListWithReplacementPlayer(config.getStringList("gui.membersgui.player.lore"),realm,rp)),e ->{
                e.setCancelled(true);
                player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
                new WholeGUI().openRankGui(rp,player,realm);
            }));
        }
        inventoryContents.set(4,0,ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.back.item")), Config.getStringWithReplacementRealm(config.getString("gui.back.name"),realm), (byte) config.getInt("gui.back.data"), Config.getListWithReplacementRealm(config.getStringList("gui.back.lore"),realm)).toItemStack(), e ->{
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
            player.closeInventory();
            new WholeGUI().openRealmGui(player,realm,false);
        }));
    }

    private void setItem(InventoryContents inventoryContents,ClickableItem clickableItem) {
        SlotIterator iterator = inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL,0,0);
        while(!iterator.ended()){
            if(iterator.get().isPresent()){
                if(iterator.get().get().getItem().getType() == Material.GRAY_STAINED_GLASS_PANE){
                    inventoryContents.set(iterator.row(),iterator.column(),clickableItem);
                    return;
                }
            }
            iterator.next();
        }

    }


    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }

}
