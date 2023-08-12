package org.example.materials;

import org.example.enums.TrashType;

public class PaperTrash extends Trash {
    public PaperTrash(String name, double totalWeight) {
        super(name, totalWeight);
    }

    @Override
    public boolean isRecyclable() {
        return true;
    }

    @Override
    public TrashType getType() {
        return TrashType.PAPER;
    }
}