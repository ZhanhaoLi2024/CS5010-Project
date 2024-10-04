import org.junit.Before;
import org.junit.Test;

import item.Item;
import place.Place;
import place.PlaceModel;
import town.TownModel;

import java.io.IOException;
import static org.junit.Assert.*;

/**
 * The TownModelTest class implements the tests for the TownModel class.
 */
public class TownModelTest {

	private TownModel town;

	/**
	 * Sets up the town for testing.
	 */
	@Before
	public void setUp() throws IOException {
		town = new TownModel("res/SmallTownWorld.txt");
	}

	/**
	 * Tests the loading of the town.
	 */
	@Test
	public void testLoadTown() {
		assertNotNull(town);

		assertEquals(20, town.getPlaces().size());

		Place firstPlace = town.getPlaces().get(0);
		assertEquals("Park", firstPlace.getName());
		assertEquals(0, ((PlaceModel) firstPlace).getRow1());
		assertEquals(0, ((PlaceModel) firstPlace).getCol1());
		assertEquals(2, ((PlaceModel) firstPlace).getRow2());
		assertEquals(3, ((PlaceModel) firstPlace).getCol2());

		Place secondPlace = town.getPlaces().get(1);
		assertEquals("Grocery Store", secondPlace.getName());
	}

	/**
	 * Tests the loading of the items.
	 */
	@Test
	public void testLoadItems() {
		Place firstPlace = town.getPlaces().get(0);
		assertEquals(1, firstPlace.getItems().size());

		Item firstItem = firstPlace.getItems().get(0);
		assertEquals("Toy Ball", firstItem.getName());
		assertEquals(8, firstItem.getDamage());

		Place secondPlace = town.getPlaces().get(1);
		Item secondItem = secondPlace.getItems().get(0);
		assertEquals("Shopping Cart", secondItem.getName());
		assertEquals(12, secondItem.getDamage());
	}

	/**
	 * Tests the movement of the character.
	 */
	@Test
	public void testCharacterMovement() {
		Place firstPlace = town.getCharacter().getCurrentPlace();
		assertEquals("Park", firstPlace.getName());

		town.moveCharacter();
		Place secondPlace = town.getCharacter().getCurrentPlace();
		assertEquals("Grocery Store", secondPlace.getName());

		town.moveCharacter();
		Place thirdPlace = town.getCharacter().getCurrentPlace();
		assertEquals("School", thirdPlace.getName());
	}

	/**
	 * Tests the looping of the character movement.
	 */
	@Test
	public void testCharacterLoopMovement() {
		for (int i = 0; i < 19; i++) {
			town.moveCharacter();
		}
		Place lastPlace = town.getCharacter().getCurrentPlace();
		assertEquals("Community Center", lastPlace.getName());

		town.moveCharacter();
		Place firstPlace = town.getCharacter().getCurrentPlace();
		assertEquals("Park", firstPlace.getName());
	}

	/**
	 * Tests the retrieval of the place information.
	 */
	@Test
	public void testGetPlaceInfo() {
		Place firstPlace = town.getCharacter().getCurrentPlace();

		town.getPlaceInfo(firstPlace);

		assertEquals("Park", firstPlace.getName());
		assertFalse(firstPlace.getItems().isEmpty());
		assertEquals("Toy Ball", firstPlace.getItems().get(0).getName());
	}

	/**
	 * Tests the retrieval of the character in the town.
	 */
	@Test
	public void testGetPlaces() {
		assertEquals(20, town.getPlaces().size());
	}

	/**
	 * Tests the retrieval of the items in the town.
	 */
	@Test
	public void testGetItems() {
		assertEquals(20, town.getItems().size());
	}

	/**
	 * Tests the retrieval of the target name.
	 */
	@Test
	public void testGetTargetName() {
		assertEquals("The Mayor", town.getTargetName());
	}

	/**
	 * Tests the retrieval of the target health.
	 */
	@Test
	public void testGetTargetHealth() {
		assertEquals(100, town.getTargetHealth());
	}

	@Test
	public void testGetTownName() {
		assertEquals("Small Town World", town.getTownName());
	}
}