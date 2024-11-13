import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import model.place.Place;
import model.player.Player;
import model.player.PlayerModel;
import model.town.Town;
import model.town.TownLoader;
import model.town.TownModel;
import org.junit.Before;
import org.junit.Test;

/**
 * A test class for verifying player visibility functionality in the game.
 * This test suite covers various scenarios of player visibility including:
 * - Players in the same space
 * - Players in neighboring spaces
 * - Players in non-neighboring spaces
 * - Pet's influence on visibility
 * - Edge cases and corner cases
 * The tests ensure that the visibility rules are correctly implemented
 * according to the game specifications.
 */
public class PlayerVisibilityTest {
  private Town town;
  private StringWriter output;
  private Player player1;
  private Player player2;
  private Player player3;

  /**
   * Sets up the test environment before each test case execution.
   * This method initializes a new town, output writer, and three players
   * with different starting positions. The setup ensures each test starts
   * with a clean and consistent game state.
   *
   * @throws IllegalStateException if player creation fails
   */
  @Before
  public void setUp() {
    try {
      output = new StringWriter();
      town = new TownModel(
          new TownLoader(),
          "res/SmallTownWorld.txt",
          new StringReader(""),
          output,
          10
      );

      // Initialize players with different starting positions
      player1 = new PlayerModel("Player1", false, 5, 1);
      player2 = new PlayerModel("Player2", false, 5, 2);
      player3 = new PlayerModel("Player3", false, 5, 4);

      // Add players to the town
      town.getPlayers().add(player1);
      town.getPlayers().add(player2);
      town.getPlayers().add(player3);
    } catch (IllegalStateException | IOException e) {
      fail("Failed to initialize test: " + e.getMessage());
    }
  }

  /**
   * Tests visibility between players occupying the same space.
   * Verifies that players in the same space can always see each other,
   * regardless of other game conditions.
   *
   * @throws IllegalStateException if player movement fails
   */
  @Test
  public void testPlayersInSameSpace() {
    try {
      // Move players to the same space
      player1.moveToPlaceNumber(1);
      player2.moveToPlaceNumber(1);

      assertTrue("Players in same space should be visible to each other",
          town.isPlayerVisible(player1));
      assertTrue("Players in same space should be visible to each other",
          town.isPlayerVisible(player2));
    } catch (IllegalStateException e) {
      fail("Test failed: " + e.getMessage());
    }
  }

  /**
   * Tests visibility between players in adjacent spaces.
   * Verifies that players in neighboring spaces can see each other
   * when no visibility-blocking conditions exist.
   *
   * @throws IllegalStateException if player movement fails
   */
  @Test
  public void testPlayersInNeighboringSpaces() {
    try {
      // Move players to neighboring spaces
      // Assuming spaces 1 and 2 are neighbors in SmallTownWorld.txt
      player1.moveToPlaceNumber(1);
      player2.moveToPlaceNumber(2);

      town.movePet(4); // Move pet to a non-adjacent space

      System.out.println(player1.getPlayerCurrentPlaceNumber());
      System.out.println(player2.getPlayerCurrentPlaceNumber());

      assertTrue("Players in neighboring spaces should be visible to each other",
          town.isPlayerVisible(player1));
      assertTrue("Players in neighboring spaces should be visible to each other",
          town.isPlayerVisible(player2));
    } catch (IllegalStateException | IOException e) {
      fail("Test failed: " + e.getMessage());
    }
  }

  /**
   * Tests visibility between players in non-adjacent spaces.
   * Verifies that players cannot see each other when positioned in
   * spaces that are not neighbors in the game world.
   *
   * @throws IllegalStateException if player movement fails
   */
  @Test
  public void testPlayersInNonNeighboringSpaces() {
    try {
      // First remove any existing players and add only our test players in order
      town.getPlayers().clear();
      town.getPlayers().add(player1);
      town.getPlayers().add(player2);

      // Place players as far apart as possible in the world
      // Using Coffee Shop (18) and Police Station (5)
      player1.moveToPlaceNumber(18); // Coffee Shop
      player2.moveToPlaceNumber(5);  // Police Station

      // Set current player to player1
      while (town.getCurrentPlayerIndex() != 0) {
        town.switchToNextPlayer();
      }

      // Test if player2 is visible from player1's position
      assertFalse("Player in distant space should not be visible",
          town.isPlayerVisible(player2));

      // Set current player to player2
      town.switchToNextPlayer();

      // Test if player1 is visible from player2's position
      assertFalse("Player in distant space should not be visible",
          town.isPlayerVisible(player1));

    } catch (IllegalStateException | IOException e) {
      fail("Test failed: " + e.getMessage());
    }
  }

  /**
   * Tests how a pet's presence affects player visibility in the same space.
   * Verifies that players in the same space remain visible to each other
   * even when a pet is present in that space.
   */
  @Test
  public void testPetInfluenceOnVisibilitySameSpace() {
    try {
      // Move players and pet to the same space
      int spaceNumber = 1;
      player1.moveToPlaceNumber(spaceNumber);
      player2.moveToPlaceNumber(spaceNumber);
      town.movePet(spaceNumber);

      // Players should still be visible to each other even with pet present
      assertTrue("Players in same space should be visible despite pet presence",
          town.isPlayerVisible(player1));
      assertTrue("Players in same space should be visible despite pet presence",
          town.isPlayerVisible(player2));
    } catch (IOException e) {
      fail("Test failed: " + e.getMessage());
    }
  }

  /**
   * Tests the pet's influence on visibility between neighboring spaces.
   * Verifies that a pet's presence in a space affects the visibility
   * of that space and its contents from neighboring spaces.
   */
  @Test
  public void testPetInfluenceOnVisibilityNeighboringSpaces() {
    try {
      // Move players to neighboring spaces and pet to one of them
      player1.moveToPlaceNumber(1);
      player2.moveToPlaceNumber(2);
      town.movePet(1); // Move pet to player1's space

      // Get player1's space and check its visibility
      Place player1Space = town.getPlaceByNumber(player1.getPlayerCurrentPlaceNumber());
      assertFalse("Space with pet should not be visible",
          town.isPlaceVisible(player1Space));

      // Set current player index to player2 for lookAround
      while (town.getCurrentPlayerIndex() != town.getPlayers().indexOf(player2)) {
        town.switchToNextPlayer();
      }

      // Clear previous output
      output.getBuffer().setLength(0);

      // Now lookAround from player2's perspective
      town.lookAround();
      String lookAroundResult = output.toString();

      // Verify that we can see basic information
      assertTrue("Look around result should contain current place info",
          lookAroundResult.contains(town.getPlaceByNumber(2).getName()));

      // Since player2 is in space 2 looking at space 1 (with pet)
      assertTrue("Look around result should indicate pet's presence",
          lookAroundResult.contains("Pet is here") || lookAroundResult.contains("pet is here"));

      // Additional verification that player visibility still works
      assertTrue("Player should still be visible despite pet presence",
          town.isPlayerVisible(player2));

    } catch (IOException e) {
      fail("Test failed: " + e.getMessage());
    }
  }

  /**
   * Tests visibility in scenarios with multiple players in various configurations.
   * Verifies that visibility rules are correctly applied when multiple players
   * are present in different combinations of spaces.
   *
   * @throws IllegalStateException if player movement fails
   */
  @Test
  public void testMultiplePlayerVisibility() {
    try {
      // Set up complex scenario with multiple players
      player1.moveToPlaceNumber(1);
      player2.moveToPlaceNumber(2);
      player3.moveToPlaceNumber(2);

      town.movePet(4); // Move pet to a non-adjacent space

      assertTrue("Player in space with multiple players should be visible",
          town.isPlayerVisible(player2));
      assertTrue("Player in space with multiple players should be visible",
          town.isPlayerVisible(player3));
      assertTrue("Player visible to multiple players should be visible",
          town.isPlayerVisible(player1));
    } catch (IllegalStateException | IOException e) {
      fail("Test failed: " + e.getMessage());
    }
  }

  /**
   * Tests edge cases in the visibility system including:
   * - Null player references
   * - Empty player lists
   * - Players at boundary spaces
   * - Unlisted players
   * Verifies that the system handles these cases gracefully and maintains
   * expected behavior.
   *
   * @throws IllegalStateException if player creation or movement fails
   */
  @Test
  public void testVisibilityEdgeCases() {
    try {
      // Test with null player
      try {
        town.isPlayerVisible(null);
        fail("Should throw IllegalArgumentException for null player");
      } catch (IllegalArgumentException e) {
        // Expected exception
      }

      // Test with empty player list
      try {
        town.getPlayers().clear();
        Player validPlayer = new PlayerModel("ValidPlayer",
            false,
            5,
            1);

        town.getPlayers().add(validPlayer);
        assertFalse("Single player should not be visible to others",
            town.isPlayerVisible(validPlayer));
      } catch (IllegalStateException e) {
        fail("Should handle single player case gracefully");
      }

      // Test player at boundary space (last valid space)
      try {
        Player boundaryPlayer = new PlayerModel("BoundaryPlayer",
            false,
            5,
            town.getPlaces().size());

        town.getPlayers().clear();
        town.getPlayers().add(boundaryPlayer);
        assertFalse("Player at boundary space should be handled correctly",
            town.isPlayerVisible(boundaryPlayer));
      } catch (IllegalStateException e) {
        fail("Should handle boundary space gracefully: " + e.getMessage());
      }

      // Test player not in players list
      try {
        Player unlistedPlayer = new PlayerModel("UnlistedPlayer",
            false,
            5,
            1);

        // Don't add to players list
        assertFalse("Unlisted player should not be visible",
            town.isPlayerVisible(unlistedPlayer));
      } catch (IllegalStateException e) {
        fail("Should handle unlisted player gracefully");
      }

    } catch (IllegalStateException e) {
      fail("Test failed unexpectedly: " + e.getMessage());
    }
  }

  /**
   * Tests visibility changes that occur when players move between spaces.
   * Verifies that the visibility status is correctly updated when players
   * change their positions in the game world.
   *
   * @throws IllegalStateException if player movement fails
   */
  @Test
  public void testVisibilityWithPlayerMovement() {
    try {
      town.movePet(5);
      // Initial setup - players are neighbors
      player1.moveToPlaceNumber(1);
      player2.moveToPlaceNumber(2);
      assertTrue("Players should be visible when neighbors",
          town.isPlayerVisible(player1));

      // Move player2 to non-neighboring space
      player2.moveToPlaceNumber(4);
      assertFalse("Players should not be visible after moving apart",
          town.isPlayerVisible(player1));

      // Move players to same space
      player2.moveToPlaceNumber(1);
      assertTrue("Players should be visible after moving to same space",
          town.isPlayerVisible(player1));
    } catch (IllegalStateException | IOException e) {
      fail("Test failed: " + e.getMessage());
    }
  }

  /**
   * Tests how pet movement affects space visibility over time.
   * Verifies that spaces become obscured or visible as the pet moves
   * through the game world, and that the lookAround functionality
   * correctly reflects these changes.
   */
  @Test
  public void testVisibilityWithPetMovement() {
    try {
      // Clear previous players and add only our test players
      town.getPlayers().clear();
      town.getPlayers().add(player1);
      town.getPlayers().add(player2);

      // Setup initial positions
      player1.moveToPlaceNumber(1); // Park
      player2.moveToPlaceNumber(2); // Grocery Store

      // Make player2 the current player
      while (town.getCurrentPlayerIndex() != town.getPlayers().indexOf(player2)) {
        town.switchToNextPlayer();
      }

      // Clear output for initial check
      output.getBuffer().setLength(0);

      // Check initial visibility through lookAround
      town.lookAround();
      String initialView = output.toString();
      // Verify can see player1's space and player1
      assertTrue("Should be able to see neighboring space initially",
          initialView.contains(town.getPlaceByNumber(1).getName()));
      assertTrue("Should be able to see player1 initially",
          initialView.contains(player1.getName()));

      // Make player2 current player again after lookAround
      while (town.getCurrentPlayerIndex() != town.getPlayers().indexOf(player2)) {
        town.switchToNextPlayer();
      }

      // Move pet to player1's space and clear output
      output.getBuffer().setLength(0);
      town.movePet(1);

      // Look around again from player2's perspective
      town.lookAround();
      String viewWithPet = output.toString();
      // Verify pet's presence is noted and space info is limited
      assertTrue("Should mention pet's presence",
          viewWithPet.contains("Pet is here") || viewWithPet.contains("pet is here"));

      // Make player2 current player one more time
      while (town.getCurrentPlayerIndex() != town.getPlayers().indexOf(player2)) {
        town.switchToNextPlayer();
      }

      // Move pet away and clear output
      output.getBuffer().setLength(0);
      town.movePet(4); // Move to a non-adjacent space

      // Final look around
      town.lookAround();
      String finalView = output.toString();
      // Verify visibility is restored
      assertTrue("Should be able to see neighboring space again",
          finalView.contains(town.getPlaceByNumber(1).getName()));
      assertTrue("Should be able to see player1 again",
          finalView.contains(player1.getName()));

    } catch (IOException e) {
      fail("Test failed: " + e.getMessage());
    }
  }

  /**
   * Tests visibility in corner cases with players in specific spatial configurations.
   * Verifies visibility rules when a player is surrounded by other players
   * in adjacent spaces.
   *
   * @throws IllegalStateException if player setup fails
   */
  @Test
  public void testVisibilityCornerCases() {
    try {
      // Test visibility when space is completely surrounded by other players
      player1.moveToPlaceNumber(2);  // Center space
      player2.moveToPlaceNumber(1);  // One neighboring space
      player3.moveToPlaceNumber(3);  // Another neighboring space

      // Add more players to surround the center
      Player player4 = new PlayerModel("Player4", false, 5, 4);
      town.getPlayers().add(player4);

      assertTrue("Player should be visible when surrounded",
          town.isPlayerVisible(player1));
      assertFalse("Players around should not be visible",
          town.isPlayerVisible(player2) && town.isPlayerVisible(player3));
    } catch (IllegalStateException e) {
      fail("Test failed: " + e.getMessage());
    }
  }

  /**
   * Tests visibility rules between computer-controlled and human players.
   * Verifies that visibility mechanics work consistently regardless of
   * player control type.
   *
   * @throws IllegalStateException if player creation fails
   */
  @Test
  public void testComputerPlayerVisibility() {
    try {
      // Create computer and human players
      Player computerPlayer = new PlayerModel("Computer", true, 5, 3);
      Player humanPlayer = new PlayerModel("Human", false, 5, 4);

      town.getPlayers().clear();
      town.getPlayers().add(computerPlayer);
      town.getPlayers().add(humanPlayer);

      assertTrue("Computer player should be visible to human player",
          town.isPlayerVisible(computerPlayer));
      assertTrue("Human player should be visible to computer player",
          town.isPlayerVisible(humanPlayer));
    } catch (IllegalStateException e) {
      fail("Test failed: " + e.getMessage());
    }
  }

  /**
   * Tests the relationship between pet position and player visibility.
   * Verifies how the pet's position affects visibility of players and spaces,
   * including lookAround results.
   */
  @Test
  public void testMultiplePetInfluenceOnVisibility() {
    try {
      // Clear existing players and add our test players
      town.getPlayers().clear();
      town.getPlayers().add(player1);
      town.getPlayers().add(player2);
      town.movePet(4);

      // Set up players in adjacent spaces
      player1.moveToPlaceNumber(1); // Park
      player2.moveToPlaceNumber(2); // Grocery Store (neighbor to Park)

      // Initial visibility check from player2's perspective
      // Make player2 the current player
      while (town.getCurrentPlayerIndex() != town.getPlayers().indexOf(player2)) {
        town.switchToNextPlayer();
      }

      // Verify initial visibility
      assertTrue("Players should be visible initially",
          town.isPlayerVisible(player1));

      // Move pet to player1's space
      town.movePet(1);

      // Verify space visibility from player2's perspective
      Place player1Space = town.getPlaceByNumber(player1.getPlayerCurrentPlaceNumber());
      assertFalse("Space with pet should not be visible",
          town.isPlaceVisible(player1Space));

      // Clear output buffer
      output.getBuffer().setLength(0);

      // Make sure player2 is still the current player for lookAround
      while (town.getCurrentPlayerIndex() != town.getPlayers().indexOf(player2)) {
        town.switchToNextPlayer();
      }

      // Look around from player2's position
      town.lookAround();
      String lookAroundResult = output.toString();

      // Verify that neighboring space (where pet is) shows up in look around results
      assertTrue("Look around should mention the neighboring space",
          lookAroundResult.contains(town.getPlaceByNumber(1).getName()));

      // Verify pet presence is noted when looking at neighboring space
      assertTrue("Look around should indicate pet presence",
          lookAroundResult.contains("Pet is here")
              || lookAroundResult.contains("Pet")
              || lookAroundResult.contains("pet"));
    } catch (IOException e) {
      fail("Test failed: " + e.getMessage());
    }
  }

  /**
   * Tests visibility through a series of connected spaces.
   * Verifies that visibility rules are correctly applied across
   * chains of connected spaces and how the pet's presence affects
   * these visibility chains.
   */
  @Test
  public void testVisibilityThroughConnectedSpaces() {
    try {
      // First clear any existing players to ensure clean test state
      town.getPlayers().clear();
      town.getPlayers().add(player1);
      town.getPlayers().add(player2);
      town.getPlayers().add(player3);

      // Place players in a chain of connected spaces
      // Using actual connected spaces from SmallTownWorld.txt
      player1.moveToPlaceNumber(1);  // Park
      player2.moveToPlaceNumber(2);  // Grocery Store
      player3.moveToPlaceNumber(2);  // Also in Grocery Store

      // Test visibility from player1's perspective
      // Set current player to player1
      while (town.getCurrentPlayerIndex() != town.getPlayers().indexOf(player1)) {
        town.switchToNextPlayer();
      }

      // Player1 should see player2 and player3 (they're in a neighboring space)
      assertTrue("Player1 should see players in neighboring space",
          town.isPlayerVisible(player2));
      assertTrue("Player1 should see players in neighboring space",
          town.isPlayerVisible(player3));

      // Now move player3 to a space not directly connected to player1
      player3.moveToPlaceNumber(4);  // School

      // Player1 should still see player2 but not player3
      assertTrue("Player1 should still see player2 in neighboring space",
          town.isPlayerVisible(player2));
      assertFalse("Player1 should not see player3 in non-neighboring space",
          town.isPlayerVisible(player3));

      // Test pet's influence
      // Move pet to player2's space (the connecting space)
      town.movePet(2);

      // Check visibility after pet moves
      Place player2Space = town.getPlaceByNumber(player2.getPlayerCurrentPlaceNumber());
      assertFalse("Space with pet should not be visible",
          town.isPlaceVisible(player2Space));

      // Look around to verify pet's effect
      output.getBuffer().setLength(0);
      town.lookAround();
      String lookAroundResult = output.toString();
      assertTrue("Look around should indicate pet's presence",
          lookAroundResult.contains("Pet is here")
              || lookAroundResult.contains("pet is here"));

    } catch (IOException e) {
      fail("Test failed: " + e.getMessage());
    }
  }

  /**
   * Tests visibility changes during the progression of player turns.
   * Verifies that visibility states are correctly maintained and updated
   * as players take their turns and move through the game world.
   */
  @Test
  public void testVisibilityDuringTurns() {
    try {
      // First clear existing players and add only test players
      town.getPlayers().clear();
      town.getPlayers().add(player1);
      town.getPlayers().add(player2);
      town.movePet(4);

      // Setup players in adjacent spaces
      player1.moveToPlaceNumber(1);  // Park
      player2.moveToPlaceNumber(2);  // Grocery Store (neighbor to Park)

      // Make sure player1 is the current player
      while (town.getCurrentPlayerIndex() != town.getPlayers().indexOf(player1)) {
        town.switchToNextPlayer();
      }

      // Initial visibility check
      assertTrue("Players should be visible at start of turn",
          town.isPlayerVisible(player1));

      // Instead of using movePlayer(), directly move player1 to a non-adjacent space
      player1.moveToPlaceNumber(4);  // School (not adjacent to Grocery Store)

      // Manually trigger turn switch to simulate move action
      town.switchToNextPlayer();

      // Get updated positions
      Place currentPlace = town.getPlaceByNumber(player1.getPlayerCurrentPlaceNumber());
      Place otherPlace = town.getPlaceByNumber(player2.getPlayerCurrentPlaceNumber());

      // Check if spaces are neighbors or same
      boolean shouldBeVisible = currentPlace.isNeighbor(otherPlace)
          || currentPlace.equals(otherPlace);

      // Verify visibility state matches spatial relationship
      assertEquals("Visibility should match spaces' relationship",
          shouldBeVisible, town.isPlayerVisible(player1));

      // Additional verification through lookAround
      output.getBuffer().setLength(0);
      town.lookAround();
      String lookAroundResult = output.toString();

      if (shouldBeVisible) {
        assertTrue("Should see player info in lookAround if visible",
            lookAroundResult.contains(player1.getName()));
      }

    } catch (IOException e) {
      fail("Test failed: " + e.getMessage());
    }
  }

  /**
   * Tests visibility when all players occupy the same space.
   * Verifies that visibility rules are correctly applied when multiple
   * players are concentrated in a single space, including the effect
   * of pet presence.
   */
  @Test
  public void testAllPlayersInSameSpace() {
    try {
      // Move all players to the same space
      int targetSpace = 1;
      for (Player player : town.getPlayers()) {
        player.moveToPlaceNumber(targetSpace);
      }

      // All players should be visible to each other
      for (Player player : town.getPlayers()) {
        assertTrue("All players should be visible when in same space",
            town.isPlayerVisible(player));
      }

      // Move pet to the same space
      town.movePet(targetSpace);

      // Players should still be visible to each other
      for (Player player : town.getPlayers()) {
        assertTrue("Players should remain visible when pet enters same space",
            town.isPlayerVisible(player));
      }
    } catch (IOException e) {
      fail("Test failed: " + e.getMessage());
    }
  }
}