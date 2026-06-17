package fr.ipazu.advancedrealm.gui;


import fr.ipazu.advancedrealm.Main;
import fr.ipazu.advancedrealm.realm.Realm;
import fr.ipazu.advancedrealm.realm.themes.ThemeType;
import fr.ipazu.advancedrealm.utils.ItemsUtils;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class ThemeProvider implements InventoryProvider {

        private Player player;
        private Realm realm;
        private ArrayList<ThemeType> avaibletheme = new ArrayList<>();

        public ThemeProvider(Player player, Realm realm) {
            this.player = player;
            this.realm = realm;
            setAvaibleTheme();
        }

        public void setAvaibleTheme(){
            for(ThemeType t : ThemeType.allthemeTypes){
               if(player.hasPermission(t.getPermission()))
                   avaibletheme.add(t);
            }
        }
        @Override
        public void init(Player player, InventoryContents inventoryContents) {
            ClickableItem basic = ClickableItem.of(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1, (byte) 15), e -> e.setCancelled(true));
            inventoryContents.fill(basic);
            for(ThemeType t : avaibletheme){
                setItem(inventoryContents,ClickableItem.of(t.getItem(),e ->{
                    realm.getTheme().setThemeType(t);
                    realm.getTheme().spawnTheme();
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    player.sendMessage("§aSuccessfully changed the type to: §e§l"+t.getName());
                    e.setCancelled(true);
                    player.closeInventory();
                }));

            }
            inventoryContents.set(2, 0, ClickableItem.of(new ItemsUtils(Material.RED_BED, "⬅ §bGo back", Arrays.asList("", "§7Click to go back to the", "§7Realm options.")).toItemStack(), e -> {
                e.setCancelled(true);
                player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
                player.closeInventory();
                new WholeGUI().openRealmGui(player, realm, false);
            }));
        }

        @Override
        public void update(Player player, InventoryContents inventoryContents) {

        }

        private void setItem(InventoryContents inventoryContents, ClickableItem clickableItem) {
            SlotIterator iterator = inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0);
            while (!iterator.ended()) {
                if (iterator.get().isPresent()) {
                    if (iterator.get().get().getItem().getType() == Material.GRAY_STAINED_GLASS_PANE) {
                        inventoryContents.set(iterator.row(), iterator.column(), clickableItem);
                        return;
                    }
                }
                iterator.next();
            }

        }
    }
