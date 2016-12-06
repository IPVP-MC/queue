package org.ipvp.queue;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.queue.listener.BungeeListener;
import org.ipvp.queue.listener.PlayerListener;
import org.ipvp.queue.listener.SignListener;
import org.ipvp.queue.sign.QueueSign;

import java.util.*;
import java.util.stream.Collectors;

public class QueuePlugin extends JavaPlugin {

    private Set<QueueSign> signs = new HashSet<>();
    private Map<String, Priority> priorities = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadPriorities();
        getServer().getMessenger().registerIncomingPluginChannel(this, "QueuePlugin", new BungeeListener(this));
        getServer().getMessenger().registerOutgoingPluginChannel(this, "QueuePlugin");
        getServer().getPluginManager().registerEvents(new SignListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
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
        return signs.stream().filter(sign -> sign.getServer().equals("server")).collect(Collectors.toSet());
    }

    /**
     * Registers a {@link QueueSign} for usage by the plugin
     *
     * @param sign Queue sign to register
     */
    public void registerSign(QueueSign sign) {
        signs.add(sign);
        // TODO: Need to save and load signs
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
