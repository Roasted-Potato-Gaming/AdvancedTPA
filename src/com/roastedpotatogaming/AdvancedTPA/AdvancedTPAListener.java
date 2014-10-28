package com.roastedpotatogaming.AdvancedTPA;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by azdaspaz818 (RPG OWNER) on 29/10/2014.
 */
public class AdvancedTPAListener implements Listener {

    AdvancedTPA _plugin;

    public AdvancedTPAListener(AdvancedTPA atpa) {
        _plugin = atpa;
        Bukkit.getPluginManager().registerEvents(this, _plugin);
    }


    @EventHandler
    public void AddAchievementPlayer(PlayerJoinEvent e) {
        TPAPlayer.RegisterTPAPlayer(new TPAPlayer(e.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void RemoveAchievementPlayer(PlayerQuitEvent e) {
        TPAPlayer.RemoverTPAPlayer(e.getPlayer().getUniqueId());
     }

}
