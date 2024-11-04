package model.pet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/**
 * The PetModelTest class tests the PetModel class.
 */
public class PetModelTest {

  private static final String VALID_NAME = "TestPet";
  private PetModel pet;

  @Before
  public void setUp() {
    pet = new PetModel(VALID_NAME);
  }

  /**
   * Tests the PetModel constructor.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testPetModel() {
    PetModel pet = new PetModel("");
  }

  /**
   * Tests the PetModel constructor.
   */
  @Test
  public void testPetModel2() {
    PetModel pet = new PetModel("Test");
    assertEquals("Test", pet.getName());
  }

  /**
   * Tests the getPetCurrentPlaceNumber method.
   */
  @Test
  public void testGetPetCurrentPlaceNumber() {
    PetModel pet = new PetModel("Test");
    assertEquals(1, pet.getPetCurrentPlaceNumber());
  }

  /**
   * Tests the movePet method.
   */
  @Test
  public void testMovePet() {
    PetModel pet = new PetModel("Test");
    pet.movePet(1);
    assertEquals(1, pet.getPetCurrentPlaceNumber());
  }

  /**
   * Tests the movePet method.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMovePet2() {
    PetModel pet = new PetModel("Test");
    pet.movePet(-1);
  }

  // Constructor Tests

  /**
   * Tests the PetModel constructor with a valid name.
   */
  @Test
  public void testValidConstruction() {
    PetModel newPet = new PetModel(VALID_NAME);
    assertNotNull("Pet should be created successfully", newPet);
    assertEquals(VALID_NAME, newPet.getName());
    assertEquals(1, newPet.getPetCurrentPlaceNumber()); // Should start at place 1
  }

  /**
   * Tests the PetModel constructor with a name containing special characters.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testEmptyNameConstruction() {
    new PetModel("");
  }

  /**
   * Tests the PetModel constructor with a name containing special characters.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNullNameConstruction() {
    new PetModel(null);
  }

  /**
   * Tests the PetModel constructor with a name containing special characters.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testWhitespaceNameConstruction() {
    new PetModel("   ");
  }

  // getName() Tests

  /**
   * Tests the getName method.
   */
  @Test
  public void testGetName() {
    assertEquals("Pet name should match constructor input", VALID_NAME, pet.getName());
  }

  /**
   * Tests the getName method with a name containing special characters.
   */
  @Test
  public void testGetNameWithSpecialCharacters() {
    String specialName = "Mr. Whiskers Jr. #1!";
    PetModel specialPet = new PetModel(specialName);
    assertEquals("Pet name should handle special characters", specialName, specialPet.getName());
  }

  /**
   * Tests the getName method with a name containing Unicode characters.
   */
  @Test
  public void testGetNameWithUnicode() {
    String unicodeName = "宠物猫"; // Chinese characters
    PetModel unicodePet = new PetModel(unicodeName);
    assertEquals("Pet name should handle unicode characters", unicodeName, unicodePet.getName());
  }

  // getPetCurrentPlaceNumber() Tests

  /**
   * Tests the getPetCurrentPlaceNumber method.
   */
  @Test
  public void testInitialPlaceNumber() {
    assertEquals("Pet should start at place 1", 1, pet.getPetCurrentPlaceNumber());
  }

  // movePet() Tests

  /**
   * Tests the movePet method with a valid place number.
   */
  @Test
  public void testValidMove() {
    int validPlace = 5;
    pet.movePet(validPlace);
    assertEquals("Pet should move to specified valid place", validPlace,
        pet.getPetCurrentPlaceNumber());
  }

  /**
   * Tests the movePet method with a valid place number.
   */
  @Test
  public void testMultipleMoves() {
    int[] moves = {2, 3, 4, 5, 1};
    for (int place : moves) {
      pet.movePet(place);
      assertEquals("Pet should move to each specified place", place,
          pet.getPetCurrentPlaceNumber());
    }
  }

  /**
   * Tests the movePet method with a valid place number.
   */
  @Test
  public void testMoveToSamePlace() {
    int currentPlace = pet.getPetCurrentPlaceNumber();
    pet.movePet(currentPlace);
    assertEquals("Moving to same place should be allowed", currentPlace,
        pet.getPetCurrentPlaceNumber());
  }

  /**
   * Tests the movePet method with a valid place number.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMoveToNegativePlace() {
    pet.movePet(-1);
  }

  /**
   * Tests the movePet method with a valid place number.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMoveToZeroPlace() {
    pet.movePet(0);
  }

  /**
   * Tests the movePet method with a valid place number.
   */
  @Test
  public void testMoveToLargeNumber() {
    int largeNumber = Integer.MAX_VALUE;
    try {
      pet.movePet(largeNumber);
      assertEquals("Pet should handle large place numbers", largeNumber,
          pet.getPetCurrentPlaceNumber());
    } catch (IllegalArgumentException e) {
      fail("Should accept any positive integer as place number");
    }
  }

  // Edge Cases and Boundary Tests

  /**
   * Tests the movePet method with a valid place number.
   */
  @Test
  public void testBoundaryMoves() {
    // Test moving to minimum valid place
    pet.movePet(1);
    assertEquals("Should allow move to minimum valid place", 1, pet.getPetCurrentPlaceNumber());

    // Test moving to a very large place number
    int largePlace = Integer.MAX_VALUE;
    pet.movePet(largePlace);
    assertEquals("Should allow move to maximum integer value", largePlace,
        pet.getPetCurrentPlaceNumber());
  }

  /**
   * Tests the movePet method with a valid place number.
   */
  @Test
  public void testConsecutiveMovesToSamePlace() {
    int place = 3;
    for (int i = 0; i < 100; i++) {
      pet.movePet(place);
      assertEquals("Repeated moves to same place should work", place,
          pet.getPetCurrentPlaceNumber());
    }
  }

  // State Consistency Tests

  /**
   * Tests the state consistency after a valid move.
   */
  @Test
  public void testStateConsistencyAfterInvalidMove() {
    int initialPlace = pet.getPetCurrentPlaceNumber();
    try {
      pet.movePet(-1);
      fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertEquals("Place number should remain unchanged after invalid move",
          initialPlace, pet.getPetCurrentPlaceNumber());
    }
  }

  @Test
  public void testNameImmutability() {
    String originalName = pet.getName();
    // Try to modify the returned name
    String returnedName = pet.getName();
    returnedName = "NewName";
    assertEquals("Pet name should remain unchanged", originalName, pet.getName());
  }

  @Test
  public void testInitialPosition() {
    PetModel pet = new PetModel("Test");
    assertEquals("Pet should start at position 1", 1, pet.getPetCurrentPlaceNumber());
  }

  @Test
  public void testPositionAfterMove() {
    PetModel pet = new PetModel("Test");
    pet.movePet(2);
    assertEquals("Pet should move to position 2", 2, pet.getPetCurrentPlaceNumber());

    // Move back to initial position
    pet.movePet(1);
    assertEquals("Pet should move back to position 1", 1, pet.getPetCurrentPlaceNumber());
  }

  @Test
  public void testPositionValidity() {
    PetModel pet = new PetModel("Test");

    // Test various valid positions
    for (int pos = 1; pos <= 5; pos++) {
      pet.movePet(pos);
      assertEquals("Pet should move to valid position " + pos,
          pos, pet.getPetCurrentPlaceNumber());
    }
  }

}
