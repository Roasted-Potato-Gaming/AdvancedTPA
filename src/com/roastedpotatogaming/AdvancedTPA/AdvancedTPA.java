package com.roastedpotatogaming.AdvancedTPA;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by azdaspaz818 (RPG OWNER) on 28/10/2014.
 */
public class AdvancedTPA extends JavaPlugin {
    public static final Logger logger = Logger.getLogger("Minecraft");
    protected static AdvancedTPA plugin;
    private static final HashMap<UUID, TPAPlayer> banList = new HashMap<UUID, TPAPlayer>();
    public static final String ChatPrefix = ChatColor.YELLOW + "[ATPA] " + ChatColor.WHITE;

    public static HashMap<UUID, TPAPlayer> getBanList() {
        return AdvancedTPA.banList;
    }

    @Override
    public void onEnable() {
        new AdvancedTPAListener(this);
        registerExistingPlayers();
    } //on enable bc github

    @Override
    public void onDisable() {
        TPAPlayer.getRegisteredPlayers().clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            TPAPlayer s = TPAPlayer.getRegisteredPlayer(p.getUniqueId());
            if (cmd.getName().equalsIgnoreCase("tpa")) {
                if (banList.containsKey(p.getUniqueId())) {
                    p.sendMessage(ChatPrefix + "You are currently banned and unable to make tpa requests");
                    return true;
                }
                Player targ;
                if(args.length == 0 || args[0].equalsIgnoreCase("help")) { //Getting help (/tpa help || /tpa)
                    sendHelp(p);
                    return true;
                } else if (args.length == 1) { //Sending requests (/tpa <player>)
                    TPAPlayer t;
                    if (Bukkit.getPlayer(args[0]) != null) {
                        targ = Bukkit.getPlayer(args[0]);
                        if (targ == null) {
                            p.sendMessage(ChatPrefix + "Target is not online.");
                        }
                        t = TPAPlayer.getRegisteredPlayer(targ.getUniqueId());
                        if (!t.isBlacklisted(s.getIdentifier())) { //Sender isn't blacklisted, add the request and send messages.
                            if (targ != p) {
                                if (!s.getOutboundRequests().contains(t.getIdentifier())) {
                                    s.addOutboundRequest(t.getIdentifier());
                                    p.sendMessage(ChatPrefix + "A request was sent to " + targ.getName());
                                    targ.sendMessage(ChatPrefix + "You have an incoming TP request from " + p.getName() + ". Type \'/tpaccept " + p.getName() + "\' to accept");
                                } else { //Request already exists
                                    p.sendMessage(ChatPrefix + "You have already sent a request to \"" + targ.getName() + ".\"");
                                }
                            } else { //Teleport to self.
                                p.sendMessage(ChatPrefix + "You cannot send a teleport request to yourself!");
                            }
                        } else { //Tell sender that they are blacklisted for the other player.
                            p.sendMessage(ChatPrefix + "Player \'" + targ.getName() + "\' has blacklisted you! Unable to send request!");
                        }
                    } else { //Must be list
                        if (args[0].equalsIgnoreCase("list")) {
                            p.sendMessage(ChatPrefix + "Current inbound requests (type /tpaccept <player> to accept):");
                            if (s.getInboundRequests().size() == 0) {
                                p.sendMessage("    You have no requests!");
                            } else {
                                System.out.println("Inbound requests: " + s.getInboundRequests().size());
                                for (int i = 0; i < s.getInboundRequests().size(); i++) {
                                    p.sendMessage("    " + Bukkit.getPlayer(s.getInboundRequests().get(i)).getName());
                                }
                            }
                            p.sendMessage(ChatPrefix + "Current outbound requests:");
                            if (s.getOutboundRequests().size() == 0) {
                                p.sendMessage("    You have no requests! Use \'/tpa <player>\' to make one!");
                            } else {
                                System.out.println("Outbound requests: " + s.getOutboundRequests().size());
                                for (int i = 0; i < s.getOutboundRequests().size(); i++) {
                                    p.sendMessage("    " + Bukkit.getPlayer(s.getOutboundRequests().get(i)).getName());
                                }
                            }
                        } else {
                            return false;
                        }
                        return true;
                    }
                    return true;
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("ban")) { //Banning players (/tpa ban <player>)
                        if (!banPlayer(args[1])) {
                            p.sendMessage(ChatPrefix + "Player is already banned or does not exist.");
                        } else {
                            p.sendMessage(ChatPrefix + "Banned player " + args[1]);
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("unban")) { //Unbanning players (/tpa unban <player>)
                        if (!unbanPlayer(args[1])) {
                            p.sendMessage(ChatPrefix + "Player \"" + args[1] + "\" is not banned from TPA or does not exist.");
                        } else {
                            p.sendMessage(ChatPrefix + "Unbanned " + args[1] + ". They can now make TPA requests again.");
                        }
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else if (cmd.getName().equalsIgnoreCase("tpaccept")) { //Accepting TP requests (/tpaccept <player>)
                TPAPlayer t;
                Player targ;
                if (args.length == 1) {
                    targ = Bukkit.getPlayer(args[0]);
                    if (targ != null) {
                        t = TPAPlayer.getRegisteredPlayer(targ.getUniqueId());
                        if (t.getOutboundRequests().contains(s.getIdentifier())) {
                            if (s.getInboundRequests().contains(t.getIdentifier())) {
                                p.sendMessage(ChatPrefix + "Teleporting to " + ChatColor.UNDERLINE + targ.getName() + ".");
                                t.teleportTo(s);
                                t.removeOutboundRequest(s.getIdentifier());
                                s.removeInboundRequest(t.getIdentifier());
                            } else {
                                p.sendMessage(ChatPrefix + "No inbound request from " + ChatColor.UNDERLINE + targ.getName() + ".");
                            }
                        } else {
                            p.sendMessage(ChatPrefix + "Something went wrong... Target has no outbound request for you.");
                        }
                    } else {
                        p.sendMessage(ChatPrefix + "Player not found; request can't be accepted!");
                    }
                } else if(args.length == 0) {
                    if (s.getInboundRequests().size() > 0) {
                        s.teleportTo(TPAPlayer.getRegisteredPlayer(s.getInboundRequests().get(0)));
                    }
                } else {
                    p.sendMessage(ChatPrefix + "Please specify the target player!");
                }
                return true;
            } else if (cmd.getName().equalsIgnoreCase("tpblacklist")) { //Blacklisting players (/tpblacklist add <player> || /tpblacklist remove <player>)
                if (args.length == 2) {
                    Player t = Bukkit.getPlayer(args[1]);
                    if (t != null) {
                        if (args[0].equalsIgnoreCase("add")) {
                            if (s.blacklistPlayer(t.getUniqueId())) {
                                s.removeInboundRequest(t.getUniqueId());
                                s.removeOutboundRequest(t.getUniqueId());
                                TPAPlayer.getRegisteredPlayer(t.getUniqueId()).removeInboundRequest(s.getIdentifier());
                                TPAPlayer.getRegisteredPlayer(t.getUniqueId()).removeOutboundRequest(s.getIdentifier());
                                p.sendMessage(ChatPrefix + "Player \'" + t.getName() + "\' has been successfully added to your blacklist.");
                            } else {
                                p.sendMessage(ChatPrefix + "Player \'" + t.getName() + "\' is already in your blacklist!");
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (s.unblacklistPlayer(t.getUniqueId())) {
                                p.sendMessage(ChatPrefix + "Player \'" + t.getName() + "\' has been successfully removed from your blacklist!");
                            } else {
                                p.sendMessage(ChatPrefix + "Player \'" + t.getName() + "\' isn't on your blacklist.");
                            }
                        }
                    } else {
                        p.sendMessage(ChatPrefix + "ERROR: Please relog! If problem persists, contact and administrator.");
                    }
                    return true;
                }
            }
            return false;
        } else { //Sender is console
            if (cmd.getName().equalsIgnoreCase("tpa") && args.length == 2) { //Console can ban TPA users, and unban them
                if (args[0].equalsIgnoreCase("ban")) {
                    if (!banPlayer(args[1])) {
                        logger.log(Level.INFO, "[" + this.getDescription().getPrefix() + "] Attempted to ban player " + args[1] + ". Player already banned.");
                    } else {
                        logger.log(Level.INFO, "[" + this.getDescription().getPrefix() + "] Banned player " + args[1] + " from TPA.");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("unban")) {
                    if (!unbanPlayer(args[1])) {
                        logger.log(Level.INFO, "[" + this.getDescription().getPrefix() + "] Attempted to unban player " + args[1] + ". Player not banned.");
                    } else {
                        logger.log(Level.INFO, "[" + this.getDescription().getPrefix() + "] Unbanned player " + args[1] + ". They can now use \"/tpa\" again");
                    }
                    return true;
                } else {
                    return false;
                }
            } else { //You can't do anything else from console.
                logger.log(Level.WARNING, "[" + this.getDescription().getPrefix() + "] TPA Commands only available to players.");
                return true;
            }
        }
    }

    /**
     * Sends help prompt to player.
     * @param p
     */
    private void sendHelp(Player p) {
        String pre = ChatColor.YELLOW + ">>> " + ChatColor.WHITE;
        p.sendMessage(pre + "AdvancedTPA ver " + this.getDescription().getVersion() + " made by RPG");
        p.sendMessage(pre + "COMMANDS:");
        p.sendMessage(pre + "/tpa <username>: Sends a tp request to the specified person!");
        p.sendMessage(pre + "/tpaccept <username>: Accept an incoming TPA Request from the selected user");
        p.sendMessage(pre + "/tpblacklist <username>: Blacklists the specified player");
        p.sendMessage(pre + "/tpa ban <username>: Bans the selected user from making TP Requests");
        p.sendMessage(pre + "/tpa unban <username>: Unbans the selected user allowing them to make TP Requests again");
    }

    public void registerExistingPlayers() {
        for (int i = 0; i < Bukkit.getOnlinePlayers().length; i++) {
            new TPAPlayer(Bukkit.getOnlinePlayers()[i].getUniqueId());
        }
    }

    public boolean banPlayer(String playerName) {
        Player targ = Bukkit.getPlayer(playerName);
        if (targ != null) {
            TPAPlayer tptarg = TPAPlayer.getRegisteredPlayer(targ.getUniqueId());
            if (!banList.containsKey(tptarg.getIdentifier())) {
                banList.put(tptarg.getIdentifier(), tptarg);
                targ.sendMessage(ChatPrefix + "You have been banned from TPA Requests! Contact an admin if this is a mistake!");
                return true;
            }
        }
        return false;
    }

    public boolean unbanPlayer(String playerName) {
        Player targ = Bukkit.getPlayer(playerName);
        if (targ != null) {
            TPAPlayer tptarg = TPAPlayer.getRegisteredPlayer(targ.getUniqueId());
            if (banList.containsKey(tptarg.getIdentifier())) {
                banList.remove(tptarg.getIdentifier());
                targ.sendMessage(ChatPrefix + "You have been unbanned and can once again make TPA Requests! Don't abuse this system!");
                return true;
            }
        }
        return false;
    }
}
