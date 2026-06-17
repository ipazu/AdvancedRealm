package fr.ipazu.advancedrealm.realm.themes;
import fr.ipazu.advancedrealm.Main;
import fr.ipazu.advancedrealm.utils.Config;
import fr.ipazu.advancedrealm.utils.ItemsUtils;
import fr.ipazu.advancedrealm.utils.SchematicUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThemeType {
        private String schematic;
        private String name;
        List<String> lore;
        private String id;
        private byte durability;
        private String itemname;
        private int nblock;
        private String permission;
        public static HashMap<String,ThemeType> themeTypes = new HashMap<>();
        public static ArrayList<ThemeType> allthemeTypes = new ArrayList<>();

        public ThemeType(String name, String path, String permission, int nblock, String itemname, String id, byte durability, List<String> lore) {
            this.schematic = "theme/"+path;
            this.nblock = nblock;
            this.name = name;
            this.itemname = itemname.replace("&","§");
            ArrayList<String > newlorelist = new ArrayList<>();
            for(String newlore : lore){
                newlorelist.add(newlore.replace("&","§"));
            }
            this.lore = newlorelist;
            this.durability = durability;
            this.permission = permission;
            this.id = id;
            themeTypes.put(name,this);
            allthemeTypes.add(this);
        }

        public void pasteTheme(Location spawn) {
            if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
                Main.getInstance().getLogger().warning("WorldEdit not found. Install WorldEdit to enable theme pasting.");
                return;
            }
            try {
                File file = new File(Main.getInstance().getDataFolder(), this.schematic);
                new SchematicUtils(spawn,file).paste();

            } catch (Exception e) {
                Main.getInstance().getLogger().log(java.util.logging.Level.SEVERE, "Failed to load schematic", e);
            }

        }

    public int getNblock() {
        return nblock;
    }
    public ItemStack getItem(){
        return new ItemsUtils(Config.getMaterial(id),itemname,durability,lore).toItemStack();
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }
    private void useless()
    {
        ArrayList<String> strs = new ArrayList<>();
        strs.forEach(s -> Main.getInstance().getLogger().info(s));
    }
}
