import org.example.enums.TrashType;
import org.example.exception.TrashIsFullException;
import org.example.materials.PlasticTrash;
import org.example.recycling_units.Holder;
import org.example.recycling_units.PlasticHolder;
import org.example.transportation.Collector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HolderTest {

    private Holder holder;

    @BeforeEach
    public void setUp() {
        double maximumCapacity = 500.0;
        TrashType trashType = TrashType.PLASTIC;
        holder = new PlasticHolder("Zorilor", maximumCapacity);
    }

    @Test // checks if the method isFull() returns false when the holder is not full
    public void testIsFullWhenNotFull() {
        assertFalse(holder.isFull());
    }

    @Test   // checks if the isFull() method returns true when the holder is full
    // adds a PlasticTrash object to the holder, causing it to become full
    public void testIsFullWhenFull() throws TrashIsFullException {
        PlasticTrash plasticTrash = new PlasticTrash("Plastic Trash", 500.0);

        holder.getTrash(plasticTrash, "Zorilor");

        assertFalse(holder.isFull());
    }

    @Test   // checks the behavior of the getTrash() method when adding trash within the capacity limit
    // creates a PlasticTrash object with a weight that is within the maximum capacity of the holder
    // getTrash() method is called to add the trash to the holder
    // test asserts that the current capacity of the holder matches the weight of the added trash and that the holder is not full
    public void testGetTrashWithinCapacity() throws TrashIsFullException {
        PlasticTrash plasticTrash = new PlasticTrash("Some Plastic Trash", 100.0);

        holder.getTrash(plasticTrash, "Zorilor");

        assertEquals(100.0, holder.getCurrentCapacity(), 0.001);
        assertFalse(holder.isFull());
    }

    @Test   // checks the behavior of the getTrash() method when attempting to add trash that exceeds the capacity limit
    // creates a PlasticTrash object with a weight that exceeds the maximum capacity of the holder
    // throws the exception when trying to add the trash
    public void testGetTrashExceedingCapacity() {
        PlasticTrash plasticTrash = new PlasticTrash("Some Plastic Trash", 600.0);

        assertThrows(TrashIsFullException.class, () -> holder.getTrash(plasticTrash, "Zorilor"));
    }

    @Test   // checks whether the resetCurrentCapacity() method correctly resets the current capacity of the holder
    // adds a PlasticTrash object to the holder, updates the current capacity, and then calls the resetCurrentCapacity() method
    // test asserts that the current capacity is reset to zero and that the holder is not full
    public void testResetCurrentCapacity() throws TrashIsFullException {
        PlasticTrash plasticTrash = new PlasticTrash("Some Plastic Trash", 460.0);

        holder.getTrash(plasticTrash, "Zorilor");
        holder.resetCurrentCapacity();

        assertEquals(0.0, holder.getCurrentCapacity(), 0.001);
        assertFalse(holder.isFull());
    }
}
