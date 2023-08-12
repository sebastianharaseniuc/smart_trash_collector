package org.example.interfaces;

import org.example.enums.TrashType;
import org.example.recycling_units.Holder;

public interface TrashCollector {
    void collectTrash(Holder holder, TrashType trashType);
}