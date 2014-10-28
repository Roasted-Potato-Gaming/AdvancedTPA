package com.roastedpotatogaming.AdvancedTPA;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Created by azdaspaz818 (RPG OWNER) on 28/10/2014.
 */
public class AdvancedTPA extends JavaPlugin {
    public static final Logger logger = Logger.getLogger("Minecraft");
    protected static AdvancedTPA plugin;

    @Override
    public void onEnable() {
        new AdvancedTPAListener(this);
    }
    
    public void onDisable() {

    }

}
