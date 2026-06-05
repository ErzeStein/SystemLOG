package de.systemlog;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class LogCommand implements CommandExecutor, TabCompleter {

    private final SystemLOG plugin;

    private static final Map<String, String> LOG_FILES = new LinkedHashMap<>();

    static {
        LOG_FILES.put("join",          "join.yml");
        LOG_FILES.put("leave",         "leave.yml");
        LOG_FILES.put("chat",          "chat.yml");
        LOG_FILES.put("commands",      "usedcommand.yml");
        LOG_FILES.put("blockbreak",    "blockbreak.yml");
        LOG_FILES.put("blockplace",    "blockplace.yml");
        LOG_FILES.put("teleport",      "teleport.yml");
        LOG_FILES.put("kicks",         "playerkicks.yml");
        LOG_FILES.put("deaths",        "playerdeaths.yml");
        LOG_FILES.put("mobkills",      "mobkills.yml");
        LOG_FILES.put("itemdrop",      "itempickup.yml");
        LOG_FILES.put("itempickup",    "itemspickup.yml");
        LOG_FILES.put("crafting",      "crafting.yml");
        LOG_FILES.put("enchanting",    "enchanting.yml");
        LOG_FILES.put("anvil",         "anvil.yml");
        LOG_FILES.put("respawn",       "respawn.yml");
        LOG_FILES.put("bed",           "bed.yml");
        LOG_FILES.put("signs",         "signchange.yml");
        LOG_FILES.put("level",         "level.yml");
        LOG_FILES.put("buckets",       "fillbucket.yml");
        LOG_FILES.put("inventories",   "openinventorys.yml");
    }

    public LogCommand(SystemLOG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("systemlog.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length < 2 || !args[0].equalsIgnoreCase("log")) {
            sender.sendMessage("§eUsage: §f/slog log <player/uuid> [type]");
            sender.sendMessage("§eTypes: §f" + String.join(", ", LOG_FILES.keySet()));
            return true;
        }

        String input = args[1];
        String filterType = args.length >= 3 ? args[2].toLowerCase() : null;

        String targetUUID = null;
        String targetName = null;

        try {
            UUID uuid = UUID.fromString(input);
            targetUUID = uuid.toString();
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            targetName = op.getName();
        } catch (IllegalArgumentException ignored) {
            targetName = input;
            Player online = Bukkit.getPlayerExact(input);
            if (online != null) {
                targetUUID = online.getUniqueId().toString();
            } else {
                for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
                    if (op.getName() != null && op.getName().equalsIgnoreCase(input)) {
                        targetUUID = op.getUniqueId().toString();
                        targetName = op.getName();
                        break;
                    }
                }
            }
        }

        if (targetUUID == null && targetName == null) {
            sender.sendMessage("§cCould not find player: §f" + input);
            return true;
        }

        final String finalUUID = targetUUID;
        final String finalName = targetName;
        final String finalType = filterType;

        sender.sendMessage("§7Searching logs for §f" + (finalName != null ? finalName : finalUUID) + "§7...");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            File logsFolder = new File(plugin.getDataFolder(), "Logs");
            Yaml yaml = new Yaml();

            Map<String, List<Map<String, String>>> results = new LinkedHashMap<>();

            Map<String, String> filesToSearch = new LinkedHashMap<>();
            if (finalType != null && LOG_FILES.containsKey(finalType)) {
                filesToSearch.put(finalType, LOG_FILES.get(finalType));
            } else {
                filesToSearch.putAll(LOG_FILES);
            }

            int totalFound = 0;

            for (Map.Entry<String, String> entry : filesToSearch.entrySet()) {
                File logFile = new File(logsFolder, entry.getValue());
                if (!logFile.exists()) continue;

                List<Map<String, String>> matches = new ArrayList<>();

                try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                    StringBuilder block = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty() && block.length() > 0) {
                            String blockStr = block.toString();
                            try {
                                Object parsed = yaml.load(blockStr);
                                if (parsed instanceof Map) {
                                    @SuppressWarnings("unchecked")
                                    Map<String, String> map = (Map<String, String>) parsed;
                                    String entryUUID = map.getOrDefault("UUID", map.getOrDefault("PlayerUUID", ""));
                                    String entryName = map.getOrDefault("PlayerName", "");

                                    boolean matchUUID = finalUUID != null && entryUUID.equalsIgnoreCase(finalUUID);
                                    boolean matchName = finalName != null && entryName.equalsIgnoreCase(finalName);

                                    if (matchUUID || matchName) {
                                        matches.add(map);
                                    }
                                }
                            } catch (Exception ignored) {}
                            block.setLength(0);
                        } else {
                            block.append(line).append("\n");
                        }
                    }
                } catch (IOException e) {
                    plugin.getLogger().warning("Could not read " + entry.getValue() + ": " + e.getMessage());
                }

                if (!matches.isEmpty()) {
                    results.put(entry.getKey(), matches);
                    totalFound += matches.size();
                }
            }

            final int total = totalFound;
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (results.isEmpty()) {
                    sender.sendMessage("§cNo log entries found for §f" + (finalName != null ? finalName : finalUUID));
                    return;
                }

                sender.sendMessage("§8§m                                        ");
                sender.sendMessage("§a§lSystemLOG §7— Results for §f" + (finalName != null ? finalName : finalUUID));
                if (finalUUID != null) sender.sendMessage("§7UUID: §f" + finalUUID);
                sender.sendMessage("§7Total entries found: §f" + total);
                sender.sendMessage("§8§m                                        ");

                for (Map.Entry<String, List<Map<String, String>>> result : results.entrySet()) {
                    sender.sendMessage("§e§l" + result.getKey().toUpperCase() + " §7(" + result.getValue().size() + " entries)");

                    List<Map<String, String>> entries = result.getValue();
                    int startIdx = Math.max(0, entries.size() - 5);
                    for (int i = startIdx; i < entries.size(); i++) {
                        Map<String, String> e = entries.get(i);
                        String timestamp = e.getOrDefault("Timestamp", "?");
                        String world = e.getOrDefault("World", e.getOrDefault("PlayerWorld", "?"));
                        String coords = e.getOrDefault("Coords", e.getOrDefault("PlayerCoords", "?"));

                        StringBuilder details = new StringBuilder();
                        for (Map.Entry<String, String> field : e.entrySet()) {
                            String key = field.getKey();
                            if (key.equals("PlayerName") || key.equals("UUID") || key.equals("PlayerUUID")
                                    || key.equals("Timestamp") || key.equals("World") || key.equals("PlayerWorld")
                                    || key.equals("Coords") || key.equals("PlayerCoords")) continue;
                            details.append("§7").append(key).append(": §f").append(field.getValue()).append(" ");
                        }

                        sender.sendMessage("  §8[" + timestamp + "] §7" + world + " §8(" + coords + ")");
                        if (details.length() > 0) {
                            sender.sendMessage("  " + details.toString().trim());
                        }
                    }

                    if (entries.size() > 5) {
                        sender.sendMessage("  §8... and " + (entries.size() - 5) + " more entries.");
                    }

                    sender.sendMessage("");
                }
                sender.sendMessage("§8§m                                        ");
            });
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("systemlog.admin")) return Collections.emptyList();

        if (args.length == 1) {
            return Collections.singletonList("log");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("log")) {
            List<String> suggestions = new ArrayList<>();
            String partial = args[1].toLowerCase();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(partial)) {
                    suggestions.add(p.getName());
                }
            }
            return suggestions;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("log")) {
            List<String> suggestions = new ArrayList<>();
            String partial = args[2].toLowerCase();
            for (String type : LOG_FILES.keySet()) {
                if (type.startsWith(partial)) suggestions.add(type);
            }
            return suggestions;
        }

        return Collections.emptyList();
    }
}
