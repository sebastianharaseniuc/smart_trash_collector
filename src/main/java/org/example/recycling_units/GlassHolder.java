package org.example.recycling_units;

import org.example.communication_and_database.DatabaseConnection;
import org.example.enums.TrashType;

public class GlassHolder extends Holder {
    public GlassHolder(String location, double maxCapacity) {
        super(location, (int) maxCapacity, TrashType.GLASS);
        super.setHolderId(new DatabaseConnection().getHolderIdBasedOnLocation(super.getLocation(), super.getTrashType()));
        super.setCurrentCapacity(new DatabaseConnection().getHolderCurrentCapacity(this.getHolderId()));
    }
}
