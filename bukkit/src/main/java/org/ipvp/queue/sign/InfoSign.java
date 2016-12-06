package org.ipvp.queue.sign;

import org.bukkit.block.Sign;
import org.ipvp.queue.Priority;

import java.util.*;

public class InfoSign extends QueueSign {

    private Map<Priority, Integer> counts = new HashMap<>();

    public InfoSign(Sign handle, String server) {
        super(handle, server);
    }

    public void update(Priority priority, int count) {
        counts.put(priority, count);
        updateSign();
    }

    // Helper method that updates the sign with all ranks
    private void updateSign() {
        SortedSet<Map.Entry<Priority, Integer>> sorted = new TreeSet<>((p1, p2) -> Integer.compare(p2.getValue(), p1.getValue()));
        sorted.addAll(counts.entrySet());
        int index = 0;
        Iterator<Map.Entry<Priority, Integer>> sortedIterator = sorted.iterator();
        while (sortedIterator.hasNext() && index++ < 4) {
            Map.Entry<Priority, Integer> next = sortedIterator.next();
            getHandle().setLine(index, next.getKey().getName() + ": " + next.getValue());
        }
    }
}
