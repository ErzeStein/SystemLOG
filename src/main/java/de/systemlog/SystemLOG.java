package de.systemlog;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class SystemLOG extends JavaPlugin implements Listener {
    private File logFile;
    private File enableFolder;
    private File blockbreak;
    private File blockplace;
    private File playerteleport;

    private File chat;
    private File kick;
    private File command;
    private File sign;
    private File kill;
    private File level;
    private File fillbucket;
    private File openinventory;
    private File mobkill;
    private File pickup;
    private File leave;

    public SystemLOG() {
    }


    //bucket einbauen
    public void onEnable() {
        this.getLogger().info("LOG is loading");
        this.enableFolder = new File(this.getDataFolder(), "Logs");
        if (!this.enableFolder.exists()) {
            this.enableFolder.mkdirs();
        }

        this.logFile = new File(this.getDataFolder(), "join.yml");
        if (!this.logFile.exists()) {
            this.saveResource("join.yml", false);
        }
        this.leave = new File(this.getDataFolder(), "leave.yml");
        if (!this.leave.exists()) {
            this.saveResource("leave.yml", false);
        }
        this.getLogger().info("Loading Commands");
        this.getCommand("SystemLOG").setExecutor(new SteinCommand());
        this.getLogger().info("Loading LOG");
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getLogger().info("LOG is Ready");

        this.blockbreak = new File(enableFolder, "blockbreak.yml");
        if(!this.blockbreak.exists()){
            this.saveDefaultConfig();
        }
        this.blockplace = new File(enableFolder, "blockplace.yml");
        if(!this.blockplace.exists()){
            this.saveDefaultConfig();
        }
        this.playerteleport = new File(enableFolder, "teleport.yml");
        if(!this.playerteleport.exists()){
            this.saveDefaultConfig();
        }
        this.chat = new File(enableFolder, "chat.yml");
        if(!this.chat.exists()){
            this.saveDefaultConfig();
        }

        this.command = new File(enableFolder, "usedcommand.yml");
        if(!this.command.exists()){
            this.saveDefaultConfig();
        }

        this.kick = new File(enableFolder, "playerkicks.yml");
        if(!this.kick.exists()){
            this.saveDefaultConfig();
        }
        this.sign = new File(enableFolder, "signchange.yml");
        if(!this.sign.exists()){
            this.saveDefaultConfig();
        }

        this.kill = new File(enableFolder, "playerdeaths.yml");
        if(!this.kill.exists()){
            this.saveDefaultConfig();
        }

        this.level = new File(enableFolder, "level.yml");
        if(!this.level.exists()){
            this.saveDefaultConfig();
        }

        this.fillbucket = new File(enableFolder, "fillbucket.yml");
        if(!this.fillbucket.exists()){
            this.saveDefaultConfig();
        }

        this.openinventory = new File(enableFolder, "openinventorys.yml");
        if(!this.openinventory.exists()){
            this.saveDefaultConfig();
        }
        this.mobkill = new File(enableFolder, "mobkills.yml");
        if(!this.mobkill.exists()){
            this.saveDefaultConfig();
        }

        this.pickup = new File(enableFolder, "itempickup.yml");
        if(!this.pickup.exists()){
            this.saveDefaultConfig();
        }
    }

    public void onDisable() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.logPlayerJoin(player);
    }
    @EventHandler
    public void OnPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.logPlayerJoin2(player);
    }






    private void logPlayerJoin2(Player player) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap();
            String X = String.valueOf(player.getLocation().getX());
            String Y = String.valueOf(player.getLocation().getY());
            String Z = String.valueOf(player.getLocation().getZ());
            String full = X + Y + Z;
            logEntry.put("Codis", full);
            logEntry.put("World", player.getWorld().getName());

            logEntry.put("UUID", player.getUniqueId().toString());
            logEntry.put("IP", player.getAddress().getAddress().getHostAddress());

            logEntry.put("PlayerName", player.getName());
            logEntry.put("Timestamp", timestamp);
            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(this.leave, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException var10) {
            IOException e = var10;
            e.printStackTrace();
        }

    }





    private void logPlayerJoin(Player player) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap();
            String X = String.valueOf(player.getLocation().getX());
            String Y = String.valueOf(player.getLocation().getY());
            String Z = String.valueOf(player.getLocation().getZ());
            String full = X + Y + Z;
            logEntry.put("Codis", full);
            logEntry.put("World", player.getWorld().getName());

            logEntry.put("UUID", player.getUniqueId().toString());
            logEntry.put("IP", player.getAddress().getAddress().getHostAddress());

            logEntry.put("PlayerName", player.getName());
            logEntry.put("Timestamp", timestamp);
            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(this.logFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException var10) {
            IOException e = var10;
            e.printStackTrace();
        }

    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            String blockbreakX = String.valueOf(event.getBlock().getX());
            String blockbreakY = String.valueOf(event.getBlock().getY());
            String blockbreakZ = String.valueOf(event.getBlock().getZ());
            String blockbreakfullcod = blockbreakX + blockbreakY + blockbreakZ;
            Block blockbreak = event.getBlock();
            Material blockbreakmaterial = blockbreak.getType();



            logEntry.put("BlockBreak", blockbreakfullcod);
            logEntry.put("World", event.getPlayer().getWorld().getName());

            logEntry.put("BlockBreakMaterial", String.valueOf(blockbreakmaterial));

            logEntry.put("UUID",event.getPlayer().getUniqueId().toString());

            logEntry.put("PlayerName", event.getPlayer().getName());
            logEntry.put("Timestamp", timestamp);
            Yaml yaml = new Yaml();

            FileWriter writer = new FileWriter(this.blockbreak, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            String blockplaceX = String.valueOf(event.getBlock().getX());
            String blockplaceY = String.valueOf(event.getBlock().getY());
            String blockplaceZ = String.valueOf(event.getBlock().getZ());
            String blockbreakfullcod = blockplaceX + blockplaceY + blockplaceZ;
            Block blockbreak = event.getBlock();
            Material blockbreakmaterial = blockbreak.getType();

            logEntry.put("BlockPlace", blockbreakfullcod);
            logEntry.put("World", event.getPlayer().getWorld().getName());

            logEntry.put("BlockPlaceMaterial", String.valueOf(blockbreakmaterial));

            logEntry.put("UUID",event.getPlayer().getUniqueId().toString());

            logEntry.put("PlayerName", event.getPlayer().getName());
            logEntry.put("Timestamp", timestamp);
            Yaml yaml = new Yaml();

            FileWriter writer = new FileWriter(this.blockplace, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @EventHandler
    public void onKick(PlayerKickEvent event){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            String kickreaon = event.getReason();
            String X = String.valueOf(event.getPlayer().getLocation().getX());
            String Y = String.valueOf(event.getPlayer().getLocation().getY());
            String Z = String.valueOf(event.getPlayer().getLocation().getZ());
            String full = X + Y + Z;

            logEntry.put("KickReason", kickreaon);
            logEntry.put("Codis", full);
            logEntry.put("World", event.getPlayer().getWorld().getName());


            logEntry.put("UUID",event.getPlayer().getUniqueId().toString());

            logEntry.put("PlayerName", event.getPlayer().getName());
            logEntry.put("Timestamp", timestamp);
            Yaml yaml = new Yaml();

            FileWriter writer = new FileWriter(this.kick, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @EventHandler
    public void onCommandUse(PlayerCommandPreprocessEvent event){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            String command = event.getMessage();
            String X = String.valueOf(event.getPlayer().getLocation().getX());
            String Y = String.valueOf(event.getPlayer().getLocation().getY());
            String Z = String.valueOf(event.getPlayer().getLocation().getZ());
            String full = X + Y + Z;

            logEntry.put("Command", command);
            logEntry.put("Codis", full);
            logEntry.put("World", event.getPlayer().getWorld().getName());

            logEntry.put("UUID",event.getPlayer().getUniqueId().toString());

            logEntry.put("PlayerName", event.getPlayer().getName());
            logEntry.put("Timestamp", timestamp);
            Yaml yaml = new Yaml();

            FileWriter writer = new FileWriter(this.command, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            Player player = event.getEntity();
            Player killer = player.getKiller();
            String Xplayer = String.valueOf(player.getLocation().getX());
            String Yplayer = String.valueOf(player.getLocation().getY());
            String Zplayer = String.valueOf(player.getLocation().getZ());
            String fullplayer = Xplayer + Yplayer + Zplayer;

            String X = String.valueOf(killer.getLocation().getX());
            String Y = String.valueOf(killer.getLocation().getY());
            String Z = String.valueOf(killer.getLocation().getZ());
            String fullkiller = X + Y + Z;


            logEntry.put("KillerCodis", fullkiller);
            logEntry.put("KillerWorld", killer.getWorld().getName());

            logEntry.put("KillerUUID",killer.getUniqueId().toString());

            logEntry.put("KillerName", killer.getName());
            logEntry.put("PlayerCodis", fullplayer);
            logEntry.put("PlayerWorld", player.getWorld().getName());

            logEntry.put("PlayerUUID",event.getEntity().getUniqueId().toString());

            logEntry.put("PlayerName", event.getEntity().getName());
            logEntry.put("Timestamp", timestamp);
            Yaml yaml = new Yaml();

            FileWriter writer = new FileWriter(this.kill, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            String X = String.valueOf(event.getBlock().getX());
            String Y = String.valueOf(event.getBlock().getY());
            String Z = String.valueOf(event.getBlock().getZ());
            String full = X + Y + Z;
            String bucket = String.valueOf(event.getBucket());

            logEntry.put("Codis", full);
            logEntry.put("Bucket", bucket);
            logEntry.put("World", event.getPlayer().getWorld().getName());

            logEntry.put("UUID",event.getPlayer().getUniqueId().toString());

            logEntry.put("PlayerName", event.getPlayer().getName());
            logEntry.put("Timestamp", timestamp);
            Yaml yaml = new Yaml();

            FileWriter writer = new FileWriter(this.sign, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @EventHandler
    public void onPlayerSign(SignChangeEvent event){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            String signchangeX = String.valueOf(event.getBlock().getX());
            String signchangeY = String.valueOf(event.getBlock().getY());
            String signchangeZ = String.valueOf(event.getBlock().getZ());
            String signchangefull = signchangeX + signchangeY + signchangeZ;


            logEntry.put("SignChangeCodis", signchangefull);
            logEntry.put("World", event.getPlayer().getWorld().getName());

            logEntry.put("UUID",event.getPlayer().getUniqueId().toString());

            logEntry.put("PlayerName", event.getPlayer().getName());
            logEntry.put("Timestamp", timestamp);
            Yaml yaml = new Yaml();

            FileWriter writer = new FileWriter(this.sign, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @EventHandler
    public void onLevel(PlayerLevelChangeEvent event){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            double leveldavor = event.getOldLevel();
            double leveldanach = event.getNewLevel();

            String X = String.valueOf(event.getPlayer().getLocation().getX());
            String Y = String.valueOf(event.getPlayer().getLocation().getY());
            String Z = String.valueOf(event.getPlayer().getLocation().getZ());
            String full = X + Y + Z;


            logEntry.put("OldLevel", String.valueOf(leveldavor));
            logEntry.put("NewLevel", String.valueOf(leveldanach));

            logEntry.put("Codis", full);
            logEntry.put("World", event.getPlayer().getWorld().getName());

            logEntry.put("UUID",event.getPlayer().getUniqueId().toString());

            logEntry.put("PlayerName", event.getPlayer().getName());
            logEntry.put("Timestamp", timestamp);
            Yaml yaml = new Yaml();

            FileWriter writer = new FileWriter(this.level, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            String clickblockX = String.valueOf(event.getBlockClicked().getX());
            String clickblockY = String.valueOf(event.getBlockClicked().getY());
            String clickblockZ = String.valueOf(event.getBlockClicked().getZ());
            String clickblockfull = clickblockX + clickblockY + clickblockX;
            Block block = event.getBlock();
            Material material = event.getBlock().getType();

            Block block2 = event.getBlockClicked();
            Material material2 = event.getBlockClicked().getType();
            String X = String.valueOf(event.getPlayer().getLocation().getX());
            String Y = String.valueOf(event.getPlayer().getLocation().getY());
            String Z = String.valueOf(event.getPlayer().getLocation().getZ());
            String full = X + Y + Z;


            logEntry.put("ClickedBlockCodis", clickblockfull);
            logEntry.put("ClickedBlockMaterial", String.valueOf(material2));
            logEntry.put("ClickedBlockWorld", event.getBlockClicked().getWorld().getName());

            logEntry.put("Codis", full);
            logEntry.put("World", event.getPlayer().getWorld().getName());

            logEntry.put("Material", String.valueOf(material));

            logEntry.put("UUID",event.getPlayer().getUniqueId().toString());

            logEntry.put("PlayerName", event.getPlayer().getName());
            logEntry.put("Timestamp", timestamp);
            Yaml yaml = new Yaml();

            FileWriter writer = new FileWriter(this.fillbucket, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private static final Set<Material> ALLOWED_MATERIALS = EnumSet.of(
            Material.CHEST,
            Material.HOPPER,
            Material.ANVIL,
            Material.FURNACE,
            Material.BUNDLE,
            Material.ENDER_CHEST,
            Material.SHULKER_BOX,
            Material.MINECART,
            Material.CHEST_MINECART,
            Material.HOPPER_MINECART,
            Material.FURNACE_MINECART
    );



    @EventHandler
    public void omItemPickUp(PlayerDropItemEvent event){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            String name = event.getEventName();
            String X = String.valueOf(event.getPlayer().getLocation().getX());
            String Y = String.valueOf(event.getPlayer().getLocation().getY());
            String Z = String.valueOf(event.getPlayer().getLocation().getZ());
            String full = X + Y + Z;

            Item droppedItem = event.getItemDrop();
            ItemStack itemStack = droppedItem.getItemStack();
            Material itemType = itemStack.getType();

            logEntry.put("ItemDroppedMaterial", String.valueOf(itemType));

            logEntry.put("Codis", full);
            logEntry.put("World", event.getPlayer().getWorld().getName());

            logEntry.put("PlayerUUID",event.getPlayer().getUniqueId().toString());

            logEntry.put("PlayerName", event.getPlayer().getKiller().getName());

            logEntry.put("Timestamp", timestamp);
            Yaml yaml = new Yaml();

            FileWriter writer = new FileWriter(this.pickup, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = e.getClickedBlock();
            if (clickedBlock != null && ALLOWED_MATERIALS.contains(clickedBlock.getType())) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String timestamp = dateFormat.format(new Date());
                    Map<String, String> logEntry = new LinkedHashMap<>();
                    Block block = e.getClickedBlock();
                    Material material = block.getType();
                    String X = String.valueOf(clickedBlock.getLocation().getX());
                    String Y = String.valueOf(clickedBlock.getLocation().getY());
                    String Z = String.valueOf(clickedBlock.getLocation().getZ());
                    String full = X + Y + Z;
                    if (block.getState() instanceof Container) {
                        Container container = (Container) block.getState();
                        logEntry.put("InventoryName", container.getInventory().getType().name());
                    } else {
                        logEntry.put("InventoryName", "N/A");
                    }
                    logEntry.put("Codis", full);

                    logEntry.put("World", clickedBlock.getWorld().getName());
                    logEntry.put("MaterialBlock", String.valueOf(material));

                    logEntry.put("PlayerUUID", e.getPlayer().getName());

                    logEntry.put("PlayerName", e.getPlayer().getName());
                    logEntry.put("Timestamp", timestamp);
                    Yaml yaml = new Yaml();

                    FileWriter writer = new FileWriter(this.openinventory, true);
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
        }

    }
    @EventHandler
    public void MobDeath(EntityDeathEvent event){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            String name = event.getEventName();
            String X = String.valueOf(event.getEntity().getLocation().getX());
            String Y = String.valueOf(event.getEntity().getLocation().getY());
            String Z = String.valueOf(event.getEntity().getLocation().getZ());
            String full = X + Y + Z;
            Mob mob = (Mob) event.getEntity();
            EntityType entityType = mob.getType();

            logEntry.put("EntityType", String.valueOf(entityType));
            logEntry.put("Codis", full);
            logEntry.put("World", event.getEntity().getWorld().getName());
            logEntry.put("KillerWorld", event.getEntity().getKiller().getWorld().getName());

            logEntry.put("KillerUUID",event.getEntity().getKiller().getUniqueId().toString());
            logEntry.put("KillerName", event.getEntity().getKiller().getName());
            logEntry.put("Timestamp", timestamp);
            Yaml yaml = new Yaml();

            FileWriter writer = new FileWriter(this.mobkill, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            String chatmessage = event.getMessage();
            String X = String.valueOf(event.getPlayer().getLocation().getX());
            String Y = String.valueOf(event.getPlayer().getLocation().getY());
            String Z = String.valueOf(event.getPlayer().getLocation().getZ());
            String full = X + Y + Z;


            logEntry.put("Message", chatmessage);
            logEntry.put("Codis", full);
            logEntry.put("World", event.getPlayer().getWorld().getName());

            logEntry.put("UUID",event.getPlayer().getUniqueId().toString());
            logEntry.put("PlayerName", event.getPlayer().getName());
            logEntry.put("Timestamp", timestamp);
            Yaml yaml = new Yaml();

            FileWriter writer = new FileWriter(this.chat, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            Map<String, String> logEntry = new LinkedHashMap<>();
            String playerteleporttoX = String.valueOf(event.getTo().getBlockX());
            String playerteleporttoY = String.valueOf(event.getTo().getBlockY());
            String playerteleporttoZ = String.valueOf(event.getTo().getBlockZ());
            Block playerteleporttoblock = event.getTo().getBlock();
            Material playerteleporttoMaterial = playerteleporttoblock.getType();
            String playerteleporttofull = playerteleporttoX + playerteleporttoY + playerteleporttoZ;

            String playerteleportFromX = String.valueOf(event.getFrom().getBlockX());
            String playerteleportFromY = String.valueOf(event.getFrom().getBlockY());
            String playerteleportFromZ = String.valueOf(event.getFrom().getBlockZ());
            Block playerteleportFromblock = event.getTo().getBlock();
            Material playerteleportFromMaterial = playerteleportFromblock.getType();
            String playerteleportFromfull = playerteleportFromX + playerteleportFromY + playerteleportFromZ;


            logEntry.put("Playerteleportto", playerteleporttofull);
            logEntry.put("PlayerteleporttoWorld", event.getTo().getWorld().getName());

            logEntry.put("PlayerteleporttoMaterial", String.valueOf(playerteleporttoMaterial));

            logEntry.put("Playerteleportfrom", playerteleportFromfull);
            logEntry.put("PlayerteleportfromWorld", event.getFrom().getWorld().getName());

            logEntry.put("PlayerteleportfromMaterial", String.valueOf(playerteleportFromMaterial));

            logEntry.put("UUID",event.getPlayer().getUniqueId().toString());

            logEntry.put("PlayerName", event.getPlayer().getName());
            logEntry.put("Timestamp", timestamp);
            Yaml yaml = new Yaml();

            FileWriter writer = new FileWriter(this.playerteleport, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            String logString = yaml.dump(logEntry);
            bufferedWriter.write(logString);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
