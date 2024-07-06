package de.systemlog;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SystemLOG extends JavaPlugin implements Listener {
    private File logFile;
    private File enableFolder;

    @Override
    public void onEnable() {
        // Plugin startup logic

        getLogger().info("LOG is loading");

        enableFolder = new File(getDataFolder(), "comming soon");
        if (!enableFolder.exists()) {
            enableFolder.mkdirs();
        }


        logFile = new File(enableFolder, "log.yml");
        if (!logFile.exists()) {
            saveResource("log.yml", false);
        }

        getLogger().info("Loading Commands");
        getCommand("Stein").setExecutor(new SteinCommand());

        getLogger().info("Loading LOG");
        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("LOG is Ready");

        logFile = new File(getDataFolder(), "log.yml");
        if (!logFile.exists()) {
            saveDefaultConfig();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        logPlayerJoin(player);
    }

    @EventHandler
    public void playerquit(PlayerQuitEvent event){
        Player p = event.getPlayer();
        logPlayerJoin(p);
    }
    @EventHandler
    public void playerjoin(PlayerKickEvent event){
        Player p = event.getPlayer();
        logPlayerJoin(p);
    }
    @EventHandler
    public void onInterect(PlayerAdvancementDoneEvent event){


        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            logEntry.put("Event: ", " PlayerInteract");
            logEntry.put("PlayerName:", event.getPlayer().getDisplayName());
            logEntry.put("PlayerUUIID:", String.valueOf(event.getPlayer().getUniqueId()));
            logEntry.put("Advancement:", event.getAdvancement().toString());

            logEntry.put("Timestamp", timestamp);

            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(logFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    @EventHandler
    public void onInterect(PlayerInteractEvent event){


        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            logEntry.put("Event: ", " PlayerInteract");
            logEntry.put("PlayerName:", event.getPlayer().getDisplayName());
            logEntry.put("PlayerUUIID:", String.valueOf(event.getPlayer().getUniqueId()));

            logEntry.put("Timestamp:", timestamp);

            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(logFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    @EventHandler
    public void onFishing(PlayerFishEvent event){
        String item = String.valueOf(event.getCaught().getType());

        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            logEntry.put("Event: ", "Fishing");
            logEntry.put("PlayerName:", event.getPlayer().getDisplayName());
            logEntry.put("PlayerUUIID:", String.valueOf(event.getPlayer().getUniqueId()));

            logEntry.put("Item:", item);
            logEntry.put("Timestamp:", timestamp);

            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(logFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @EventHandler
    public void onplace(BlockPlaceEvent event){

        String block = String.valueOf(event.getBlock().getType());

        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            logEntry.put("Event: ", "Block Place");
            logEntry.put("PlayerName:", event.getPlayer().getDisplayName());
            logEntry.put("PlayerUUIID:", String.valueOf(event.getPlayer().getUniqueId()));
            logEntry.put("Block:", block);
            logEntry.put("POS:", " X:" + event.getBlock().getX() + " Y:" + event.getBlock().getY() + " Z:" + event.getBlock().getZ());

            logEntry.put("Timestamp:", timestamp);

            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(logFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onbreak(BlockBreakEvent event){

        String Block = String.valueOf(event.getBlock().getType());

        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            logEntry.put("Event: ", "Block Break");
            logEntry.put("PlayerName:", event.getPlayer().getDisplayName());
            logEntry.put("PlayerUUIID:", String.valueOf(event.getPlayer().getUniqueId()));

            logEntry.put("Block:", Block);
            logEntry.put("POS:", " X:" + event.getBlock().getX() + " Y:" + event.getBlock().getY() + " Z:" + event.getBlock().getZ());

            logEntry.put("Timestamp:", timestamp);

            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(logFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onchat(AsyncPlayerChatEvent event){

        String message = event.getMessage();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            logEntry.put("Event: ", "Chat");
            logEntry.put("PlayerName:", event.getPlayer().getDisplayName());
            logEntry.put("PlayerUUIID:", String.valueOf(event.getPlayer().getUniqueId()));
            logEntry.put("Message:", message);
            logEntry.put("Timestamp:", timestamp);

            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(logFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();

        }catch (IOException e) {
        e.printStackTrace();
    }
    }

    private void logPlayerJoin(Player player) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());

            Map<String, String> logEntry = new LinkedHashMap<>();
            logEntry.put("Event: ", "Join");
            logEntry.put("IP:", player.getAddress().getAddress().getHostAddress());
            logEntry.put("UUID:", player.getUniqueId().toString());
            logEntry.put("PlayerName:", player.getName());
            logEntry.put("Timestamp:", timestamp);
            logEntry.put("World:", String.valueOf(player.getWorld()));
            logEntry.put("Location:", String.valueOf(player.getLocation()));

            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(logFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
