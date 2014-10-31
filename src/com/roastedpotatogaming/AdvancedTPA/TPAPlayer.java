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
    private static HashMap<UUID, TPAPlayer> playerRegistry = new HashMap<UUID, TPAPlayer>();

    private UUID identifier;
    private ArrayList<TPAPlayer> inboundRequests = new ArrayList<TPAPlayer>();
    private ArrayList<TPAPlayer> outboundRequests = new ArrayList<TPAPlayer>();
    private ArrayList<TPAPlayer> blacklist = new ArrayList<TPAPlayer>();

    public static HashMap<UUID, TPAPlayer> getRegisteredPlayers() {
        return playerRegistry;
    }

    /**
     *
     * @return
     */
    public UUID getIdentifier() {
        return this.identifier;
    }

    public ArrayList<TPAPlayer> getOutboundRequests() {
        return this.outboundRequests;
    }

    public boolean isRegistered() {
        if (playerRegistry.containsKey(identifier)) {
            return true;
        }
        return false;
    }

    public void addInboundRequest(TPAPlayer tpp) {
        this.inboundRequests.add(tpp);
    }

    public void addOutboundRequest(TPAPlayer tpp) {
        if (!(tpp.blacklist.contains(this))) {
            this.outboundRequests.add(tpp);
            tpp.addInboundRequest(this);
        }
    }

    public static void RegisterTPAPlayer(TPAPlayer p) {
        if (!playerRegistry.containsKey(p.getIdentifier())) {
            playerRegistry.put(p.getIdentifier(), p);
        }
    }

    public static void RemoveTPAPlayer(UUID id) {
        if (playerRegistry.containsKey(id)) {
            playerRegistry.remove(id);
            AdvancedTPA.logger.log(Level.INFO, "TPAPlayer " + Bukkit.getPlayer(id).getDisplayName() + " deleted successfully.");
        }
    }

    public TPAPlayer(UUID id) {
        this.identifier = id;
    }

    public TPAPlayer(Player p) {
        this(p.getUniqueId());
    }

}
