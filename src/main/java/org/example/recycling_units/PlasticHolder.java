package org.example.recycling_units;

import org.example.communication_and_database.DatabaseConnection;
import org.example.enums.TrashType;

public class PlasticHolder extends Holder {
    public PlasticHolder(String location, double maxCapacity) {
        super(location, maxCapacity, TrashType.PLASTIC);
        super.setHolderId(new DatabaseConnection().getHolderIdBasedOnLocation(super.getLocation(), super.getTrashType()));
        super.setCurrentCapacity(new DatabaseConnection().getHolderCurrentCapacity(this.getHolderId()));
    }
}
