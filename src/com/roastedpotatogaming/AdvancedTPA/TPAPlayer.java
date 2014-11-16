package com.roastedpotatogaming.AdvancedTPA;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
    private ArrayList<UUID> inboundRequests = new ArrayList<UUID>();
    private ArrayList<UUID> outboundRequests = new ArrayList<UUID>();
    private ArrayList<UUID> blacklist = new ArrayList<UUID>();

    public static HashMap<UUID, TPAPlayer> getRegisteredPlayers() {
        return playerRegistry;
    }


    public static TPAPlayer getRegisteredPlayer(UUID id) {
        return playerRegistry.get(id);
    }

    /**
     * Gets the TPA player's Universally Unique Identifier
     * @return
     */
    public UUID getIdentifier() {
        return this.identifier;
    }

    public ArrayList<UUID> getOutboundRequests() {
        return this.outboundRequests;
    }

    public ArrayList<UUID> getInboundRequests() {
        return this.inboundRequests;
    }

    public boolean isRegistered() {
        if (playerRegistry.containsKey(identifier)) {
            return true;
        }
        return false;
    }

    public void addInboundRequest(UUID id) {
        this.inboundRequests.add(id);
    }

    public void removeInboundRequest(UUID id) {
        this.inboundRequests.remove(id);
    }

    public void addOutboundRequest(UUID id) {
        if (!(playerRegistry.get(id).blacklist.contains(this.getIdentifier()))) {
            this.outboundRequests.add(id);
            playerRegistry.get(id).addInboundRequest(this.getIdentifier());
        }
    }

    public void removeOutboundRequest(UUID id) {
        this.outboundRequests.remove(id);
        TPAPlayer.getRegisteredPlayer(id).removeInboundRequest(this.getIdentifier());
    }

    public void teleportTo(TPAPlayer targ) {
        Player src = Bukkit.getPlayer(this.getIdentifier());
        Player dest = Bukkit.getPlayer(targ.getIdentifier());

        src.teleport(dest.getLocation());
    }

    public boolean isBanned() {
        if (AdvancedTPA.getBanList().containsKey(this.getIdentifier())) {
            return true;
        }
        return false;
    }

    public boolean isBlacklisted(UUID id) {
        return blacklist.contains(id);
    }

    public boolean blacklistPlayer(UUID id) {
        if (!blacklist.contains(id)) {
            blacklist.add(id);
            return true;
        }
        return false;
    }

    public boolean unblacklistPlayer(UUID id) {
        if (blacklist.contains(id)) {
            blacklist.remove(id);
            return true;
        }
        return false;
    }

    private void RegisterTPAPlayer(TPAPlayer p) {
        if (!playerRegistry.containsKey(p.getIdentifier())) {
            playerRegistry.put(p.getIdentifier(), p);
        }
    }


    public static void RemoveTPAPlayer(UUID id) {
        if (playerRegistry.containsKey(id)) {
            TPAPlayer tpap = playerRegistry.get(id);
            for (int i = 0; i < tpap.getOutboundRequests().size(); i++) {
                TPAPlayer t = TPAPlayer.getRegisteredPlayer(tpap.getOutboundRequests().get(i));
                Bukkit.getPlayer(t.getIdentifier()).sendMessage(AdvancedTPA.ChatPrefix + "Player \"" + Bukkit.getPlayer(tpap.getIdentifier()).getName() + "\" left the game. The pending request was cancelled!");
                t.getInboundRequests().remove(tpap.getIdentifier());
                tpap.getOutboundRequests().remove(i);
            }
            for (int i = 0; i < tpap.getInboundRequests().size(); i++) {
                TPAPlayer t = TPAPlayer.getRegisteredPlayer(tpap.getInboundRequests().get(i));
                Bukkit.getPlayer(t.getIdentifier()).sendMessage(AdvancedTPA.ChatPrefix + "Player \"" + Bukkit.getPlayer(tpap.getIdentifier()).getName() + "\" left the game. The pending request was cancelled!");
                t.getOutboundRequests().remove(tpap.getIdentifier());
                tpap.getInboundRequests().remove(i);
            }
            playerRegistry.remove(id);
            AdvancedTPA.logger.log(Level.INFO, "TPAPlayer " + Bukkit.getPlayer(id).getDisplayName() + " deleted successfully.");
        }
    }

    public TPAPlayer(UUID id) {
        this.identifier = id;
        RegisterTPAPlayer(this);
    }

    public TPAPlayer(Player p) {
        this(p.getUniqueId());
    }

}
