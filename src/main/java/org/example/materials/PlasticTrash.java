package org.example.materials;

import org.example.enums.TrashType;
import org.example.materials.Trash;

public class PlasticTrash extends Trash {
    public PlasticTrash(String name, double totalWeight) {
        super(name, totalWeight);
    }

    @Override
    public boolean isRecyclable() {
        return true;
    }

    @Override
    public TrashType getType() {
        return TrashType.PLASTIC;
    }
}
