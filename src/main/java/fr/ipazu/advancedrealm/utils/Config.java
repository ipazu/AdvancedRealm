package fr.ipazu.advancedrealm.utils;

import fr.ipazu.advancedrealm.Main;
import fr.ipazu.advancedrealm.realm.Realm;
import fr.ipazu.advancedrealm.realm.RealmLevel;
import fr.ipazu.advancedrealm.realm.RealmPlayer;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public enum Config {
    CONFIG(Main.getInstance().getDataFolder(),"config.yml",true),
    REALM(Main.getInstance().getDataFolder(),"realm.yml",false),
    ASPECT(Main.getInstance().getDataFolder(),"aspect.yml",true),
    UPGRADES(Main.getInstance().getDataFolder(),"upgrades.yml",true);


    private String name;
    private YamlConfiguration config;
    private File folder;
    private boolean copydefault;
    Config(File folder, String name,boolean copydefault) {
        this.name = name;
        this.folder = folder;
        this.copydefault = copydefault;
    }

    public String getFileName() {
        return name;
    }

    public YamlConfiguration getConfig() {
        return config;
    }
    public boolean getCopyDefault(){
        return copydefault;
    }
    public File getFolder(){
        return folder;
    }

    private static final java.util.Map<String, String> MATERIAL_ALIASES = new java.util.HashMap<>();
    static {
        MATERIAL_ALIASES.put("DARK_OAK_DOOR_ITEM", "DARK_OAK_DOOR");
        MATERIAL_ALIASES.put("WOOD_SWORD", "WOODEN_SWORD");
        MATERIAL_ALIASES.put("SKULL_ITEM", "PLAYER_HEAD");
        MATERIAL_ALIASES.put("STAINED_GLASS_PANE", "BLACK_STAINED_GLASS_PANE");
        MATERIAL_ALIASES.put("BED", "RED_BED");
        MATERIAL_ALIASES.put("WATCH", "CLOCK");
        MATERIAL_ALIASES.put("INK_SACK", "BLACK_DYE");
        MATERIAL_ALIASES.put("WOOD", "OAK_PLANKS");
        MATERIAL_ALIASES.put("STEP", "STONE_SLAB");
    }

    public static Material getMaterial(String name){
        if (name == null) return Material.STONE;
        String upper = name.toUpperCase();
        Material material = Material.getMaterial(upper);
        if (material != null) return material;
        String alias = MATERIAL_ALIASES.get(upper);
        if (alias != null) {
            material = Material.getMaterial(alias);
            if (material != null) return material;
        }
        return Material.STONE;
    }
    public static String getStringWithReplacementPlayer(String oldstring, Realm r, RealmPlayer rp){
        String newstring = getStringWithReplacementRealm(oldstring,r);
        newstring = pushColor(newstring);
        newstring = newstring.replace("%player_name%",rp.getName());
        newstring = newstring.replace("%targeted_player%",rp.getName());

        if(r.getRealmMembers().contains(rp))
        newstring = newstring.replace("%player_rank%",rp.getRankByRealm(r).toString());
        return newstring;
    }
    public static String getStringWithReplacementRealm(String oldstring, Realm r){
        String newstring = oldstring;
        newstring = pushColor(newstring);
        newstring = newstring.replace("%realm_name%",r.getOwner().getName());
        newstring = newstring.replace("%realm_privacy%",r.getPrivacyString());
        newstring = newstring.replace("%realm_bordersize%",r.getLevel().getBordersize()+"");
        newstring = newstring.replace("%realm_maxplayer%",r.getLevel().getMaxplayer()+"");
        newstring = newstring.replace("%realm_level%",r.getLevel().getNumber()+"");
        RealmLevel nextlevel = RealmLevel.getLevel(r.getLevel().getNumber() + 1);
        if(nextlevel != null){
            newstring = newstring.replace("%realm_nextbordersize%",nextlevel.getBordersize()+"");
            newstring = newstring.replace("%realm_nextmaxplayer%",nextlevel.getMaxplayer()+"");
            newstring = newstring.replace("%realm_nextlevelcost%",nextlevel.getPrice()+"");
            newstring = newstring.replace("%realm_nextlevel%",nextlevel.getNumber()+"");
        }

        return newstring;
    }

    public static String pushColor(String oldstring){
        String newstring = oldstring;
        newstring = newstring.replace("&","§");
        return newstring;
    }
    public static List<String> getListWithReplacementPlayer(List<String> oldlist, Realm r,RealmPlayer rp){
        List<String> newlist = new ArrayList<>();
        for(String s : oldlist){
            newlist.add(getStringWithReplacementPlayer(s,r,rp));
        }
        return newlist;
    }
    public static List<String> getListWithReplacementRealm(List<String> oldlist, Realm r){
        List<String> newlist = new ArrayList<>();
        for(String s : oldlist){
            newlist.add(getStringWithReplacementRealm(s,r));
        }
        return newlist;
    }
    public static int getCollumFromInt(int i){
      int collum = i -9*getRowFromInt(i);
      return collum;
    }
    public static int getRowFromInt(int i){
       int row = i/9;
       return row;
    }
    public File getFile(){
        return new File(getFolder(),getFileName());
    }
    public void setConfig(YamlConfiguration config) {
        this.config = config;
    }
}
