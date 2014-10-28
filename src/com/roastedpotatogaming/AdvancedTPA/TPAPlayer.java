package com.roastedpotatogaming.AdvancedTPA;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import sun.util.logging.PlatformLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by azdaspaz818 (RPG OWNER) on 28/10/2014.
 */
public class TPAPlayer {
    private static HashMap<UUID, TPAPlayer> _playerRegistry = new HashMap<UUID, TPAPlayer>();

    private UUID _identifier;
    private ArrayList<TPAPlayer> _inboundRequests = new ArrayList<TPAPlayer>();
    private ArrayList<TPAPlayer> _outboundRequests = new ArrayList<TPAPlayer>();

    public static HashMap<UUID, TPAPlayer> getRegisteredPlayers() {
        return _playerRegistry;
    }

    public UUID getIdentifier() {
        return _identifier;
    }

    public static void RegisterTPAPlayer(TPAPlayer p) {
        _playerRegistry.put(p.getIdentifier(), p);
    }

    public static void RemoverTPAPlayer(UUID id) {
        if (_playerRegistry.containsKey(id)) {
            _playerRegistry.remove(id);
            AdvancedTPA.logger.log(Level.INFO, "TPAPlayer " + Bukkit.getPlayer(id).getDisplayName() + " deleted successfully.");
        }
    }

    public TPAPlayer(UUID id) {
        _identifier = id;
    }

    public TPAPlayer(Player p) {
        this(p.getUniqueId());
    }

}
