import item.ItemModel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * The ItemModelTest class implements the tests for the ItemModel class.
 */
public class ItemModelTest {

	private ItemModel gasCan;
	private ItemModel houseKey;

	/**
	 * Sets up the items for testing.
	 */
	@Before
	public void setUp() {
		gasCan = new ItemModel("Gas Can", 11);
		houseKey = new ItemModel("House Key", 15);
	}

	/**
	 * Tests the initialization of the items.
	 */
	@Test
	public void testItemInitialization() {
		assertEquals("Gas Can", gasCan.getName());
		assertEquals(11, gasCan.getDamage());

		assertEquals("House Key", houseKey.getName());
		assertEquals(15, houseKey.getDamage());
	}

	/**
	 * Tests the retrieval of the name of the items.
	 */
	@Test
	public void testGetName() {
		assertEquals("Gas Can", gasCan.getName());
		assertEquals("House Key", houseKey.getName());
	}

	/**
	 * Tests the retrieval of the damage value of the items.
	 */
	@Test
	public void testGetDamage() {
		assertEquals(11, gasCan.getDamage());
		assertEquals(15, houseKey.getDamage());
	}
}