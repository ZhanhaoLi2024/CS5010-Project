import item.Item;
import item.ItemModel;
import org.junit.Before;
import org.junit.Test;
import place.Place;
import place.PlaceModel;

import java.util.List;
import static org.junit.Assert.*;

public class PlaceModelTest {

    private PlaceModel place1;
    private PlaceModel place2;
    private PlaceModel place3;
    private ItemModel item1;
    private ItemModel item2;

    @Before
    public void setUp() {
        place1 = new PlaceModel(4, 2, 6, 7, "Hospital");
        place2 = new PlaceModel(6, 3, 9, 5, "Library");
        place3 = new PlaceModel(9, 3, 11, 7, "Fire Station");

        item1 = new ItemModel("Medical Kit", 18);
        item2 = new ItemModel("Storybook", 10);
    }

    @Test
    public void testPlaceInitialization() {
        assertEquals("Fire Station", place3.getName());
        assertEquals(4, place1.getRow1());
        assertEquals(7, place1.getCol2());
    }

    @Test
    public void testAddItem() {
        place1.addItem(item1);
        place1.addItem(item2);

        List<Item> items = place1.getItems();
        assertEquals(2, items.size());
        assertEquals("Medical Kit", items.get(0).getName());
        assertEquals(10, items.get(1).getDamage());
    }

    @Test
    public void testAddNeighbor() {
        place2.addNeighbor(place1);
        place2.addNeighbor(place3);

        List<Place> neighbors = place2.getNeighbors();
        assertEquals(2, neighbors.size());
        assertEquals("Hospital", neighbors.get(0).getName());
        assertEquals("Fire Station", neighbors.get(1).getName());
    }

    @Test
    public void testGetNeighbors() {
        place1.addNeighbor(place2);
        List<Place> neighbors = place1.getNeighbors();

        assertEquals(1, neighbors.size());
        assertEquals("Library", neighbors.get(0).getName());
    }

    @Test
    public void testIsNeighbor() {
        assertTrue(place1.isNeighbor(place2));
        assertFalse(place1.isNeighbor(place3));
    }

    @Test
    public void testGetItems() {
        place1.addItem(item1);
        List<Item> items = place1.getItems();

        assertEquals(1, items.size());
        assertEquals("Medical Kit", items.get(0).getName());
    }
}