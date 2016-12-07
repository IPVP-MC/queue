package org.ipvp.queue;

import java.util.Objects;

/**
 * Represents a server rank and the weight they carry in the priority queue
 */
public final class Priority {

    private String name;
    private int weight;

    public Priority(String name, int weight) {
        Objects.requireNonNull(name, "Name cannot be null");
        this.name = name;
        this.weight = weight;
    }

    /**
     * Returns the rank name of this priority
     *
     * @return Rank name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the weight of this priority class
     *
     * @return Weight inside queues
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Returns whether this priority is a bypass priority
     *
     * @return True if this priority bypasses queues
     */
    public boolean isBypass() {
        return weight >= 999;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        return prime * name.hashCode()
                + prime * Integer.hashCode(weight);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Priority)) {
            return false;
        }
        Priority other = (Priority) o;
        return other.name.equals(name) && other.weight == weight;
    }
}
