package org.ipvp.queue;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.queue.listener.PlayerListener;

public class QueuePlugin extends JavaPlugin {

    private Map<String, Priority> priorities = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadPriorities();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "Queue");
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
