package fr.ipazu.advancedrealm.events;

import fr.ipazu.advancedrealm.Main;
import fr.ipazu.advancedrealm.realm.Realm;
import fr.ipazu.advancedrealm.realm.RealmPlayer;
import fr.ipazu.advancedrealm.realm.RealmRank;
import fr.ipazu.advancedrealm.utils.Config;
import fr.ipazu.advancedrealm.utils.ConfigFiles;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.ArrayList;
import java.util.Iterator;

public class InterractEvent implements Listener {
    YamlConfiguration config = Config.ASPECT.getConfig();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        RealmPlayer realmPlayer = RealmPlayer.getPlayer(player.getUniqueId().toString());
        if (Realm.getRealmFromLocation(event.getBlock().getLocation()) != null) {
            Realm realm = Realm.getRealmFromLocation(event.getBlock().getLocation());
            //new RealmBreakEvent(realm, realmPlayer, player, event.getBlock());
            if (player.hasPermission("realm.bypass")) {
                return;
            }
            if (realm.getTheme().getCuboid().containsLocation(event.getBlock().getLocation())) {
                if (!config.getString("messages.interact.nobreak").isEmpty() && config.getString("messages.interact.nobreak") != null)
                    player.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(config.getString("messages.interact.nobreak").replace("&","§")));
                event.setCancelled(true);
            }
            else if (!realm.getRealmMembers().contains(realmPlayer)) {
                if (!config.getString("messages.interact.nobreak").isEmpty() && config.getString("messages.interact.nobreak") != null)
                    player.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(config.getString("messages.interact.nobreak").replace("&","§")));
                event.setCancelled(true);
            }


        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBuild(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        RealmPlayer realmPlayer = RealmPlayer.getPlayer(player.getUniqueId().toString());
        if (Realm.getRealmFromLocation(event.getBlock().getLocation()) != null) {
            Realm realm = Realm.getRealmFromLocation(event.getBlock().getLocation());
//            new RealmBuildEvent(realm, realmPlayer, player, event.getBlock());
            if (player.hasPermission("realm.bypass")) {
                return;
            }
            if (!realm.getRealmMembers().contains(realmPlayer)) {
                if (!config.getString("messages.interact.nobuild").isEmpty() && config.getString("messages.interact.nobuild") != null)
                    player.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(config.getString("messages.interact.nobuild").replace("&","§")));
                event.setCancelled(true);
            }
            else if (checkBlockInRealm(event.getBlockPlaced().getLocation(), realm)) {
                if (!config.getString("messages.interact.nobuild").isEmpty() && config.getString("messages.interact.nobuild") != null)
                    player.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(config.getString("messages.interact.nobuild").replace("&","§")));
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        if (Realm.getRealmFromLocation(event.getFrom()) != null) {
            Realm realm = Realm.getRealmFromLocation(event.getFrom());
            if (realm.getTheme().getCuboid().containsLocation(event.getFrom())) {
                event.setCancelled(true);
                event.setTo(ConfigFiles.getSpawn());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChest(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        RealmPlayer realmPlayer = RealmPlayer.getPlayer(player.getUniqueId().toString());
        if (player.hasPermission("realm.bypass")) {
            return;
        }
        if (event.getAction() == Action.PHYSICAL) {
            if (Realm.getRealmFromLocation(event.getClickedBlock().getLocation()) != null) {
                Realm realm = Realm.getRealmFromLocation(event.getClickedBlock().getLocation());
                if (!realm.getRealmMembers().contains(realmPlayer)) {
                    if (!config.getString("messages.interact.nointeract").isEmpty() && config.getString("messages.interact.nointeract") != null)
                        player.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(config.getString("messages.interact.nointeract").replace("&","§")));
                    event.setCancelled(true);
                }
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock() != null && Realm.getRealmFromLocation(event.getClickedBlock().getLocation()) != null) {
                Realm realm = Realm.getRealmFromLocation(event.getClickedBlock().getLocation());
                if (!realm.getRealmMembers().contains(realmPlayer) && event.getClickedBlock().getType() != Material.OAK_SIGN && event.getClickedBlock().getType() != Material.OAK_WALL_SIGN) {
                    event.setCancelled(true);
                }
                if (realm.getRealmMembers().contains(realmPlayer) && event.getClickedBlock().getType() == Material.CHEST && realmPlayer.getRankByRealm(realm) == RealmRank.MEMBER) {
                    event.setCancelled(true);
                    if (!config.getString("messages.interact.nochest").isEmpty()&& config.getString("messages.interact.nochest") != null)
                        player.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(config.getString("messages.interact.nochest").replace("&","§")));
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<Block> iter = event.blockList().iterator();
        while (iter.hasNext()) {
            Block b = iter.next();
            if (Realm.getRealmFromLocation(b.getLocation()) != null) {
                Realm realm = Realm.getRealmFromLocation(b.getLocation());
                if (realm.getTheme().getCuboid().containsLocation(b.getLocation())) {
                    iter.remove();
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (Realm.getRealmFromLocation(player.getLocation()) != null) {
            Realm realm = Realm.getRealmFromLocation(player.getLocation());
            //new PlayerMoveInRealmEvent(realm,RealmPlayer.getPlayer(player.getUniqueId().toString()),player);
            if (player.getLocation().getBlockY() <= -1) {
                realm.teleportToSpawn(player);
            }
            if (player.getLocation().getBlock().getType() == Material.NETHER_PORTAL) {
                new ConfigFiles().sendToSpawn(player);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow && (((Arrow) event.getDamager()).getShooter() instanceof Player)) {
            Player shooter = (Player) (((Arrow) event.getDamager()).getShooter());
            if (shooter.hasPermission("realm.bypass")) {
                return;
            }
            RealmPlayer realmPlayer = RealmPlayer.getPlayer(shooter.getUniqueId().toString());
            if (Realm.getRealmFromLocation(event.getDamager().getLocation()) != null) {
                Realm realm = Realm.getRealmFromLocation(event.getDamager().getLocation());
                if (!realm.getRealmMembers().contains(realmPlayer)) {
                    shooter.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cYou cannot do that!"));
                    event.setCancelled(true);
                }

            }
        }
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            if (damager.hasPermission("realm.bypass")) {
                return;
            }
            RealmPlayer realmPlayer = RealmPlayer.getPlayer(damager.getUniqueId().toString());
            if (Realm.getRealmFromLocation(event.getDamager().getLocation()) != null) {
                Realm realm = Realm.getRealmFromLocation(event.getDamager().getLocation());
                if (!realm.getRealmMembers().contains(realmPlayer)) {
                    if (!config.getString("messages.interact.notinrealm").isEmpty()&& config.getString("messages.interact.notinrealm") != null)
                        damager.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(config.getString("messages.interact.notinrealm").replace("&","§")));
                    event.setCancelled(true);
                }

            }
        } else {
            if (Realm.getRealmFromLocation(event.getEntity().getLocation()) != null && event.getEntity() instanceof Player) {
                Realm realm = Realm.getRealmFromLocation(event.getEntity().getLocation());
                if (realm.getTheme().getCuboid().containsLocation(event.getEntity().getLocation())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractAtEntityEvent event) {
        RealmPlayer realmPlayer = RealmPlayer.getPlayer(event.getPlayer().getUniqueId().toString());
        if (event.getPlayer().hasPermission("realm.bypass")) {
            return;
        }
        if (Realm.getRealmFromLocation(event.getRightClicked().getLocation()) != null) {
            Realm realm = Realm.getRealmFromLocation(event.getRightClicked().getLocation());
            if (!realm.getRealmMembers().contains(realmPlayer)) {
                if (!config.getString("messages.inteact.nointeract").isEmpty()&& config.getString("messages.interact.nointeract") != null)
                    event.getPlayer().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(config.getString("messages.inteact.nointeract").replace("&","§")));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.hasPermission("realm.bypass")) {
                return;
            }
            if (Realm.getRealmFromLocation(player.getLocation()) != null) {
                Realm realm = Realm.getRealmFromLocation(player.getLocation());
                if (!realm.getRealmMembers().contains(RealmPlayer.getPlayer(player.getUniqueId().toString()))) {
                    event.setCancelled(true);
                }
                if (realm.getTheme().getCuboid().containsLocation(player.getLocation())) {
                    event.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void mobSpawn(CreatureSpawnEvent event) {
        if (Realm.getRealmFromLocation(event.getEntity().getLocation()) != null) {
            Realm realm = Realm.getRealmFromLocation(event.getEntity().getLocation());
            if (realm.getTheme().getCuboid().containsLocation(event.getEntity().getLocation())) {
                event.setCancelled(true);
            }

        }
    }

    private boolean checkBlockInRealm(Location l, Realm r) {
        if (r.getTheme().getCuboid().containsLocation(l)) {
            return true;
        }
        return false;
    }

    private void useless() {
        ArrayList<String> strs = new ArrayList<>();
        strs.forEach(s -> Main.getInstance().getLogger().info(s));
    }
}
