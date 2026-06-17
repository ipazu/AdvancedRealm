package fr.ipazu.advancedrealm.gui;


import fr.ipazu.advancedrealm.gui.allrealms.*;
import fr.ipazu.advancedrealm.realm.Realm;
import fr.ipazu.advancedrealm.realm.RealmPlayer;
import fr.minuskube.inv.SmartInventory;
import org.bukkit.entity.Player;

public class WholeGUI {
    public WholeGUI() {

    }

    public void openRealmGui(Player player, Realm realm,boolean from) {
        SmartInventory inventory = SmartInventory.builder()
                .provider(new RealmProvider(player, realm,from))
                .size(5, 9)
                .title("Realm")
                .build();
        inventory.open(player);
    }

    public void openMembersGui(Player player, Realm realm) {
        SmartInventory inventory = SmartInventory.builder()
                .provider(new MembersProvider(player, realm))
                .size(5, 9)
                .title("Realm members")
                .build();
        inventory.open(player);
    }
    public void openBanned(Player player, Realm realm) {
        SmartInventory inventory = SmartInventory.builder()
                .provider(new BannedProvider(player, realm))
                .size(5, 9)
                .title("Banned players")
                .build();
        inventory.open(player);
    }
    public void openRankGui(RealmPlayer vised,Player player, Realm realm) {
        SmartInventory inventory = SmartInventory.builder()
                .provider(new EditMemberProvider(player, realm,vised))
                .size(5, 9)
                .title("Realm members -> "+vised.getName())
                .build();
        inventory.open(player);
    }
    public void openUpgradeConfirmGui(Player player, Realm realm) {
        SmartInventory inventory = SmartInventory.builder()
                .provider(new UpgradeConfirmProvider(player, realm))
                .size(3, 9)
                .title("Confirm Upgrade")
                .build();
        inventory.open(player);
    }
    public void openUnclaimGui(Player player,Realm realm) {
        SmartInventory inventory = SmartInventory.builder()
                .provider(new RemoveProvider(player, realm))
                .size(3, 9)
                .title("Please confirm")
                .build();
        inventory.open(player);
    }
    public void openThemeGui(Player player, Realm realm) {
        SmartInventory inventory = SmartInventory.builder()
                .provider(new ThemeProvider(player, realm))
                .size(3, 9)
                .title("Choose a Theme")
                .build();
        inventory.open(player);
    }

    public void openAllInviteGUI(Player player, Player vised) {
        getAllInviteGUI(player, vised).open(player);
    }
    public void openAllKickGUI(Player player, RealmPlayer vised) {
        getAllKickGUI(player, vised).open(player);
    }
    public void openAllBanGUI(Player player, RealmPlayer vised) {
        getAllBanGUI(player, vised).open(player);
    }
    public void openAllHomeGUI(Player player) {
        getAllHome(player).open(player);
    }
    public void openAllRealmGUI(Player player) {
        getAllRealmGUI(player).open(player);
    }
    public void openPerkGUI(Player player, RealmPlayer realmPlayer) { getPerkGui(player,realmPlayer).open(player);}
    public void openTopGUI(Player player) { getTopGui(player).open(player);}


    public SmartInventory getPerkGui(Player player, RealmPlayer realmPlayer) {
        return SmartInventory.builder()
                .provider(new PerkProvider(player, realmPlayer))
                .size(1, 9)
                .title("Choose your specialization")
                .build();
    }
    public SmartInventory getTopGui(Player player) {
        return SmartInventory.builder()
                .provider(new TopProvider(player))
                .size(5, 9)
                .title("Realm Top")
                .build();
    }
    public SmartInventory getAllInviteGUI(Player player, Player vised) {
        return SmartInventory.builder()
                .provider(new AllInviteProvider(player, vised))
                .size(1, 9)
                .title("Select Realm")
                .build();
    }
    public SmartInventory getAllRealmGUI(Player player) {
        return SmartInventory.builder()
                .provider(new AllRealmProvider(player))
                .size(3, 9)
                .title("Select a Realm")
                .build();

    }
    public SmartInventory getAllHome(Player player) {
        return SmartInventory.builder()
                .provider(new AllHomeProvider(player))
                .size(3, 9)
                .title("Select a Realm")
                .build();

    }
    public SmartInventory getAllKickGUI(Player player,RealmPlayer vised) {
        return SmartInventory.builder()
                .provider(new AllKickProvider(player,vised))
                .size(1, 9)
                .title("Select a Realm")
                .build();

    }
    public SmartInventory getAllBanGUI(Player player,RealmPlayer vised) {
        return SmartInventory.builder()
                .provider(new AllBanProvider(player,vised))
                .size(1, 9)
                .title("Select a Realm")
                .build();

    }
}

