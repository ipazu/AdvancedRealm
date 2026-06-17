package fr.ipazu.advancedrealm.gui;


import fr.ipazu.advancedrealm.Main;
import fr.ipazu.advancedrealm.realm.Realm;
import fr.ipazu.advancedrealm.utils.ItemsUtils;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class TopProvider implements InventoryProvider {
    private Player player;
    private ArrayList<ClickableItem> items = new ArrayList<>();
    private ClickableItem up, down;

    public TopProvider(Player player) {
        this.player = player;
    }

    public ArrayList<Realm> getTopArray() {
        ArrayList<Realm> allrealms = new ArrayList<>();
        allrealms.addAll(Realm.allrealm);
        ArrayList<Realm> toprealm = new ArrayList<>();
        while (allrealms.size() > 0) {
            Realm current = null;
            if (toprealm.size() >= 200) {
                return toprealm;
            }
            for (Realm r : allrealms) {
                if (current == null)
                    current = r;
                else {
                    if (r.getVote() >= current.getVote())
                        current = r;
                }
            }
            toprealm.add(current);
            allrealms.remove(current);
        }
        return toprealm;
    }

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        Pagination pagination = inventoryContents.pagination();
        int i = 0;
        int rank = 1;
        ClickableItem basic = ClickableItem.of(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1, (byte) 15), e -> e.setCancelled(true));
        if (Realm.allrealm.size() <= 9 * 4) {
            inventoryContents.fill(basic);
        }
        for (Realm r : getTopArray()) {
            ClickableItem cl = ClickableItem.of(ItemsUtils.getHead(r.getOwner().getName(), "§e" + r.getOwner().getName(), Arrays.asList("", "§7Rank: §d" + rank, "§7Votes: §e" + r.getVote(), "", "§eClick to visit")), e -> {
                e.setCancelled(true);
                player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
                player.closeInventory();
                player.performCommand("visit " + r.getOwner().getName());
            });
            rank++;
            if (Realm.allrealm.size() <= 9 * 4)
                placeIfVoid(cl, inventoryContents);
            else {
                up = ClickableItem.of(new ItemsUtils(Material.ARROW, "§bNext page", Arrays.asList("§7Click to go to ", "§7the page §f" + getNextPage(pagination))).toItemStack(), e -> {
                    e.setCancelled(true);
                    int d = Math.round(items.size() / 45);
                    if (getNextPage(pagination) <= d) {
                        new WholeGUI().getTopGui(player).open(player, pagination.next().getPage());
                    }
                });
                down = ClickableItem.of(new ItemsUtils(Material.ARROW, "§bPrevious page", Arrays.asList("§7Click to go to ", "§7the page §f" + getPreviousPage(pagination))).toItemStack(), e -> {
                    e.setCancelled(true);
                    new WholeGUI().getTopGui(player).open(player, pagination.previous().getPage());
                });
                int m = i;
                int j = i;
                //first row
                if (m % 45 == 0 && i < 45) {
                    for (int b = 1; b <= 9; b++) {
                        items.add(basic);
                    }
                    items.add(cl);
                    i += 10;
                }//last row
                else if ((j + 9) % 45 == 0 && j > 9) {
                    for (int b = 1; b <= 9; b++) {
                        if (b == 4 && getPreviousPage(pagination) != 0) {
                            items.add(down);
                        } else if (b == 6) {
                            items.add(up);
                        } else {
                            items.add(basic);
                        }
                    }
                    i += 9;
                    //making the first row after the last one
                    for (int b = 1; b <= 9; b++) {
                        items.add(basic);
                    }
                    items.add(cl);
                    i += 10;
                }
                //add head item
                else {
                    items.add(cl);
                    i++;
                }
            }

        }
        if (Realm.allrealm.size() > 9 * 4) {
            while (items.size() % 9 * 5 != 0) {
                items.add(basic);
            }
            for (int b = 1; b <= 9; b++) {
                if (b == 4) {
                    items.add(down);
                } else if (b == 6) {
                    items.add(up);
                } else {
                    items.add(basic);
                }
            }
            pagination.setItems(items.toArray(new ClickableItem[items.size()]));
            pagination.setItemsPerPage(9 * 5);
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

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }

    private int getPreviousPage(Pagination pagination) {
        int i = pagination.getPage();
        return i;
    }

    private int getNextPage(Pagination pagination) {
        int i = pagination.getPage();
        i+=2;
        return i;
    }

}
