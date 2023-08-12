package org.example.materials;

import org.example.enums.TrashType;

public class ToxicTrash extends Trash{
    public ToxicTrash(String name, double weight, int quantity) {
        super(name, weight);
    }
    @Override
    public boolean isRecyclable(){
        return false;
    }

    @Override
    public TrashType getType() {
        return TrashType.TOXIC;
    }
}
