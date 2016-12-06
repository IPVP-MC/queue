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

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
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
