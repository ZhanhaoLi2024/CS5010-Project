package model.town;

import java.io.IOException;
import java.util.List;
import model.item.Item;
import model.pet.Pet;
import model.place.Place;
import model.player.Player;
import model.target.Target;

/**
 * The Town interface defines the basic behaviors and attributes of a town in a
 * game. A town has a name, a list of places, and a character that can move
 * between different places.
 */
public interface Town {

  /**
   * Displays the information of the specified place.
   *
   * @param place the place to display the information of
   */
  void getPlaceInfo(Place place) throws IOException;

  /**
   * Retrieves the place number by name.
   *
   * @param placeName the name of the place
   * @return the place number
   */
  int getPlaceNumberByName(String placeName) throws IOException;

  /**
   * Retrieves the place by number.
   *
   * @param placeNumber the number of the place
   * @return the place
   */
  Place getPlaceByNumber(int placeNumber);

  /**
   * Moves the character to the next place.
   */
  void moveTarget();

  /**
   * Retrieves the character in the town.
   *
   * @return the character in the town
   */
  Target getTarget();

  /**
   * Retrieves the pet in the town.
   *
   * @return the pet in the town
   */
  Pet getPet();

  /**
   * Determines if a place is visible (not blocked by pet).
   *
   * @param place the place to check visibility for
   * @return true if the place is visible, false otherwise
   */
  boolean isPlaceVisible(Place place);

  /**
   * Moves the pet to a new location.
   *
   * @param placeNumber the number of the place to move the pet to
   * @throws IllegalArgumentException if the place number is invalid
   */
  void movePet(int placeNumber) throws IOException;

  /**
   * Retrieves the name of the town.
   *
   * @return the name of the town as a String
   */
  String getName();

  /**
   * Retrieves the list of items in the town.
   *
   * @return the list of items in the town
   */
  List<Item> getItems();

  /**
   * Retrieves the list of places in the town.
   *
   * @return the list of places in the town
   */
  List<Place> getPlaces();

  /**
   * Retrieves the name of the target character.
   *
   * @return the name of the target character as a String
   */
  String getTargetName();

  /**
   * Retrieves the health status of the target character.
   *
   * @return the health status of the target character
   */
  int getTargetHealth();

  /**
   * Retrieves the name of the town.
   *
   * @return the name of the town as a String
   */
  String getTownName();

  /**
   * Retrieves the list of players in the town.
   *
   * @return the list of players in the town
   */
  List<Player> getPlayers();

  /**
   * Retrieves the neighbors of the specified place.
   *
   * @param place the place to retrieve the neighbors of
   * @return the neighbors of the specified place
   */
  List<Place> getCurrentPlaceNeighbors(Place place);

  /**
   * Adds a computer player to the town.
   *
   * @throws IOException if an I/O error occurs
   */
  void addComputerPlayer() throws IOException;

  /**
   * Adds a player to the town.
   *
   * @throws IOException if an I/O error occurs
   */
  void addPlayer() throws IOException;

  /**
   * Shows information about all places.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  void showAllPlacesInfo() throws IOException;

  /**
   * Shows information about a specific place.
   *
   * @param placeName the name of the place
   * @throws IOException if there is an issue with I/O operations.
   */
  void getPlaceByName(String placeName) throws IOException;

  /**
   * Shows information about all places.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  void showAllPlayersInfo() throws IOException;

  /**
   * Shows information about a specific player.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  void getPlayerByName(String playerName) throws IOException;

  /**
   * Allows the player to look around the current place.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  void lookAround() throws IOException;

  /**
   * Switches to the next player in the game.
   */
  void switchToNextPlayer() throws IOException;

  /**
   * Shows the current information of the player.
   *
   * @throws IOException if there is an issue with I/O operations
   */
  void showPlayerCurrentInfo() throws IOException;

  /**
   * Checks if the game is over.
   *
   * @return true if the game is over, false otherwise
   */
  boolean isGameOver();

  /**
   * Checks if the computer controller is a player.
   *
   * @return true if the computer controller is a player, false otherwise
   */
  Boolean isComputerControllerPlayer();

  int getCurrentTurn();

  /**
   * Moves the player to the next place in the list of neighboring places.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  void movePlayer() throws IOException;

  /**
   * Allows the player to pick up an item from the current location.
   *
   * @throws IllegalStateException if the player cannot carry any more items.
   */
  void pickUpItem() throws IOException;

  /**
   * Gets the index of the current player.
   *
   * @return the index of the current player
   */
  int getCurrentPlayerIndex();

  /**
   * Checks if a player can be seen by other players.
   * A player is visible if another player is in the same room or a neighboring room.
   *
   * @param player the player to check visibility for
   * @return true if the player can be seen by others, false otherwise
   */
  boolean isPlayerVisible(Player player);

  /**
   * Displays the current state of the target character.
   *
   * @throws IOException if there is an error writing output
   */
  void showTargetInfo() throws IOException;

  void startTurn() throws IOException;

  /**
   * Executes an attack with a specific item.
   *
   * @param player the player performing the attack
   * @param item   the item being used
   * @throws IOException              if there is an error with output
   * @throws IllegalArgumentException if player or item is null
   */
  void executeItemAttack(Player player, Item item) throws IOException;

  /**
   * Executes a basic poke attack that deals 1 damage.
   *
   * @param player the player performing the poke attack
   * @throws IOException              if there is an error with output
   * @throws IllegalArgumentException if player is null
   */
  void executePoke(Player player) throws IOException;

  /**
   * Executes an attack for a computer-controlled player.
   *
   * @param player the computer-controlled player
   * @throws IOException              if there is an error with output
   * @throws IllegalArgumentException if player is null or not computer-controlled
   */
  void executeComputerAttack(Player player) throws IOException;

  /**
   * Handles attack options and execution for human players.
   *
   * @param player the human player making the attack
   * @throws IOException              if there is an error with output
   * @throws IllegalArgumentException if player is null or is computer-controlled
   */
  void handleHumanAttack(Player player) throws IOException;

  /**
   * Retrieves the maximum number of turns allowed in the game.
   *
   * @return the maximum number of turns allowed in the game
   */
  int getMaxTurns();
}
