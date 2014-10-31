package com.roastedpotatogaming.AdvancedTPA;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.ChatPaginator;

import java.util.ArrayList;
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
    private final String pref = ChatColor.YELLOW + "[FAG] " + ChatColor.WHITE;

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
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("ban")) {
                        Player targ = Bukkit.getPlayer(args[1]);
                        if (targ != null) {
                            TPAPlayer tptarg = TPAPlayer.getRegisteredPlayer(targ.getUniqueId());
                            if (banList.containsKey(tptarg.getIdentifier())) {
                                p.sendMessage(this.pref + "Player is already banned");
                                return true;
                            } else {
                                banList.put(tptarg.getIdentifier(), tptarg);
                                p.sendMessage(this.pref + "Banned player " + targ.getName());
                                return true;
                            }
                        }
                        return false;
                    } else if (args[0].equalsIgnoreCase("unban")) {
                        Player targ = Bukkit.getPlayer(args[1]);
                        if (targ != null) {
                            TPAPlayer tptarg = TPAPlayer.getRegisteredPlayer(targ.getUniqueId());
                            if (banList.containsKey(tptarg.getIdentifier())) {
                                banList.remove(tptarg.getIdentifier());
                                p.sendMessage(this.pref + "Unbanned " + targ.getName());
                            } else {
                                p.sendMessage(this.pref + "Player is not banned from TPA.");
                                return true;
                            }
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else if (cmd.getName().equalsIgnoreCase("tpaccept")) {
                return false;
            } else if (cmd.getName().equalsIgnoreCase("tpblacklist")) {
                return false;
            } else {
                return false;
            }
            return true;
        } else {
            if (cmd.getName().equalsIgnoreCase("tpa") && args[0].equalsIgnoreCase("ban")) {
                return false;
            } else {
                logger.log(Level.WARNING, "TPA Commands only available to players.");
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
    }

    public void registerExistingPlayers() {
        for (int i = 0; i < Bukkit.getOnlinePlayers().length; i++) {
            TPAPlayer.RegisterTPAPlayer(new TPAPlayer(Bukkit.getOnlinePlayers()[i].getUniqueId()));
        }
    }
}
