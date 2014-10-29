package com.roastedpotatogaming.AdvancedTPA;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by azdaspaz818 (RPG OWNER) on 28/10/2014.
 */
public class AdvancedTPA extends JavaPlugin {
    public static final Logger logger = Logger.getLogger("Minecraft");
    protected static AdvancedTPA plugin;
    public static final ArrayList<TPAPlayer> banList = new ArrayList<TPAPlayer>();

    @Override
    public void onEnable() {
        new AdvancedTPAListener(this);
    }

    @Override
    public void onDisable() {
        TPAPlayer.getRegisteredPlayers().clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("tpa")) {
                if(args.length == 0) {
                    sendHelp(p);
                }
            } else if (cmd.getName().equalsIgnoreCase("tpaccept"))
            return true;
        } else {
            if (cmd.getName().equalsIgnoreCase("tpa") && args[0].equalsIgnoreCase("ban")) {

            } else {
                logger.log(Level.WARNING, "TPA Commands only available to players.");
            }
        }
        return false;
    }

    private void sendHelp(Player p) {

    }

    public void registerExistingPlayers() {
        for (int i = 0; i < Bukkit.getOnlinePlayers().length; i++) {
            TPAPlayer.RegisterTPAPlayer(new TPAPlayer(Bukkit.getOnlinePlayers()[i].getUniqueId()));
        }
    }
}
