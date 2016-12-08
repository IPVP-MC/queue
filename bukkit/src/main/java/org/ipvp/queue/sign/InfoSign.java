package org.ipvp.queue.sign;

import org.bukkit.block.Sign;
import org.ipvp.queue.Priority;

import java.util.*;

public class InfoSign extends QueueSign {

    private Map<String, Integer> counts = new HashMap<>();

    public InfoSign(Sign handle, String server) {
        super(handle, Type.INFO, server);
    }

    /**
     * Updates a priority count waiting in this queue
     *
     * @param priority Priority to modify
     * @param count New count waiting with this priority
     */
    public void updateCount(Priority priority, int count) {
        counts.put(priority.getName(), count);
    }

    /**
     * Returns the players and priority counts waiting in this queue
     *
     * @return Priority counts waiting in this queue
     */
    public Map<String, Integer> getCounts() {
        return Collections.unmodifiableMap(counts);
    }
}
