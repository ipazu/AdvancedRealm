package fr.ipazu.advancedrealm.realm.themes;

import fr.ipazu.advancedrealm.Main;
import fr.ipazu.advancedrealm.utils.CuboidUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import java.util.ArrayList;

public class Theme {

    private Location spawn;
    private ThemeType themeType;
    private CuboidUtils cuboid;

    public Theme(ThemeType themeType, Location spawn) {
        this.spawn = spawn;
        this.themeType = themeType;
        if (themeType != null) {
            loadCuboid();
        }
    }

    public void spawnTheme() {
        removeTheme();
        themeType.pasteTheme(spawn);
    }

    private void loadCuboid() {
        Location pos1 = new Location(spawn.getWorld(), spawn.clone().getX() + 30, spawn.clone().getY() + 30, spawn.clone().getZ() + themeType.getNblock());
        Location pos2 = new Location(spawn.getWorld(), spawn.clone().getX() - 30, spawn.clone().getY() - 30, spawn.clone().getZ() - 20);
        cuboid = new CuboidUtils(pos1, pos2);
    }

    public void setThemeType(ThemeType themeType) {
        this.themeType = themeType;
        loadCuboid();
    }
    public CuboidUtils getCuboid(){
        return cuboid;
    }
    public ThemeType getThemeType() {
        return themeType;
    }

    public void removeTheme() {
        for (Block block : cuboid.getBlocks()) {
            block.setType(Material.AIR);
        }
        removeItems();
    }

    private void removeItems() {
        for (Entity entity : spawn.getWorld().getEntities()) {
            if (entity instanceof Item) {
                if (cuboid.containsLocation(entity.getLocation())) {
                    entity.remove();
                }
            }
        }
    }

    public Location getSpawn() {
        return spawn;
    }

    private void useless()
    {
        ArrayList<String> strs = new ArrayList<>();
        strs.forEach(s -> Main.getInstance().getLogger().info(s));
    }
}
