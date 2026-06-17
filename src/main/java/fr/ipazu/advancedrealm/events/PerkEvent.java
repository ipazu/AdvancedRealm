package fr.ipazu.advancedrealm.events;

import fr.ipazu.advancedrealm.realm.Realm;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class PerkEvent implements Listener {
    @EventHandler
    public void onCrops(BlockGrowEvent event){
        if(Realm.getRealmFromLocation(event.getBlock().getLocation()) != null){
            Realm realm = Realm.getRealmFromLocation(event.getBlock().getLocation());
            if(realm.getPerk() != null){
                if(realm.getPerk().equalsIgnoreCase("crops")){
                    Ageable ageable = (Ageable) event.getBlock().getBlockData();
                    ageable.setAge(Math.min(ageable.getAge() + 1, ageable.getMaximumAge()));
                    event.getBlock().setBlockData(ageable);
                }
            }
        }
    }
    @EventHandler
    public void onXp(PlayerExpChangeEvent event){
        if(Realm.getRealmFromLocation(event.getPlayer().getLocation()) != null){
            Realm realm = Realm.getRealmFromLocation(event.getPlayer().getLocation());
            if(realm.getPerk() != null){
                if(realm.getPerk().equalsIgnoreCase("xp")){
                    int expnew = (int)Math.round(event.getAmount() * 1.5);
                    event.setAmount(expnew);
                }
            }
        }
    }
}
