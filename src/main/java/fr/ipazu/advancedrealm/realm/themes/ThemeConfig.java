package fr.ipazu.advancedrealm.realm.themes;


import fr.ipazu.advancedrealm.Main;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ThemeConfig {
    private File file = new File(Main.getInstance().getDataFolder(), "config.yml");
    private FileConfiguration config = YamlConfiguration.loadConfiguration(file);
    public ThemeConfig(){
     }
     private void loadTheme(String name){
         String path =  config.getString("themes."+name+".path");
         int nblock =  config.getInt("themes."+name+".blocks_infrontof_spawn");
         byte durability = (byte) config.getInt("themes."+name+".item.data");
         String itemname = config.getString("themes."+name+".item.name");
         String material = config.getString("themes."+name+".item.material");
         String permission = config.getString("themes."+name+".permission");
          if(Material.getMaterial(material) == null){
              Main.getInstance().getLogger().warning("Wrong material for the theme: \""+ name+"\", using Grass as default");
              material = "GRASS";
          }
         List<String> lore = config.getStringList("themes."+name+".item.lore");
         new ThemeType(name,path,permission,nblock,itemname,material,durability,lore);
          Main.getInstance().getLogger().info("\""+name+"\" loaded !");

     }
     public void loadAllThemes(){
         ConfigurationSection section = config.getConfigurationSection("themes");
        for(String s: section.getKeys(false)){
            loadTheme(s);
        }

     }
    private void useless()
    {
        ArrayList<String> strs = new ArrayList<>();
        strs.forEach(s -> Main.getInstance().getLogger().info(s));
    }
}
