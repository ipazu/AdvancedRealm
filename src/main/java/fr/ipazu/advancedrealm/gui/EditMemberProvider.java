package fr.ipazu.advancedrealm.gui;

import fr.ipazu.advancedrealm.Main;
import fr.ipazu.advancedrealm.realm.Realm;
import fr.ipazu.advancedrealm.realm.RealmPlayer;
import fr.ipazu.advancedrealm.realm.RealmRank;
import fr.ipazu.advancedrealm.utils.ItemsUtils;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

public class EditMemberProvider implements InventoryProvider {
    private RealmPlayer vised;
    private Realm realm;
    private Player player;
    private RealmPlayer realmplayer;
    private ClickableItem ban,kick,member,guard,manager,basic,retour;

    public EditMemberProvider(Player player, Realm realm,RealmPlayer vised) {
        this.player = player;
        this.realm = realm;
        this.realmplayer = RealmPlayer.getPlayer(player.getUniqueId().toString());
        this.vised =vised;
        setUpItems();
    }

    private void setUpItems() {
        ban = ClickableItem.of(new ItemsUtils(Material.BARRIER, "§4Ban "+vised.getName(), Collections.singletonList("§7Ban " + vised.getName() + " from this realm")).toItemStack(), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_BREAK, 1, 1);
            player.closeInventory();
            if(realmplayer.getRankByRealm(realm) == RealmRank.MEMBER || realmplayer.getRankByRealm(realm) == RealmRank.GUARD){
                player.sendMessage("§cYou don't have the required rank to ban this player of the realm.");
                return;
            }
            if(player.getName().equalsIgnoreCase(vised.getName())){
                player.sendMessage("§cYou cannot ban yourself.");
                return;
            }
            if(vised.getRankByRealm(realm) == RealmRank.OWNER){
                player.sendMessage("§cYou cannot ban the owner of the realm.");
                return;
            }
            realm.banPlayer(vised);
            player.sendMessage("§aYou have successfully banned that player from the Realm.");
            for(RealmPlayer s : realm.getRealmMembers()){
                if(Bukkit.getPlayer(s.getName()) != null)
                    Bukkit.getPlayer(s.getName()).sendMessage("§e§l"+vised.getName()+"§c has been banned from §b§l"+realm.getOwner().getName()+"'s §cRealm.");
            }
        });

        kick = ClickableItem.of(ItemsUtils.getColoredArmor(Material.LEATHER_BOOTS, Color.RED, "§cKick "+vised.getName(), Collections.singletonList("§7Kick " + vised.getName() + " from this realm")), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_BREAK, 1, 1);
            player.closeInventory();
            if(realmplayer.getRankByRealm(realm) == RealmRank.MEMBER || realmplayer.getRankByRealm(realm) == RealmRank.GUARD){
                player.sendMessage("§cYou don't have the required rank to kick this player of the realm.");
                return;
            }
            if(player.getName().equalsIgnoreCase(vised.getName())){
                player.sendMessage("§cYou cannot kick yourself.");
                return;
            }
            if(vised.getRankByRealm(realm) == RealmRank.OWNER){
                player.sendMessage("§cYou cannot kick the owner of the realm.");
                return;
            }
            realm.kickPlayer(vised);
            player.sendMessage("§aYou have successfully kicked that player from the Realm.");
            for(RealmPlayer s : realm.getRealmMembers()){
                if(Bukkit.getPlayer(s.getName()) != null)
                    Bukkit.getPlayer(s.getName()).sendMessage("§e§l"+vised.getName()+"§c has been kicked from §b§l"+realm.getOwner().getName()+"'s §cRealm.");
            }
        });

        member = ClickableItem.of(new ItemsUtils(Material.COBBLESTONE, "§bMember",
                Arrays.asList("§7Member Permissions:","","§7-§e Can't open chests")).toItemStack(), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
            e.getWhoClicked().closeInventory();
            if(realmplayer.getRankByRealm(realm) == RealmRank.MEMBER || realmplayer.getRankByRealm(realm) == RealmRank.GUARD){
                player.sendMessage("§cYou don't have the required rank to edit the permissions of this player.");
                return;
            }
            if(player.getName().equalsIgnoreCase(vised.getName())){
                player.sendMessage("§cYou cannot edit yourself's permissions.");
                return;
            }
            if(vised.getRankByRealm(realm) == RealmRank.OWNER){
                player.sendMessage("§cYou cannot edit the permissions of the owner.");
                return;
            }
            player.sendMessage("§aSuccefully changed the player's rank");
            realm.promote(vised, RealmRank.MEMBER);
            for(RealmPlayer s : realm.getRealmMembers()){
                if(Bukkit.getPlayer(s.getName()) != null)
                    Bukkit.getPlayer(s.getName()).sendMessage("§e§l"+vised.getName()+" §ais now §bMember §aon§b§l "+realm.getOwner().getName()+"'s §aRealm.");
            }
        });
        guard = ClickableItem.of(new ItemsUtils(Material.LAPIS_ORE, "§bGuard",
                Arrays.asList("§7Guard Permissions:","","§7-§e Can build without any restrictions","§7-§e Can break without any restrictions","§7-§e Can interact without any restrictions")).toItemStack(), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
            e.getWhoClicked().closeInventory();
            if(realmplayer.getRankByRealm(realm) == RealmRank.MEMBER || realmplayer.getRankByRealm(realm) == RealmRank.GUARD){
                player.sendMessage("§cYou don't have the required rank to edit the permissions of this player.");
                return;
            }
            if(player.getName().equalsIgnoreCase(vised.getName())){
                player.sendMessage("§cYou cannot edit yourself's permissions.");
                return;
            }
            if(vised.getRankByRealm(realm) == RealmRank.OWNER){
                player.sendMessage("§cYou cannot edit the permissions of the owner.");
                return;
            }
            player.sendMessage("§aSuccefully changed the player's rank");
            realm.promote(vised, RealmRank.GUARD);
            for(RealmPlayer s : realm.getRealmMembers()){
                if(Bukkit.getPlayer(s.getName()) != null)
                    Bukkit.getPlayer(s.getName()).sendMessage("§e§l"+vised.getName()+" §ais now §6Guard §aon§b§l "+realm.getOwner().getName()+"'s §aRealm.");
            }
        });
        manager = ClickableItem.of(new ItemsUtils(Material.GOLD_ORE, "§bManager",
                Arrays.asList("§7Manager Permissions:","","§7-§e Can build without any restrictions","§7-§e Can break without any restrictions","§7-§e Can interact without any restrictions"
                        ,"§7-§e Can kick other members from Realm","§7-§e Can ban other members from Realm","§7-§e Can promote other members from Realm","§7-§e Can invite other members from Realm","§7-§e Can change Realm's privacy")).toItemStack(), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
            e.getWhoClicked().closeInventory();
            if(realmplayer.getRankByRealm(realm) == RealmRank.MEMBER || realmplayer.getRankByRealm(realm) == RealmRank.GUARD){
                player.sendMessage("§cYou don't have the required rank to edit the permissions of this player.");
                return;
            }
            if(player.getName().equalsIgnoreCase(vised.getName())){
                player.sendMessage("§cYou cannot edit yourself's permissions.");
                return;
            }
            if(vised.getRankByRealm(realm) == RealmRank.OWNER){
                player.sendMessage("§cYou cannot edit the permissions of the owner.");
                return;
            }
            player.sendMessage("§aSuccefully changed the player's rank");
            realm.promote(vised, RealmRank.MANAGER);
            for(RealmPlayer s : realm.getRealmMembers()){
                if(Bukkit.getPlayer(s.getName()) != null)
                    Bukkit.getPlayer(s.getName()).sendMessage("§e§l"+vised.getName()+" §ais now §dManager §aon§b§l "+realm.getOwner().getName()+"'s §aRealm.");
            }
        });
        retour = ClickableItem.of(new ItemsUtils(Material.RED_BED,"⬅ §bGo back",Arrays.asList("","§7Click to go back to the","§7Realm options.")).toItemStack(),e ->{
            e.setCancelled(true);
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
            player.closeInventory();
            new WholeGUI().openMembersGui(player,realm);
        });
        basic = ClickableItem.of(new ItemStack(Material.GRAY_STAINED_GLASS_PANE,1,(byte) 15), e -> e.setCancelled(true));
    }


    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        inventoryContents.fill(basic);
        inventoryContents.set(1,2,member);
        inventoryContents.set(1,4,guard);
        inventoryContents.set(1,6,manager);
        inventoryContents.set(3,3,kick);
        inventoryContents.set(3,5,ban);
        inventoryContents.set(4,0,retour);

    }



    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
