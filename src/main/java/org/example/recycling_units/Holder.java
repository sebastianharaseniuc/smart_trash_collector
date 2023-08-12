package org.example.recycling_units;

import org.example.communication_and_database.DataLogger;
import org.example.communication_and_database.DatabaseConnection;
import org.example.enums.TrashType;
import org.example.exception.TrashIsFullException;
import org.example.interfaces.TrashCollector;
import org.example.materials.Trash;
import org.example.transportation.Collector;

public abstract class Holder {
    private final double maximumCapacity;
    private double currentCapacity;
    private final TrashCollector trashCollector = new Collector();
    private final TrashType trashType;
    private int holderId;
    private final String location;

    public Holder(String location, double maximumCapacity, TrashType trashType) {
        this.location = location;
        this.maximumCapacity = maximumCapacity;
        this.trashType = trashType;
    }
    public boolean isFull() {
        return currentCapacity >= maximumCapacity * 0.9;
    }
    public void getTrash(Trash trash, String locationName) throws TrashIsFullException {
        if (currentCapacity + trash.getWeight() > maximumCapacity) {
            throw new TrashIsFullException("There is too much trash to handle, please add fewer items");
        }

        currentCapacity += trash.getWeight();

        if (isFull()) {
            System.out.println(getClass().getSimpleName() + " is full. Collector will come and collect " + trashType + " trash.");
            trashCollector.collectTrash(this, trashType);
            new DatabaseConnection().updateDataToHolder(this.holderId, currentCapacity);
            DataLogger.logCollectorActivity(trashType.toString(), locationName);
        }
    }

    public String getLocation() {
        return location;
    }

    public double getMaximumCapacity() {
        return maximumCapacity;
    }

    public TrashType getTrashType() {
        return trashType;
    }

    public double getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(double currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public int getHolderId() {
        return holderId;
    }
    public void resetCurrentCapacity() {
        currentCapacity = 0;
    }
    public void setHolderId(int holderId) {
        this.holderId = holderId;
    }
}
