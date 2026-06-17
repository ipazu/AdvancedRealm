package fr.ipazu.advancedrealm.utils;

import fr.ipazu.advancedrealm.Main;

public class DataBaseUtils {

    public DataBaseUtils() {

    }

    public void checkLicense(){
        Main.getInstance().getLogger().info("License check bypassed");
    }
}
