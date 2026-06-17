package fr.ipazu.advancedrealm.realm;


import fr.ipazu.advancedrealm.Main;
import org.bukkit.ChatColor;

import java.util.ArrayList;

public enum RealmRank {
    MEMBER("Member", ChatColor.AQUA), GUARD("Guard",ChatColor.GOLD), MANAGER("Manager",ChatColor.LIGHT_PURPLE), OWNER("Owner",ChatColor.YELLOW);
    String rank;
    ChatColor color;

    RealmRank(String rank,ChatColor c) {
        this.rank = rank;
        this.color = c;
    }

    public String toString() {
        return rank;
    }
    public ChatColor getColor(){
        return color;
    }

    public static RealmRank getRankByString(String s) {
        for (RealmRank realmrank : RealmRank.values()) {
            if (realmrank.toString().equalsIgnoreCase(s)){
                return realmrank;
            }
        }
        return null;
    }
    private void useless()
    {
        ArrayList<String> strs = new ArrayList<>();
        strs.forEach(s -> Main.getInstance().getLogger().info(s));
    }
}
