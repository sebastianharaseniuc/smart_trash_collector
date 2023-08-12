package org.example.transportation;

import org.example.enums.TrashType;
import org.example.interfaces.TrashCollector;
import org.example.recycling_units.Holder;

public class Collector implements TrashCollector {
    @Override
    public void collectTrash(Holder holder, TrashType trashType) {
        System.out.println("Collector truck is here to collect " + trashType + " trash from " + holder.getClass().getSimpleName());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            holder.resetCurrentCapacity();
        }
    }
}