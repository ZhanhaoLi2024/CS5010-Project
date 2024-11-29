package model.item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ItemModel. Tests item creation, damage validation, and equality comparison.
 */
public class ItemModelTest {
  private ItemModel item1;
  private ItemModel item2;

  @Before
  public void setUp() {
    item1 = new ItemModel("Sword", 10);
    item2 = new ItemModel("Shield", 5);
  }

  // Test item creation
  @Test
  public void testValidItemCreation() {
    assertEquals("Item name should match", "Sword", item1.getName());
    assertEquals("Item damage should match", 10, item1.getDamage());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullNameItemCreation() {
    new ItemModel(null, 10);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyNameItemCreation() {
    new ItemModel("", 10);
  }

  // Test damage validation
  @Test(expected = IllegalArgumentException.class)
  public void testNegativeDamageItemCreation() {
    new ItemModel("Sword", -1);
  }

  @Test
  public void testZeroDamageItemCreation() {
    ItemModel zeroItem = new ItemModel("Feather", 0);
    assertEquals("Zero damage should be allowed", 0, zeroItem.getDamage());
  }

  @Test
  public void testMaxDamageItemCreation() {
    ItemModel maxItem = new ItemModel("Ultimate", Integer.MAX_VALUE);
    assertEquals("Max integer damage should be allowed", Integer.MAX_VALUE, maxItem.getDamage());
  }

  // Test equality comparison
  @Test
  public void testEqualItems() {
    ItemModel duplicateItem = new ItemModel("Sword", 10);
    assertEquals("Same name and damage should be equal", item1, duplicateItem);
    assertEquals("Hash codes should match for equal items",
        item1.hashCode(), duplicateItem.hashCode());
  }

  @Test
  public void testUnequalItems() {
    assertNotEquals("Different items should not be equal", item1, item2);
    assertNotEquals("Different items should not be equal", item1, item2);
  }

  @Test
  public void testUnequalItemsSameName() {
    ItemModel differentDamage = new ItemModel("Sword", 20);
    assertNotEquals("Same name but different damage should not be equal",
        item1, differentDamage);
  }

  @Test
  public void testUnequalItemsSameDamage() {
    ItemModel differentName = new ItemModel("Axe", 10);
    assertNotEquals("Different name but same damage should not be equal",
        item1, differentName);
  }

  @Test
  public void testEqualityWithNull() {
    assertNotEquals("Item should not equal null", null, item1);
  }

  @Test
  public void testEqualityWithDifferentClass() {
    assertNotEquals("Item should not equal non-item object", "Sword", item1);
  }

  @Test
  public void testEqualityWithSelf() {
    assertEquals("Item should equal itself", item1, item1);
  }
}