package fr.ipazu.advancedrealm.realm;

import fr.ipazu.advancedrealm.Main;
import fr.ipazu.advancedrealm.realm.themes.Theme;
import fr.ipazu.advancedrealm.realm.themes.ThemeType;
import fr.ipazu.advancedrealm.utils.ConfigFiles;
import fr.ipazu.advancedrealm.utils.CuboidUtils;
import fr.ipazu.advancedrealm.utils.SchematicUtils;
import fr.ipazu.advancedrealm.utils.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Realm {
    public static ArrayList<Realm> allrealm = new ArrayList<>();
    private RealmLevel level;
    private boolean privacy;
    private CuboidUtils cuboid;
    private String perk;
    private Theme theme;
    private int vote;
    private ArrayList<RealmPlayer> realmmembers = new ArrayList<>();
    private ArrayList<RealmPlayer> banned = new ArrayList<>();
    private RealmPlayer owner;

    public Realm(RealmPlayer rp, ThemeType theme, Location location, int level, int vote) {
        owner = rp;
        setLevel(level);
        privacy = false;
        this.theme = new Theme(theme, location);
        this.vote = vote;
        setCuboid();
        allrealm.add(this);
        addPlayer(rp);
        promote(rp, RealmRank.OWNER);
        rp.setOwned(this);
    }
    public void addPlayer(RealmPlayer p) {
        if (p == null) return;
        if (!(realmmembers.size() >= level.getMaxplayer())) {
            realmmembers.add(p);
            p.addRealm(this);
            new RealmConfig().addPlayer(p, this);
        }
    }

    public void upgrade(int i) {
        setLevel(i);
        setCuboid();
        new RealmConfig().setLevel(this);
        sendBorderToAll();
    }

    public void spawnTheme() {
        theme.spawnTheme();
    }

    public void teleportToSpawn(Player player) {
        player.setFallDistance(0);
        player.teleport(theme.getSpawn().clone().add(0.5, 1, 0.5));
        new BukkitRunnable() {
            @Override
            public void run() {
                sendWorldBorderPacket(player);
            }
        }.runTaskLater(Main.getInstance(), 2);
    }

    public void sendBorderToAll() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (cuboid.containsLocation(player.getLocation())) {
                sendWorldBorderPacket(player);
            }
        }
    }

    public Location getCenter() {
        return theme.getSpawn().clone().add(0, 0, 0);
    }

    public void sendWorldBorderPacket(Player player) {
        WorldBorder.sendBorder(getCenter(), level.getBordersize(), player);
    }

    public Theme getTheme() {
        return theme;
    }

    public void kickPlayer(RealmPlayer player) {
        if (realmmembers.contains(player)) {
            realmmembers.remove(player);
            if (Bukkit.getPlayer(player.getUniqueId()) != null) {
                Player kicked = Bukkit.getPlayer(player.getUniqueId());
                if (cuboid.containsLocation(kicked.getLocation())) {
                    new ConfigFiles().sendToSpawn(kicked);
                }
            }

            player.remove(this);
            demote(player);
            new RealmConfig().removePlayer(player, this);
        }
    }

    public void setCuboid() {
        Location center = getCenter();
        Location loc1 = center.clone().add(-(level.getBordersize() / 2), 300, -((level.getBordersize() / 2) + 1));
        Location loc2 = center.clone().add(level.getBordersize() / 2, -300, (level.getBordersize() / 2) + 1);
        cuboid = new CuboidUtils(loc1, loc2);
    }

    public void banPlayer(RealmPlayer player) {
        if (realmmembers.contains(player)) {
            kickPlayer(player);
        }
        if (!banned.contains(player)) {
            banned.add(player);
            new RealmConfig().banPlayer(player, this);
        }
    }

    public void unbanPlayer(RealmPlayer player) {
        if (banned.contains(player)) {
            banned.remove(player);
            new RealmConfig().unbanPlayer(player, this);
        }

    }

    public void fillChest() {

        for (Block block : cuboid.getBlocks()) {
            if (block.getLocation().getChunk().isLoaded()) {
                block.getLocation().getChunk().load();
            }
            Block fromloc = block.getLocation().getBlock();
            if (fromloc.getType() == Material.CHEST) {
                Chest chest = (Chest) fromloc.getState();
                if (ConfigFiles.getRealmchest() == null) {
                    chest.getBlockInventory().setItem(2, new ItemStack(Material.CARROT));
                    chest.getBlockInventory().setItem(3, new ItemStack(Material.SUGAR_CANE));
                    chest.getBlockInventory().setItem(5, new ItemStack(Material.MELON));
                    chest.getBlockInventory().setItem(6, new ItemStack(Material.ICE));
                    chest.getBlockInventory().setItem(13, new ItemStack(Material.TORCH, 8));
                    chest.getBlockInventory().setItem(20, new ItemStack(Material.POTATO));
                    chest.getBlockInventory().setItem(21, new ItemStack(Material.CACTUS));
                    chest.getBlockInventory().setItem(23, new ItemStack(Material.PUMPKIN));
                    chest.getBlockInventory().setItem(24, new ItemStack(Material.LAVA_BUCKET));
                } else {
                    chest.getBlockInventory().setContents(ConfigFiles.getRealmchest().getContents());
                }

            }
        }

    }

    public void promote(RealmPlayer player, RealmRank rank) {
        if (realmmembers.contains(player)) {
            demote(player);
        }
        player.rankbyrealm.put(this, rank);
        new RealmConfig().promotePlayer(player, this);

    }

    public void pasteIsland() {
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            Main.getInstance().getLogger().warning("WorldEdit not found. Install WorldEdit to enable schematic pasting.");
            return;
        }
        try {
            File file = new File(Main.getInstance().getDataFolder(), "island.schematic");
            new SchematicUtils(theme.getSpawn(),file).paste();
            } catch (Exception e) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Failed to load schematic", e);
        }

    }

    public void delete() {
        allrealm.remove(this);
        owner.setOwned(null);
        List<RealmPlayer> toRemove = new ArrayList<>();
        toRemove.addAll(realmmembers);
        for (RealmPlayer rp : toRemove)
            kickPlayer(rp);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (cuboid.containsLocation(player.getLocation())) {
                new ConfigFiles().sendToSpawn(player);
            }
        }
        new RealmConfig().delete(this);
        new RealmConfig().removeVotes(this);
            for (Block block : cuboid.getBlocks()) {
                block.setType(Material.AIR);
            }
            for (Entity entity : theme.getSpawn().getWorld().getEntities()) {
                if (!(entity instanceof Player) && cuboid.containsLocation(entity.getLocation())) {
                    entity.remove();
                }
            }
    }

    public void demote(RealmPlayer player) {
        player.rankbyrealm.remove(this);
    }

    public ArrayList<RealmPlayer> getBanned() {
        return banned;
    }

    public ArrayList<RealmPlayer> getRealmMembers() {
        return realmmembers;
    }

    public RealmPlayer getOwner() {
        return owner;
    }

    public boolean getPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean b) {
        privacy = b;
        new RealmConfig().setPrivacy(this);
        if (b) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                RealmPlayer realmPlayer = RealmPlayer.getPlayer(p.getUniqueId().toString());
                if (this.cuboid.containsLocation(p.getLocation()) && !this.getRealmMembers().contains(realmPlayer)) {
                    p.teleport(ConfigFiles.getSpawn());
                }
            }
        }
    }

    public String getPerk() {
        return perk;
    }

    public void setPerk(String s) {
        perk = s;
        new RealmConfig().setPerk(this);
    }

    public RealmLevel getLevel() {
        return level;
    }

    public void setLevel(int i) {
        level = RealmLevel.getLevel(i);
        if (level == null) {
            level = RealmLevel.getLevel(1);
        }
    }

    public void addVote() {
        vote++;
        new RealmConfig().updateVote(this);
    }

    public static Realm getRealmFromLocation(Location location) {
        for (Realm r : allrealm) {
            if (r.cuboid.containsLocation(location)) {
                return r;
            }
        }
        return null;
    }

    public String getPrivacyString() {
        if (this.getPrivacy())
            return "Private";
        else
            return "Public";
    }

    public void setOwner(RealmPlayer owner) {
        this.owner = owner;
    }

    public CuboidUtils getCuboid() {
        return cuboid;
    }

    public int getVote() {
        return vote;
    }

    private void useless() {
        ArrayList<String> strs = new ArrayList<>();
        strs.forEach(s -> Main.getInstance().getLogger().info(s));
    }
}

