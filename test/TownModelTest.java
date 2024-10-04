import org.junit.Before;
import org.junit.Test;

import item.Item;
import place.Place;
import place.PlaceModel;
import town.TownModel;

import java.io.IOException;
import static org.junit.Assert.*;

public class TownModelTest {

    private TownModel town;

    @Before
    public void setUp() throws IOException {
        town = new TownModel("res/SmallTownWorld.txt");
    }

    @Test
    public void testLoadTown() {
        assertNotNull(town);
        
        assertEquals(20, town.getPlaces().size());
        
        Place firstPlace = town.getPlaces().get(0);
        assertEquals("Park", firstPlace.getName());
        assertEquals(0, ((PlaceModel)firstPlace).getRow1());
        assertEquals(0, ((PlaceModel)firstPlace).getCol1());
        assertEquals(2, ((PlaceModel)firstPlace).getRow2());
        assertEquals(3, ((PlaceModel)firstPlace).getCol2());

        Place secondPlace = town.getPlaces().get(1);
        assertEquals("Grocery Store", secondPlace.getName());
    }

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

    @Test
    public void testDisplayPlaceInfo() {
        Place firstPlace = town.getCharacter().getCurrentPlace();
        
        town.displayPlaceInfo(firstPlace);
        
        assertEquals("Park", firstPlace.getName());
        assertFalse(firstPlace.getItems().isEmpty());
        assertEquals("Toy Ball", firstPlace.getItems().get(0).getName());
    }
    
    @Test
    public void testGetPlaces() {
        assertEquals(20, town.getPlaces().size());
    }

    @Test
    public void testGetItems() {
        assertEquals(20, town.getItems().size());
    }
}