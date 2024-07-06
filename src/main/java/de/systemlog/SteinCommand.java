package de.systemlog;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;

public class SteinCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        sender.sendMessage("§a§lDeveloped by Stein & Mosuree & Namensauswahl");
        sender.sendMessage("§a§lhttps://www.spigotmc.org/resources/systemlog.113233/");
        sender.sendMessage("§a§lDiscord Support Link");
        sender.sendMessage("§e§lhttps://discord.gg/B7GwzGGFaP");
        return true;
    }
}
