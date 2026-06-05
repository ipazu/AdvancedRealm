package fr.ipazu.advancedrealm.realm;


import com.google.common.collect.Iterables;
import fr.ipazu.advancedrealm.Main;
import fr.ipazu.advancedrealm.realm.themes.ThemeType;
import fr.ipazu.advancedrealm.utils.ConfigFiles;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;

public class RealmConfig {
    private File file = new File(Main.getInstance().getDataFolder(), "realm.yml");
    private FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    public RealmConfig() {

    }

    public void updateRealm(Realm realm) {
        config.set("realms." + realm.getOwner().getUniqueId() + ".owner.uuid", realm.getOwner().getUniqueId());
        config.set("realms." + realm.getOwner().getUniqueId() + ".owner.name", realm.getOwner().getName());
        config.set("realms." + realm.getOwner().getUniqueId() + ".privacy", realm.getPrivacy());
        config.set("realms." + realm.getOwner().getUniqueId() + ".level", realm.getLevel().getNumber());
        updateVote(realm);
        for (RealmPlayer bplayer : realm.getBanned()) {
            config.set("realms." + realm.getOwner().getUniqueId() + ".banned.", bplayer.getUniqueId());
        }
        config.set("realms." + realm.getOwner().getUniqueId() + ".theme.id", realm.getTheme().getThemeType().getName());
        config.set("realms." + realm.getOwner().getUniqueId() + ".theme.spawn.x", realm.getTheme().getSpawn().getBlockX());
        config.set("realms." + realm.getOwner().getUniqueId() + ".theme.spawn.y", realm.getTheme().getSpawn().getBlockY());
        config.set("realms." + realm.getOwner().getUniqueId() + ".theme.spawn.z", realm.getTheme().getSpawn().getBlockZ());
        config.set("realms." + realm.getOwner().getUniqueId() + ".theme.spawn.yaw", realm.getTheme().getSpawn().getYaw());
        config.set("realms." + realm.getOwner().getUniqueId() + ".theme.spawn.pitch", realm.getTheme().getSpawn().getPitch());
        try {
            config.save(file);
        } catch (Exception e) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error while loading the realm.yml file", e);
        }

    }

    public void addPlayer(RealmPlayer rplayer, Realm realm) {
        config.set("realms." + realm.getOwner().getUniqueId() + ".players." + rplayer.getUniqueId() + ".name", rplayer.getName());
        try {
            config.save(file);
        } catch (Exception e) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error while loading the realm.yml file", e);
        }
    }

    public void promotePlayer(RealmPlayer rplayer, Realm realm) {
        config.set("realms." + realm.getOwner().getUniqueId() + ".players." + rplayer.getUniqueId() + ".rank", rplayer.getRankByRealm(realm).toString());
        try {
            config.save(file);
        } catch (Exception e) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error while loading the realm.yml file", e);
        }
    }

    public void removePlayer(RealmPlayer rplayer, Realm realm) {
        config.set("realms." + realm.getOwner().getUniqueId() + ".players." + rplayer.getUniqueId(), null);
        try {
            config.save(file);
        } catch (Exception e) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error while loading the realm.yml file", e);
        }
    }

    public void banPlayer(RealmPlayer rplayer, Realm realm) {
        if (realm.getBanned().size() == 0)
            config.set("realms." + realm.getOwner().getUniqueId() + ".banned", null);
        else
            config.set("realms." + realm.getOwner().getUniqueId() + ".banned." + rplayer.getUniqueId() + ".name", rplayer.getName());
        try {
            config.save(file);
        } catch (Exception e) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error while loading the realm.yml file", e);
        }
    }

    public void unbanPlayer(RealmPlayer rplayer, Realm realm) {
        config.set("realms." + realm.getOwner().getUniqueId() + ".banned." + rplayer.getUniqueId(), null);
        try {
            config.save(file);
        } catch (Exception e) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error while loading the realm.yml file", e);
        }
    }

    public void addNewPlayer(Player p) {
        config.set("realmplayers." + p.getUniqueId() + ".uuid", p.getUniqueId().toString());
        config.set("realmplayers." + p.getUniqueId() + ".name", p.getName());
        try {
            config.save(file);
        } catch (Exception ex) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error while loading the realm.yml file", ex);
        }

        if (RealmPlayer.getPlayer(p.getUniqueId().toString()) == null)
            new RealmPlayer(p.getUniqueId().toString(), p.getName());

    }

    public void setPerk(Realm realm) {
        config.set("realms." + realm.getOwner().getUniqueId() + ".perk", realm.getPerk());
        try {
            config.save(file);
        } catch (Exception ex) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error while loading the realm.yml file", ex);
        }
    }

    public void setPrivacy(Realm realm) {
        config.set("realms." + realm.getOwner().getUniqueId() + ".privacy", realm.getPrivacy());
        try {
            config.save(file);
        } catch (Exception ex) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error while loading the realm.yml file", ex);
        }
    }

    public void delete(Realm realm) {
        if (Realm.allrealm.size() == 0) {
            config.set("realms", null);
        } else
            config.set("realms." + realm.getOwner().getUniqueId(), null);
        try {
            config.save(file);
        } catch (Exception ex) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error while loading the realm.yml file", ex);
        }
    }

    public void setLevel(Realm realm) {
        config.set("realms." + realm.getOwner().getUniqueId() + ".level", realm.getLevel().getNumber());
        try {
            config.save(file);
        } catch (Exception ex) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error while loading the realm.yml file", ex);
        }
    }

    public void updatePlayerName(Player player) {
        RealmPlayer rp = RealmPlayer.getPlayer(player.getUniqueId().toString());
        for (Realm r : rp.getAllRealm()) {
            if (r.getOwner().getUniqueId().equals(player.getUniqueId().toString())) {
                config.set("realms." + r.getOwner().getUniqueId() + ".owner.name", player.getName());
            }
            config.set("realms." + r.getOwner().getUniqueId() + ".players." + rp.getUniqueId() + ".name", player.getName());
            rp.setName(player.getName());
        }
        for (String s : config.getConfigurationSection("realmplayers").getKeys(false)) {
            if (player.getUniqueId().toString().equals(s))
                config.set("realmplayers." + s + ".name", player.getName());
        }
        try {
            config.save(file);
        } catch (Exception ex) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error while loading the realm.yml file", ex);
        }
        rp.setName(player.getName());
    }

    public void updateLastVote(RealmPlayer rp) {
        config.set("realmplayers." + rp.getUniqueId() + ".lastvote", System.currentTimeMillis());
        rp.setLastvote(System.currentTimeMillis());
        try {
            config.save(file);
        } catch (Exception ex) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error while loading the realm.yml file", ex);
        }
    }

    public void loadRealm(String name) {
        try {
            if (config.getConfigurationSection("realms." + name + ".players") != null) {
                for (String s : config.getConfigurationSection("realms." + name + ".players").getKeys(false)) {
                    if (RealmPlayer.getPlayer(s) == null)
                        new RealmPlayer(s, config.getString("realms." + name + ".players." + s + ".name"));
                }
            }
            Location spawn = new Location(ConfigFiles.getWorld(),
                config.getInt("realms." + name + ".theme.spawn.x"),
                config.getInt("realms." + name + ".theme.spawn.y"),
                config.getInt("realms." + name + ".theme.spawn.z"),
                (float) config.getInt("realms." + name + ".theme.spawn.yaw"),
                (float) config.getInt("realms." + name + ".theme.spawn.pitch"));
            ThemeType themeType = ThemeType.themeTypes.get(config.getString("realms." + name + ".theme.id"));
            if (themeType == null) {
                themeType = ThemeType.allthemeTypes.isEmpty() ? null : ThemeType.allthemeTypes.get(0);
            }
            String ownerUuid = config.getString("realms." + name + ".owner.uuid");
            if (ownerUuid != null && RealmPlayer.getPlayer(ownerUuid) == null) {
                new RealmPlayer(ownerUuid, config.getString("realms." + name + ".owner.name"));
            }
            RealmPlayer owner = ownerUuid != null ? RealmPlayer.getPlayer(ownerUuid) : null;
            if (owner == null) return;
            Realm realm = new Realm(owner, themeType, spawn, Math.max(1, config.getInt("realms." + name + ".level")), Math.max(0, config.getInt("realms." + name + ".vote")));
            if (config.getConfigurationSection("realms." + name + ".players") != null) {
                for (String s : config.getConfigurationSection("realms." + name + ".players").getKeys(false)) {
                    RealmPlayer rp = RealmPlayer.getPlayer(s);
                    if (rp != null && rp.getOwned() != realm) {
                        realm.addPlayer(rp);
                        realm.promote(rp, RealmRank.getRankByString(config.getString("realms." + name + ".players." + s + ".rank")));
                    }
                }
            }
            realm.setPrivacy(config.getBoolean("realms." + name + ".privacy"));
            realm.setPerk(config.getString("realms."+name+".perk"));
            if (config.getConfigurationSection("realms." + name + ".banned") != null) {
                for (String s : config.getConfigurationSection("realms." + name + ".banned").getKeys(false)) {
                    if (RealmPlayer.getPlayer(s) == null)
                        new RealmPlayer(s, config.getString("realms." + name + ".banned." + s + ".name"));
                    RealmPlayer bannedPlayer = RealmPlayer.getPlayer(s);
                    if (bannedPlayer != null) realm.banPlayer(bannedPlayer);
                }
            }
        } catch (Exception ex) {
            Main.getInstance().getLogger().log(java.util.logging.Level.WARNING, "Failed to load realm: " + name, ex);
        }
    }

    private void loadNewRealmPlayer() {
        for (String s : config.getConfigurationSection("realmplayers").getKeys(false)) {
            if (RealmPlayer.getPlayer(s) == null)
                new RealmPlayer(config.getString("realmplayers." + s + ".uuid"), config.getString("realmplayers." + s + ".name"));
        }
    }

    public void loadAllRealm() {

        if (config.getConfigurationSection("realms") != null) {
            for (String s : config.getConfigurationSection("realms").getKeys(false)) {
                try {
                    loadRealm(s);
                } catch (Exception ex) {
                    Main.getInstance().getLogger().log(java.util.logging.Level.WARNING, "Skipping invalid realm: " + s, ex);
                }
            }
            try {
                loadNewRealmPlayer();
                for (String s : config.getConfigurationSection("realmplayers").getKeys(false)) {
                    RealmPlayer rp = RealmPlayer.getPlayer(s);
                    if (rp != null) {
                        rp.setLastvote(config.getLong("realmplayers." + s + ".lastvote"));
                        for (String sr : config.getStringList("realmplayers." + s + ".voted")) {
                            if (sr != null) {
                                RealmPlayer votedPlayer = RealmPlayer.getPlayer(sr);
                                if (votedPlayer != null && votedPlayer.getOwned() != null) {
                                    rp.addRealmVoted(votedPlayer.getOwned());
                                }
                            }
                        }
                    }
                }
                Main.getInstance().getLogger().info("Successfully loaded all realms !");
            } catch (Exception ex) {
                Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Failed to load realm players", ex);
            }
        }
    }
    public void removeVotes(Realm deletedrealm) {

        String deletedName = deletedrealm.getOwner().getUniqueId();
        for (String s : config.getConfigurationSection("realmplayers").getKeys(false)) {
            ArrayList<String> newvotelist = new ArrayList<>();
            for (String sr : config.getStringList("realmplayers." + s + ".voted")) {
                if (sr != null && !sr.equalsIgnoreCase(deletedName)) {
                    newvotelist.add(sr);
                }
            }
            config.set("realmplayers." + s + ".voted",newvotelist);
        }
        try {
            config.save(file);
        } catch (Exception ex) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error while loading the realm.yml file", ex);
        }
    }

    private Location getLastLocation() {
        if (config.getConfigurationSection("realms") == null) {
            return new Location(ConfigFiles.getWorld(), ConfigFiles.getRealmspacing(), 66, 0);
        }
        String laststring = Iterables.getLast(config.getConfigurationSection("realms").getKeys(false));
        ConfigurationSection section = config.getConfigurationSection("realms." + laststring + ".theme.spawn");
        if (section != null) {
            return new Location(ConfigFiles.getWorld(), section.getDouble("x"), 66, section.getDouble("z"));
        }
        return new Location(ConfigFiles.getWorld(), ConfigFiles.getRealmspacing(), 66, 0);
    }

    public Location getNewLocation() {
        int i = Realm.allrealm.size() % 20;
        if (i == 0 && Realm.allrealm.size() != 0)
            return getLastLocation().clone().add(-getLastLocation().getX(), 0, ConfigFiles.getRealmspacing());
        else
            return getLastLocation().clone().add(ConfigFiles.getRealmspacing(), 0, 0);
    }

    public void updateVote(Realm realm) {
        config.set("realms." + realm.getOwner().getUniqueId() + ".vote", realm.getVote());
        try {
            config.save(file);
        } catch (Exception e) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error while loading the realm.yml file", e);
        }
    }

    public void updateMultipleVote(RealmPlayer rp) {
        config.set("realmplayers." + rp.getUniqueId() + ".voted", rp.voteduuid);
        for (String s : rp.voteduuid)
            Main.getInstance().getLogger().info(s);
        try {
            config.save(file);
        } catch (Exception e) {
            Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Error while loading the realm.yml file", e);
        }
    }
    private void useless()
    {
        ArrayList<String> strs = new ArrayList<>();
        strs.forEach(s -> Main.getInstance().getLogger().info(s));
    }
}

