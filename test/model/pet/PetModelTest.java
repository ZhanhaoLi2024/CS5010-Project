package model.pet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * The PetModelTest class tests the PetModel class.
 */
public class PetModelTest {


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
    assertEquals(0, pet.getPetCurrentPlaceNumber());
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

}
