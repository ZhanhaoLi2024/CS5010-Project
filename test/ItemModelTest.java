import item.ItemModel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ItemModelTest {

    private ItemModel gasCan;
    private ItemModel houseKey;

    @Before
    public void setUp() {
        gasCan = new ItemModel("Gas Can", 11);
        houseKey = new ItemModel("House Key", 15);
    }

    @Test
    public void testItemInitialization() {
        assertEquals("Gas Can", gasCan.getName());
        assertEquals(11, gasCan.getDamage());

        assertEquals("House Key", houseKey.getName());
        assertEquals(15, houseKey.getDamage());
    }

    @Test
    public void testGetName() {
        assertEquals("Gas Can", gasCan.getName());
        assertEquals("House Key", houseKey.getName());
    }

    @Test
    public void testGetDamage() {
        assertEquals(11, gasCan.getDamage());
        assertEquals(15, houseKey.getDamage());
    }
}