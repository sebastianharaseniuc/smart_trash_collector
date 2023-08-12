package org.example.interfaces;
import org.example.exception.NoRecyclableTrashFoundException;
import org.example.exception.TrashIsFullException;
import org.example.materials.Trash;
import org.example.recycling_units.GlassHolder;
import org.example.recycling_units.PaperHolder;
import org.example.recycling_units.PlasticHolder;

import java.util.List;

public interface RecyclingUnit {
    void receives(List<Trash> trashes) throws NoRecyclableTrashFoundException, TrashIsFullException;
}
