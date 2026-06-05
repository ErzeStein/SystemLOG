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
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class SystemLOG extends JavaPlugin implements Listener {

    private File logFolder;
    private File joinFile;
    private File leaveFile;
    private File blockbreakFile;
    private File blockplaceFile;
    private File teleportFile;
    private File chatFile;
    private File kickFile;
    private File commandFile;
    private File signFile;
    private File playerdeathFile;
    private File levelFile;
    private File fillbucketFile;
    private File openinventoryFile;
    private File mobkillFile;
    private File itemdropFile;

    private final Map<File, ConcurrentLinkedQueue<Map<String, String>>> writeQueues = new HashMap<>();

    private static final Set<Material> CONTAINER_MATERIALS = EnumSet.of(
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.BARREL,
            Material.HOPPER,
            Material.ANVIL,
            Material.CHIPPED_ANVIL,
            Material.DAMAGED_ANVIL,
            Material.FURNACE,
            Material.BLAST_FURNACE,
            Material.SMOKER,
            Material.ENDER_CHEST,
            Material.SHULKER_BOX,
            Material.CHEST_MINECART,
            Material.HOPPER_MINECART,
            Material.FURNACE_MINECART,
            Material.DROPPER,
            Material.DISPENSER,
            Material.BREWING_STAND,
            Material.CRAFTING_TABLE
    );

    @Override
    public void onEnable() {
        getLogger().info("SystemLOG is starting...");

        logFolder = new File(getDataFolder(), "Logs");
        if (!logFolder.exists()) {
            logFolder.mkdirs();
        }

        joinFile         = initLogFile(getDataFolder(), "join.yml");
        leaveFile        = initLogFile(getDataFolder(), "leave.yml");
        blockbreakFile   = initLogFile(logFolder, "blockbreak.yml");
        blockplaceFile   = initLogFile(logFolder, "blockplace.yml");
        teleportFile     = initLogFile(logFolder, "teleport.yml");
        chatFile         = initLogFile(logFolder, "chat.yml");
        kickFile         = initLogFile(logFolder, "playerkicks.yml");
        commandFile      = initLogFile(logFolder, "usedcommand.yml");
        signFile         = initLogFile(logFolder, "signchange.yml");
        playerdeathFile  = initLogFile(logFolder, "playerdeaths.yml");
        levelFile        = initLogFile(logFolder, "level.yml");
        fillbucketFile   = initLogFile(logFolder, "fillbucket.yml");
        openinventoryFile = initLogFile(logFolder, "openinventorys.yml");
        mobkillFile      = initLogFile(logFolder, "mobkills.yml");
        itemdropFile     = initLogFile(logFolder, "itempickup.yml");

        for (File f : new File[]{joinFile, leaveFile, blockbreakFile, blockplaceFile, teleportFile,
                chatFile, kickFile, commandFile, signFile, playerdeathFile, levelFile,
                fillbucketFile, openinventoryFile, mobkillFile, itemdropFile}) {
            writeQueues.put(f, new ConcurrentLinkedQueue<>());
        }

        startFlushTask();

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("SystemLOG").setExecutor(new SteinCommand());

        getLogger().info("SystemLOG is ready.");
    }

    @Override
    public void onDisable() {
        flushAll();
        getLogger().info("SystemLOG flushed and disabled.");
    }

    private File initLogFile(File folder, String name) {
        File f = new File(folder, name);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                getLogger().warning("Could not create log file: " + name);
            }
        }
        return f;
    }

    private void startFlushTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                flushAll();
            }
        }.runTaskTimerAsynchronously(this, 100L, 100L);
    }

    private void flushAll() {
        DumperOptions opts = new DumperOptions();
        opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(opts);

        for (Map.Entry<File, ConcurrentLinkedQueue<Map<String, String>>> entry : writeQueues.entrySet()) {
            ConcurrentLinkedQueue<Map<String, String>> queue = entry.getValue();
            if (queue.isEmpty()) continue;

            List<Map<String, String>> batch = new ArrayList<>();
            Map<String, String> item;
            while ((item = queue.poll()) != null) {
                batch.add(item);
            }

            if (batch.isEmpty()) continue;

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(entry.getKey(), true))) {
                for (Map<String, String> logEntry : batch) {
                    writer.write(yaml.dump(logEntry));
                    writer.newLine();
                }
            } catch (IOException e) {
                getLogger().warning("Failed to flush log to " + entry.getKey().getName() + ": " + e.getMessage());
            }
        }
    }

    private void queueEntry(File target, Map<String, String> entry) {
        ConcurrentLinkedQueue<Map<String, String>> queue = writeQueues.get(target);
        if (queue != null) {
            queue.add(entry);
        }
    }

    private String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private String coords(double x, double y, double z) {
        return String.format("%.2f / %.2f / %.2f", x, y, z);
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("PlayerName", player.getName());
        entry.put("UUID", player.getUniqueId().toString());
        entry.put("IP", player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : "unknown");
        entry.put("World", player.getWorld().getName());
        entry.put("Coords", coords(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
        entry.put("Timestamp", timestamp());
        queueEntry(joinFile, entry);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("PlayerName", player.getName());
        entry.put("UUID", player.getUniqueId().toString());
        entry.put("IP", player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : "unknown");
        entry.put("World", player.getWorld().getName());
        entry.put("Coords", coords(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
        entry.put("Timestamp", timestamp());
        queueEntry(leaveFile, entry);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("PlayerName", event.getPlayer().getName());
        entry.put("UUID", event.getPlayer().getUniqueId().toString());
        entry.put("Material", block.getType().name());
        entry.put("World", block.getWorld().getName());
        entry.put("Coords", coords(block.getX(), block.getY(), block.getZ()));
        entry.put("Timestamp", timestamp());
        queueEntry(blockbreakFile, entry);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("PlayerName", event.getPlayer().getName());
        entry.put("UUID", event.getPlayer().getUniqueId().toString());
        entry.put("Material", block.getType().name());
        entry.put("World", block.getWorld().getName());
        entry.put("Coords", coords(block.getX(), block.getY(), block.getZ()));
        entry.put("Timestamp", timestamp());
        queueEntry(blockplaceFile, entry);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("PlayerName", player.getName());
        entry.put("UUID", player.getUniqueId().toString());
        entry.put("KickReason", event.getReason());
        entry.put("World", player.getWorld().getName());
        entry.put("Coords", coords(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
        entry.put("Timestamp", timestamp());
        queueEntry(kickFile, entry);
    }

    @EventHandler
    public void onCommandUse(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("PlayerName", player.getName());
        entry.put("UUID", player.getUniqueId().toString());
        entry.put("Command", event.getMessage());
        entry.put("World", player.getWorld().getName());
        entry.put("Coords", coords(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
        entry.put("Timestamp", timestamp());
        queueEntry(commandFile, entry);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("PlayerName", player.getName());
        entry.put("PlayerUUID", player.getUniqueId().toString());
        entry.put("PlayerWorld", player.getWorld().getName());
        entry.put("PlayerCoords", coords(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
        entry.put("DeathMessage", event.getDeathMessage() != null ? event.getDeathMessage() : "unknown");

        if (killer != null) {
            entry.put("KillerName", killer.getName());
            entry.put("KillerUUID", killer.getUniqueId().toString());
            entry.put("KillerWorld", killer.getWorld().getName());
            entry.put("KillerCoords", coords(killer.getLocation().getX(), killer.getLocation().getY(), killer.getLocation().getZ()));
        } else {
            entry.put("KillerName", "none");
            entry.put("KillerUUID", "none");
        }

        entry.put("Timestamp", timestamp());
        queueEntry(playerdeathFile, entry);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Block block = event.getBlock();
        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("PlayerName", event.getPlayer().getName());
        entry.put("UUID", event.getPlayer().getUniqueId().toString());
        entry.put("World", block.getWorld().getName());
        entry.put("Coords", coords(block.getX(), block.getY(), block.getZ()));
        entry.put("Timestamp", timestamp());
        queueEntry(signFile, entry);
    }

    @EventHandler
    public void onLevelChange(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();
        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("PlayerName", player.getName());
        entry.put("UUID", player.getUniqueId().toString());
        entry.put("OldLevel", String.valueOf(event.getOldLevel()));
        entry.put("NewLevel", String.valueOf(event.getNewLevel()));
        entry.put("World", player.getWorld().getName());
        entry.put("Coords", coords(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
        entry.put("Timestamp", timestamp());
        queueEntry(levelFile, entry);
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Block clicked = event.getBlockClicked();
        Player player = event.getPlayer();
        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("PlayerName", player.getName());
        entry.put("UUID", player.getUniqueId().toString());
        entry.put("Bucket", event.getBucket().name());
        entry.put("ClickedBlockMaterial", clicked.getType().name());
        entry.put("ClickedBlockWorld", clicked.getWorld().getName());
        entry.put("ClickedBlockCoords", coords(clicked.getX(), clicked.getY(), clicked.getZ()));
        entry.put("PlayerWorld", player.getWorld().getName());
        entry.put("PlayerCoords", coords(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
        entry.put("Timestamp", timestamp());
        queueEntry(fillbucketFile, entry);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("PlayerName", player.getName());
        entry.put("UUID", player.getUniqueId().toString());
        entry.put("Bucket", event.getBucket().name());
        entry.put("World", player.getWorld().getName());
        entry.put("Coords", coords(block.getX(), block.getY(), block.getZ()));
        entry.put("Timestamp", timestamp());
        queueEntry(fillbucketFile, entry);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block clicked = event.getClickedBlock();
        if (clicked == null || !CONTAINER_MATERIALS.contains(clicked.getType())) return;

        Player player = event.getPlayer();
        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("PlayerName", player.getName());
        entry.put("PlayerUUID", player.getUniqueId().toString());
        entry.put("BlockMaterial", clicked.getType().name());

        if (clicked.getState() instanceof Container) {
            Container container = (Container) clicked.getState();
            entry.put("InventoryType", container.getInventory().getType().name());
        } else {
            entry.put("InventoryType", "N/A");
        }

        entry.put("World", clicked.getWorld().getName());
        entry.put("Coords", coords(clicked.getX(), clicked.getY(), clicked.getZ()));
        entry.put("Timestamp", timestamp());
        queueEntry(openinventoryFile, entry);
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Mob)) return;
        Mob mob = (Mob) event.getEntity();
        Player killer = mob.getKiller();
        if (killer == null) return;

        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("EntityType", mob.getType().name());
        entry.put("EntityWorld", mob.getWorld().getName());
        entry.put("EntityCoords", coords(mob.getLocation().getX(), mob.getLocation().getY(), mob.getLocation().getZ()));
        entry.put("KillerName", killer.getName());
        entry.put("KillerUUID", killer.getUniqueId().toString());
        entry.put("KillerWorld", killer.getWorld().getName());
        entry.put("Timestamp", timestamp());
        queueEntry(mobkillFile, entry);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item dropped = event.getItemDrop();
        ItemStack stack = dropped.getItemStack();

        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("PlayerName", player.getName());
        entry.put("PlayerUUID", player.getUniqueId().toString());
        entry.put("ItemMaterial", stack.getType().name());
        entry.put("ItemAmount", String.valueOf(stack.getAmount()));
        entry.put("World", player.getWorld().getName());
        entry.put("Coords", coords(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
        entry.put("Timestamp", timestamp());
        queueEntry(itemdropFile, entry);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("PlayerName", player.getName());
        entry.put("UUID", player.getUniqueId().toString());
        entry.put("Message", event.getMessage());
        entry.put("World", player.getWorld().getName());
        entry.put("Coords", coords(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
        entry.put("Timestamp", timestamp());
        queueEntry(chatFile, entry);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getFrom().getWorld() == null || event.getTo() == null || event.getTo().getWorld() == null) return;
        Player player = event.getPlayer();

        Map<String, String> entry = new LinkedHashMap<>();
        entry.put("PlayerName", player.getName());
        entry.put("UUID", player.getUniqueId().toString());
        entry.put("FromWorld", event.getFrom().getWorld().getName());
        entry.put("FromCoords", coords(event.getFrom().getX(), event.getFrom().getY(), event.getFrom().getZ()));
        entry.put("ToWorld", event.getTo().getWorld().getName());
        entry.put("ToCoords", coords(event.getTo().getX(), event.getTo().getY(), event.getTo().getZ()));
        entry.put("Cause", event.getCause().name());
        entry.put("Timestamp", timestamp());
        queueEntry(teleportFile, entry);
    }
}
