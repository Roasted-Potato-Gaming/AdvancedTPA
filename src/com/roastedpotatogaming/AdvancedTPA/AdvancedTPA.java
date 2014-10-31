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
    public static final HashMap<UUID, TPAPlayer> banList = new HashMap<UUID, TPAPlayer>();
    public static final String ChatPrefix = ChatColor.YELLOW + "[ATPA] " + ChatColor.WHITE;

    @Override
    public void onEnable() {
        new AdvancedTPAListener(this);
    } //on enable bc github

    @Override
    public void onDisable() {
        TPAPlayer.getRegisteredPlayers().clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("tpa")) {
                if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
                    sendHelp(p);
                    return true;
                } else if (args.length == 1) {
                    TPAPlayer s = new TPAPlayer(p.getUniqueId());
                    TPAPlayer t;
                    if (Bukkit.getPlayer(args[0]) != null) {
                        t = new TPAPlayer(Bukkit.getPlayer(args[0]).getUniqueId());
                    } else {
                        p.sendMessage(ChatPrefix + "Player not found; request not sent.");
                        return true;
                    }
                    t.addInboundRequest(s.getIdentifier());
                    s.addOutboundRequest(t.getIdentifier());
                    return true;
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("ban")) { //Banning players (/tpa ban <player>)
                        if (!banPlayer(args[1])) {
                            p.sendMessage(this.ChatPrefix + "Player is already banned or does not exist.");
                        } else {
                            p.sendMessage(this.ChatPrefix + "Banned player " + args[1]);
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("unban")) { //Unbanning players (/tpa unban <player>)
                        if (!unbanPlayer(args[1])) {
                            p.sendMessage(this.ChatPrefix + "Player \"" + args[1] + "\" is not banned from TPA or does not exist.");
                        } else {
                            p.sendMessage(this.ChatPrefix + "Unbanned " + args[1] + ". They can now make TPA requests again.");
                        }
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else if (cmd.getName().equalsIgnoreCase("tpaccept")) {
                TPAPlayer s = new TPAPlayer(p.getUniqueId());
                TPAPlayer t;
                if (args.length == 1) {
                    if (Bukkit.getPlayer(args[0]) != null) {

                    } else {
                        p.sendMessage(ChatPrefix + "Player not found; request can't be accepted!");
                    }
                } else {
                    p.sendMessage(ChatPrefix + "Please specify the target player!");
                }
                return true;
            } else if (cmd.getName().equalsIgnoreCase("tpblacklist")) {
                return false;
            }
            return false;
        } else {
            if (cmd.getName().equalsIgnoreCase("tpa") && args.length == 2) {
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
            } else {
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
