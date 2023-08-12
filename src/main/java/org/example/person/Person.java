package org.example.person;

import org.example.exception.TrashIsFullException;
import org.example.materials.Trash;
import org.example.recycling_units.Unit;

import java.util.ArrayList;
import java.util.List;

public class Person {
    private final String name;
    private Unit unit;
    private final List<Trash> garbage;

    public Person(String name) {
        this.name = name;
        this.garbage = new ArrayList<>();
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void addNewGarbage(Trash trash) throws TrashIsFullException {
        if (trash.isRecyclable()) {
            garbage.add(trash);
        } else {
            System.out.println("Error: Non-recyclable trash cannot be added to the garbage list.");
        }
    }
    public List<Trash> getGarbage() {
        return garbage;
    }
}
