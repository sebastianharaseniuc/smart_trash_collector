package org.example.recycling_units;

import org.example.communication_and_database.DataLogger;
import org.example.communication_and_database.DatabaseConnection;
import org.example.enums.TrashType;
import org.example.exception.NoRecyclableTrashFoundException;
import org.example.exception.TrashIsFullException;
import org.example.interfaces.RecyclingUnit;
import org.example.materials.GlassTrash;
import org.example.materials.PaperTrash;
import org.example.materials.PlasticTrash;
import org.example.materials.Trash;

import java.util.ArrayList;
import java.util.List;

public class Unit implements RecyclingUnit {

    private final String locationName;
    private final PlasticHolder plasticHolder;
    private final GlassHolder glassHolder;
    private final PaperHolder paperHolder;
    public Unit(String locationName, int plasticHolderCapacity, int glassHolderCapacity, int paperHolderCapacity) {
        this.locationName = locationName;
        this.plasticHolder = new PlasticHolder(locationName, plasticHolderCapacity);
        this.glassHolder = new GlassHolder(locationName, glassHolderCapacity);
        this.paperHolder = new PaperHolder(locationName, paperHolderCapacity);
    }

    public String getLocationName() {
        return locationName;
    }

    public PlasticHolder getPlasticHolder() {
        return plasticHolder;
    }

    public GlassHolder getGlassHolder() {
        return glassHolder;
    }

    public PaperHolder getPaperHolder() {
        return paperHolder;
    }

    public void receives(List<Trash> trashes) throws NoRecyclableTrashFoundException, TrashIsFullException {
        List<Trash> recyclableTrash = new ArrayList<>();
        for (Trash trashItem : trashes) {
            TrashType trashType = getTrashType(trashItem);
            if (trashItem.isRecyclable() && trashType != null) {
                recyclableTrash.add(trashItem);
                System.out.println(trashType + " item added to " + trashItem.getClass().getSimpleName() + ".");
                DataLogger.logGarbageInsertion(trashType.toString(), locationName);
            }
        }

        List<Trash> trashToRemove = new ArrayList<>();

        DatabaseConnection databaseConnection = new DatabaseConnection();

        for (Trash trashItem : recyclableTrash) {

            TrashType trashType = getTrashType(trashItem);
            assert trashType != null;
            Holder holder = getHolderByTrashType(trashType);
            if (holder != null) {
                try {
                    int holderId = databaseConnection.getHolderUnitId(locationName, trashType);

                    holder.setHolderId(holderId);

                    holder.getTrash(trashItem, locationName);

                    databaseConnection.updateDataToHolder(holderId,  holder.getCurrentCapacity());

                    trashToRemove.add(trashItem);

                    if (holder.isFull()) {
                        DataLogger.logCollectorActivity(trashType.toString(), locationName);
                    }

                } catch (TrashIsFullException e) {
                    System.out.println("Error: " + e.getMessage());
                    DataLogger.logTrashNotAdded(trashType.toString(), locationName);
                }
            }
        }

        trashes.removeAll(trashToRemove);
    }

    private Holder getHolderByTrashType(TrashType trashType) throws NoRecyclableTrashFoundException {
        return switch (trashType) {
            case GLASS -> this.glassHolder;
            case PLASTIC -> this.plasticHolder;
            case PAPER -> this.paperHolder;
            default -> throw new NoRecyclableTrashFoundException("No recyclable item found");
        };
    }
    private TrashType getTrashType(Trash trash) {
        if (trash instanceof PlasticTrash && trash.isRecyclable()) {
            return TrashType.PLASTIC;
        } else if (trash instanceof GlassTrash && trash.isRecyclable()) {
            return TrashType.GLASS;
        } else if (trash instanceof PaperTrash && trash.isRecyclable()) {
            return TrashType.PAPER;
        }
        return null;
    }

    public void setPlasticHolder(PlasticHolder plasticHolder) {
    }
}