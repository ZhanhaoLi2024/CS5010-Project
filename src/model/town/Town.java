package model.town;

import java.io.IOException;
import java.util.List;
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
   * Shows the current information about the pet.
   */
  String petCurrentInfo();

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
   * Moves the pet to a new location.
   *
   * @param placeNumber the number of the place to move the pet to
   * @throws IllegalArgumentException if the place number is invalid
   */
  void movePet(int placeNumber) throws IOException;


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
   * Retrieves the list of players in the town.
   *
   * @return the list of players in the town
   */
  List<Player> getPlayers();


  Integer getPlayerCurrPlaceNumber(int playerIndex);

  String getCurrentPlaceInfo(int placeNumber) throws IOException;

  String getCurrentPlaceNeighborsInfo(int placeNumber) throws IOException;

  /**
   * Adds a player to the town.
   *
   * @param newPlayerName       the name of the new player
   * @param newPlayerPlace      the starting place of the new player
   * @param newPlayerCarryLimit the carry limit of the new player
   * @param isComputerPlayer    true if the player is computer-controlled, false otherwise
   */
  void addPlayer(String newPlayerName, int newPlayerPlace, int newPlayerCarryLimit,
                 boolean isComputerPlayer);


  String getPlayerByName(String playerName) throws IOException;

  /**
   * Allows the player to look around the current place.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  void lookAround() throws IOException;

  boolean attackTarget(String attackItemName) throws IOException;

  /**
   * Switches to the next player in the game.
   */
  void switchToNextPlayer() throws IOException;

  /**
   * Shows the basic location information.
   *
   * @throws IOException if there is an issue with I/O operations
   */
  String showBasicLocationInfo() throws IOException;

  List<String> getAllPlayersInfo();

  /**
   * Resets the game state to the initial configuration.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  void resetGameState() throws IOException;

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

  /**
   * Retrieves the current turn number.
   *
   * @return the current turn number
   */
  int getCurrentTurn();


  void movePlayer(int playerIndex, int newPlaceNumber) throws IOException;

  /**
   * Allows the player to pick up an item from the current location.
   *
   * @throws IllegalStateException if the player cannot carry any more items.
   */
  void pickUpItem(String itemName) throws IOException;

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


  String getPlayerCurrentCarriedItems(int playerIndex) throws IOException;


  /**
   * Retrieves the maximum number of turns allowed in the game.
   *
   * @return the maximum number of turns allowed in the game
   */
  int getMaxTurns();
}
