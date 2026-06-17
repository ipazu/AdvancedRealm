package fr.ipazu.advancedrealm.realm;

import fr.ipazu.advancedrealm.Main;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class RealmPlayer {
    public static HashMap<String, RealmPlayer> realmPlayer = new HashMap<>();
    public static HashMap<String, RealmPlayer> realmPlayername = new HashMap<>();
    public HashMap<Realm, RealmRank> rankbyrealm = new HashMap<>();
    public ArrayList<String> voteduuid = new ArrayList<>();
    public ArrayList<Realm> realmwaiting = new ArrayList<>();
    private long lastvote;
    private Realm owned;
    private ArrayList<Realm> allrealm = new ArrayList<>();
    private String name;
    private String stringuuid;

    public RealmPlayer(String uuid,String name) {
        realmPlayer.put(uuid, this);
        realmPlayername.put(name.toLowerCase(),this);
        this.name = name;
        this.stringuuid = uuid;
    }

    public Realm getOwned() {
        return owned;
    }

    public void setOwned(Realm owned) {
        this.owned = owned;
    }
    public void addWaiting(Realm realm){
        realmwaiting.add(realm);
    }
    public void removeWaiting(Realm realm){
        realmwaiting.remove(realm);
    }
    public static RealmPlayer getPlayer(String uuid){
         return realmPlayer.get(uuid);
    }
    public static RealmPlayer getPlayerFromName(String name){
        return realmPlayername.get(name.toLowerCase());
    }
    public String getUniqueId(){
        return stringuuid;
    }
    public RealmRank getRankByRealm(Realm realm) {
        return rankbyrealm.get(realm);
    }
    public ArrayList<Realm> getAllRealm() {
        return allrealm;
    }
    public void addRealm(Realm realm){
        allrealm.add(realm);
    }
    public void remove(Realm realm){
        allrealm.remove(realm);
    }
    public String getName() {
        return name;
    }
    public void addRealmVoted(Realm r){
        voteduuid.add(r.getOwner().getUniqueId());
    }
    public void setName(String name) {
        this.name = name;
    }

    public long getLastvote() {
        return lastvote;
    }

    public void setLastvote(long lastvote) {
        this.lastvote = lastvote;
    }
    private void useless()
    {
        ArrayList<String> strs = new ArrayList<>();
        strs.forEach(s -> Main.getInstance().getLogger().info(s));
    }

}


