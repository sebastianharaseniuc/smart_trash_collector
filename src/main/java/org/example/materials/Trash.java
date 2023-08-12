package org.example.materials;

import org.example.enums.TrashType;

public abstract class Trash {
    private String name;
    private double weight;
    public Trash(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }
    public String getName() {
        return name;
    }
    public double getWeight() {
        return weight;
    }
    public abstract boolean isRecyclable();
    public abstract TrashType getType();
}
