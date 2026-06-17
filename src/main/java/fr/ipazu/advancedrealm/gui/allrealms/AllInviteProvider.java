package fr.ipazu.advancedrealm.gui.allrealms;


import fr.ipazu.advancedrealm.Main;
import fr.ipazu.advancedrealm.gui.WholeGUI;
import fr.ipazu.advancedrealm.realm.Realm;
import fr.ipazu.advancedrealm.realm.RealmPlayer;
import fr.ipazu.advancedrealm.realm.RealmRank;
import fr.ipazu.advancedrealm.utils.ItemsUtils;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AllInviteProvider implements InventoryProvider {
    private ArrayList<Realm> avaiblerealms = new ArrayList<>();
    private RealmPlayer realmPlayer;
    private Player player;
    private Player visedplayer;
    private RealmPlayer vised;
    private ArrayList<ClickableItem> items = new ArrayList<>();
    private ClickableItem up, down;

    public AllInviteProvider(Player player, Player vised) {
        this.realmPlayer = RealmPlayer.getPlayer(player.getUniqueId().toString());
        this.vised = RealmPlayer.getPlayer(vised.getUniqueId().toString());
        this.visedplayer = vised;
        this.player = player;
        setAvaibleRealms();
    }

    public void setAvaibleRealms() {
        for (Realm r : realmPlayer.getAllRealm()) {
            if (realmPlayer.getRankByRealm(r) == RealmRank.MANAGER || realmPlayer.getRankByRealm(r) == RealmRank.OWNER) {
                if(realmPlayer.getOwned() == r){
                    avaiblerealms.add(0,r);
                }else
                avaiblerealms.add(r);
            }
        }
        Collections.reverse(avaiblerealms);
    }

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        Pagination pagination = inventoryContents.pagination();
        int i = 1;
        ClickableItem basic = ClickableItem.of(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1, (byte) 15), e -> e.setCancelled(true));
        if (realmPlayer.getAllRealm().size() <= 9)
            inventoryContents.fill(basic);
        if(realmPlayer.getOwned() != null){
            avaiblerealms.removeIf(realm -> realmPlayer.getOwned() == realm);
            avaiblerealms.add(0,realmPlayer.getOwned());
        }

        for (Realm r : avaiblerealms) {
            ClickableItem cl = ClickableItem.of(ItemsUtils.getHead(r.getOwner().getName(), "§bInvite to " + r.getOwner().getName() + "'s Realm.",
                    Arrays.asList("§7# of members §6" + r.getRealmMembers().size() + "§7/§6" + r.getLevel().getMaxplayer(), "§7Privacy: §6" + r.getPrivacyString())), e -> {
                e.setCancelled(true);
                player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
                if(player.getName().equalsIgnoreCase(visedplayer.getName())){
                    player.sendMessage("You cannot invite yourself.");
                    player.closeInventory();
                    return;
                }
                if (r.getRealmMembers().contains(vised)) {
                    player.sendMessage("§cThis player is already in this realm !");
                    player.closeInventory();
                    return;
                }
                if (r.getRealmMembers().size() >= r.getLevel().getMaxplayer()) {
                    player.sendMessage("§cThis realm is full, try to upgrade it!");
                    player.closeInventory();
                    return;
                }
                if(vised.realmwaiting.contains(r)){
                    player.sendMessage("This player is already invited to this Realm.");
                    player.closeInventory();
                    return;
                }
                if(r.getBanned().contains(vised)){
                    player.sendMessage("§cThis player is banned from this realm");
                }
                vised.addWaiting(r);
                visedplayer.sendMessage("§e§l" + player.getName() + " §ainvited you to join §b§l" + r.getOwner().getName() + "'s Realm.");
                ComponentBuilder cb = new ComponentBuilder("");
                cb.append("§6Click Here").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7click").create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/realm accept " + r.getOwner().getName()));
                cb.append("§a to join! You have §d120 seconds §ato accept");
                visedplayer.spigot().sendMessage(cb.create());
                player.sendMessage("§aSuccessfully sent invite to §e§l" + visedplayer.getName()+"§a. It will expire in §b120 secondes");
                player.closeInventory();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        vised.removeWaiting(r);
                    }
                }.runTaskLater(Main.getInstance(), 20 * 120);
            });
            if (realmPlayer.getOwned() == r) {
                cl.getItem().setType(Material.RED_BED);
            }
            if (realmPlayer.getAllRealm().size() <= 9)
                placeIfVoid(cl, inventoryContents);
            else {
                up = ClickableItem.of(new ItemsUtils(Material.ARROW, "§bNext page", Arrays.asList("§7Click to go to ", "§7the page " + getNextPage(pagination))).toItemStack(), e -> {
                    e.setCancelled(true);
                    new WholeGUI().getAllRealmGUI(player).open(player, pagination.next().getPage());
                });
                down = ClickableItem.of(new ItemsUtils(Material.ARROW, "§bPrevious page", Arrays.asList("§7Click to go to ", "§7the page " + getPreviousPage(pagination))).toItemStack(), e -> {
                    e.setCancelled(true);
                    new WholeGUI().getAllRealmGUI(player).open(player, pagination.previous().getPage());
                });
                i++;
                int j = i;
                if (j > 9 && (j - 1) % 9 == 0) {
                    items.add(down);
                } else if (j % 9 == 0) {
                    items.add(up);
                } else
                    items.add(cl);
            }
        }
        if (realmPlayer.getAllRealm().size() > 9) {
            while (items.size() % 9 != 0) {
                items.add(basic);
            }
            pagination.setItems(items.toArray(new ClickableItem[items.size()]));
            pagination.setItemsPerPage(9);
            pagination.addToIterator(inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));
        }

    }

    public void placeIfVoid(ClickableItem clickableItem, InventoryContents inventoryContents) {
        SlotIterator iterator = inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0);
        while (!iterator.ended()) {
            if (iterator.get().isPresent() && iterator.get().get().getItem().getType() == Material.GRAY_STAINED_GLASS_PANE) {
                inventoryContents.set(iterator.row(), iterator.column(), clickableItem);
                return;
            }
            iterator.next();
        }
    }
    private int getPreviousPage(Pagination pagination) {
        int i = pagination.getPage();
        return i;
    }

    private int getNextPage(Pagination pagination) {
        int i = pagination.getPage();
        i++;
        return i;
    }
    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
