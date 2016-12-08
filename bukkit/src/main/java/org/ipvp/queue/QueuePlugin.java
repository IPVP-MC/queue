package org.ipvp.queue;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.queue.listener.BungeeListener;
import org.ipvp.queue.listener.PlayerListener;
import org.ipvp.queue.listener.SignListener;
import org.ipvp.queue.sign.InfoSign;
import org.ipvp.queue.sign.JoinSign;
import org.ipvp.queue.sign.QueueSign;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class QueuePlugin extends JavaPlugin {

    private Map<Location, QueueSign> signs = new HashMap<>();
    private Map<String, Priority> priorities = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadPriorities();
        getServer().getMessenger().registerIncomingPluginChannel(this, "Queue", new BungeeListener(this));
        getServer().getMessenger().registerOutgoingPluginChannel(this, "Queue");
        getServer().getPluginManager().registerEvents(new SignListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        try {
            loadSigns();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "failed to load signs.json", e);
        }
    }

    // Loads all signs from signs.json
    private void loadSigns() throws IOException {
        File file = new File(getDataFolder(), "signs.json");
        if (!file.exists()) {
            return;
        }
        FileReader reader = new FileReader(file);
        JSONParser parser = new JSONParser();
        JSONObject object;
        try {
            object = (JSONObject) parser.parse(reader);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        JSONArray signs = (JSONArray) object.get("signs");
        for (Object o : signs) {
            JSONObject sign = (JSONObject) o;

            Location location = deserializeLocation((JSONObject) sign.get("location"));
            QueueSign.Type type = QueueSign.Type.valueOf((String) sign.get("type"));
            String server = (String) sign.get("server");

            Block block = location.getBlock();
            if (!(block.getState() instanceof Sign)) {
                continue;
            }

            Sign handle = (Sign) block.getState();
            switch (type) {
                case JOIN:
                    this.signs.put(location, new JoinSign(handle, server));
                    break;
                case INFO:
                    this.signs.put(location, new InfoSign(handle, server));
                    break;
            }
        }
    }

    // Deserializes a json object into a location
    private Location deserializeLocation(JSONObject object) {
        String world = (String) object.get("world");
        World w = getServer().getWorld(world);
        int x = (int) object.get("x");
        int y = (int) object.get("y");
        int z = (int) object.get("z");
        return new Location(w, x, y, z);
    }

    @Override
    public void onDisable() {
        try {
            writeSigns();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to output to signs.json", e);
        }
    }

    // A helper method that writes signs to disc
    private void writeSigns() throws IOException {
        File file = new File(getDataFolder(), "signs.json");
        if (!file.exists()) {
            file.createNewFile();
        }

        JSONObject root = new JSONObject();
        JSONArray signs = new JSONArray();
        for (QueueSign sign : this.signs.values()) {
            JSONObject serialized = new JSONObject();
            serialized.put("location", serializeLocation(sign.getHandle().getLocation()));
            serialized.put("type", sign.getType().name());
            serialized.put("server", sign.getServer());
            signs.add(serialized);
        }
        root.put("signs", signs);
        FileWriter writer = new FileWriter(file);
        writer.write(root.toJSONString());
        writer.flush();
        writer.close();
    }

    private JSONObject serializeLocation(Location location) {
        JSONObject ret = new JSONObject();
        ret.put("world", location.getWorld().getName());
        ret.put("x", location.getBlockX());
        ret.put("y", location.getBlockY());
        ret.put("z", location.getBlockZ());
        return ret;
    }


    // A helper method to load rank priorities/weights
    private void loadPriorities() {
        ConfigurationSection priorities = getConfig().getConfigurationSection("priorities");
        for (String title : priorities.getKeys(false)) {
            String permission = priorities.getString(title + ".permission");
            int weight = priorities.getInt(title + ".weight");
            this.priorities.put(title, new Priority(title, permission, weight));
        }
    }

    /**
     * Returns a set of signs related to a specific server
     *
     * @param server Server to target
     * @return Signs for the server
     */
    public Set<QueueSign> getSigns(String server) {
        return signs.values().stream().filter(sign -> sign.getServer().equalsIgnoreCase(server)).collect(Collectors.toSet());
    }

    /**
     * Returns the sign at a specific location
     *
     * @param location Location to check
     * @return Sign at the location, or null
     */
    public QueueSign getSign(Location location) {
        return signs.get(location);
    }

    /**
     * Registers a {@link QueueSign} for usage by the plugin
     *
     * @param sign Queue sign to register
     */
    public void registerSign(QueueSign sign) {
        signs.put(sign.getHandle().getLocation(), sign);
    }

    /**
     * Returns the priority weight of a player
     *
     * @param player Player to check
     * @return Priority for player
     */
    public Priority getPriority(Player player) {
        return priorities.values().stream().filter(r -> player.hasPermission(r.getPermission()))
                .sorted((r1, r2) -> Integer.compare(r2.getWeight(), r1.getWeight()))
                .findFirst().orElse(Priority.DEFAULT_PRIORITY);
    }

    /**
     * Returns the priority for a rank
     *
     * @param rank Rank to check
     * @return Priority for the rank
     */
    public Priority getPriority(String rank) {
        return priorities.get(rank);
    }
}
