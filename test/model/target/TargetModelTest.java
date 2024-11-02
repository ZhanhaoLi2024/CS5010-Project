package model.target;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import model.place.Place;
import model.place.PlaceModel;
import org.junit.Before;
import org.junit.Test;

/**
 * The CharacterModelTest class tests the methods in the CharacterModel class.
 */
public class TargetModelTest {

  private TargetModel target;

  /**
   * Sets up a character with a name, health, and a list of places.
   */
  @Before
  public void setUp() {
    PlaceModel pharmacy = new PlaceModel(8, 7, 11, 11, "Pharmacy", String.valueOf(1));
    PlaceModel barberShop = new PlaceModel(6, 9, 8, 11, "Barber Shop", String.valueOf(2));
    PlaceModel coffeeShop = new PlaceModel(0, 10, 5, 12, "Coffee Shop", String.valueOf(3));

    List<Place> places = new ArrayList<>();
    places.add(pharmacy);
    places.add(barberShop);
    places.add(coffeeShop);

    target = new TargetModel("The Mayor", 100, pharmacy, places);
  }

  /**
   * Tests the initialization of the character.
   */
  @Test
  public void testCharacterInitialization() {
    assertEquals("The Mayor", target.getName());
    assertEquals(100, target.getHealth());
    assertEquals("Pharmacy", target.getCurrentPlace().getName());
  }

  /**
   * Tests the movement of the character to the next place.
   */
  @Test
  public void testMoveToNextPlace() {
    assertEquals("Pharmacy", target.getCurrentPlace().getName());

    target.moveToNextPlace();
    assertEquals("Barber Shop", target.getCurrentPlace().getName());

    target.moveToNextPlace();
    assertEquals("Coffee Shop", target.getCurrentPlace().getName());
  }

  /**
   * Tests the movement of the character to the next place and looping back to the
   * first place.
   */
  @Test
  public void testMoveToNextPlaceAndLoopBack() {
    target.moveToNextPlace();
    target.moveToNextPlace();

    target.moveToNextPlace();
    assertEquals("Pharmacy", target.getCurrentPlace().getName());
  }

  /**
   * Tests the retrieval of the current place where the character is located.
   */
  @Test
  public void testGetCurrentPlace() {
    assertEquals("Pharmacy", target.getCurrentPlace().getName());

    target.moveToNextPlace();
    assertEquals("Barber Shop", target.getCurrentPlace().getName());
  }
}