package model.town;

import java.io.IOException;
import java.util.List;
import model.place.Place;
import model.player.Player;
import model.target.Target;

/**
 * The Town interface represents the game world model in the Doctor Lucky game.
 * It encapsulates all game state and game logic, including players, places, items,
 * the target character, and the pet. This interface follows the Model component
 * of the MVC pattern, providing methods to query and modify the game state while
 * maintaining encapsulation.
 */
public interface Town {

  /**
   * Gets the current information about the pet in the game, including its name
   * and current location.
   *
   * @return a string containing the pet's name and current location, formatted as "name,location"
   */
  String petCurrentInfo();

  /**
   * Retrieves a specific place in the town by its number.
   *
   * @param placeNumber the number identifier of the place to retrieve
   * @return the Place object corresponding to the given number
   * @throws IllegalArgumentException if the place number is invalid
   */
  Place getPlaceByNumber(int placeNumber);

  /**
   * Moves the target character to their next location following a predetermined path.
   * The target moves to spaces in sequential order based on their index.
   */
  void moveTarget();

  /**
   * Retrieves the target character object.
   *
   * @return the Target object representing the game's target character
   */
  Target getTarget();

  /**
   * Moves the pet to a specified location in the town.
   *
   * @param placeNumber the number of the place to move the pet to
   * @throws IllegalArgumentException if the place number is invalid
   * @throws IOException              if there is an error in the operation
   */
  void movePet(int placeNumber) throws IOException;

  /**
   * Gets all places in the town.
   *
   * @return a List of all Place objects in the town
   */
  List<Place> getPlaces();

  /**
   * Gets the name of the target character.
   *
   * @return the name of the target character
   */
  String getTargetName();

  /**
   * Gets the current health points of the target character.
   *
   * @return the current health points of the target
   */
  int getTargetHealth();

  /**
   * Gets all players currently in the game.
   *
   * @return a List of all Player objects in the game
   */
  List<Player> getPlayers();

  /**
   * Gets the current place number for a specific player.
   *
   * @param playerIndex the index of the player in the players list
   * @return the place number where the specified player is currently located
   * @throws IllegalArgumentException if the player index is invalid
   */
  Integer getPlayerCurrPlaceNumber(int playerIndex);

  /**
   * Gets detailed information about a specific place.
   *
   * @param placeNumber the number of the place to get information about
   * @return a string containing the place's information in format "name;items;players"
   * @throws IOException if there is an error retrieving the information
   */
  String getCurrentPlaceInfo(int placeNumber) throws IOException;

  /**
   * Gets information about the neighboring places of a specific location.
   *
   * @param placeNumber the number of the place to get neighbors for
   * @return a string containing information about all neighboring places
   * @throws IOException if there is an error retrieving the information
   */
  String getCurrentPlaceNeighborsInfo(int placeNumber) throws IOException;

  /**
   * Adds a new player to the game.
   *
   * @param newPlayerName       the name of the new player
   * @param newPlayerPlace      the starting place number for the new player
   * @param newPlayerCarryLimit the maximum number of items the player can carry
   * @param isComputerPlayer    true if the player is computer-controlled, false otherwise
   * @throws IllegalArgumentException if any of the parameters are invalid
   */
  void addPlayer(String newPlayerName, int newPlayerPlace, int newPlayerCarryLimit,
                 boolean isComputerPlayer);

  /**
   * Gets information about a specific player by their name.
   *
   * @param playerName the name of the player to get information about
   * @return a string containing the player's information
   * @throws IOException if there is an error retrieving the information
   */
  String getPlayerByName(String playerName) throws IOException;

  /**
   * Executes the look around action for the current player.
   * This action allows a player to see information about their current location
   * and neighboring spaces.
   *
   * @throws IOException if there is an error executing the action
   */
  void lookAround() throws IOException;

  /**
   * Attempts to attack the target character using a specified item.
   *
   * @param attackItemName the name of the item to use in the attack
   * @return true if the attack succeeded in defeating the target, false otherwise
   * @throws IOException if there is an error executing the attack
   */
  boolean attackTarget(String attackItemName) throws IOException;

  /**
   * Advances the game to the next player's turn.
   *
   * @throws IOException if there is an error switching players
   */
  void switchToNextPlayer() throws IOException;

  /**
   * Gets basic location information about the current game state.
   *
   * @return a string containing basic location information
   * @throws IOException if there is an error retrieving the information
   */
  String showBasicLocationInfo() throws IOException;

  /**
   * Gets information about all players in the game.
   *
   * @return a List of strings containing information about all players
   */
  List<String> getAllPlayersInfo();

  /**
   * Resets the game state to its initial configuration.
   *
   * @throws IOException if there is an error resetting the game state
   */
  void resetGameState() throws IOException;

  /**
   * Checks if the game has ended.
   *
   * @return true if the game is over, false otherwise
   */
  boolean isGameOver();

  /**
   * Checks if the current player is computer-controlled.
   *
   * @return true if the current player is computer-controlled, false otherwise
   */
  Boolean isComputerControllerPlayer();

  /**
   * Gets the current turn number.
   *
   * @return the current turn number
   */
  int getCurrentTurn();

  /**
   * Moves a specified player to a new location.
   *
   * @param playerIndex    the index of the player to move
   * @param newPlaceNumber the number of the place to move to
   * @throws IOException              if there is an error executing the move
   * @throws IllegalArgumentException if the player index or place number is invalid
   */
  void movePlayer(int playerIndex, int newPlaceNumber) throws IOException;

  /**
   * Allows the current player to pick up a specified item from their current location.
   *
   * @param itemName the name of the item to pick up
   * @throws IOException           if there is an error executing the action
   * @throws IllegalStateException if the player cannot carry any more items
   */
  void pickUpItem(String itemName) throws IOException;

  /**
   * Gets the index of the current player.
   *
   * @return the index of the current player in the players list
   */
  int getCurrentPlayerIndex();

  /**
   * Checks if a specific player is visible to other players.
   * A player is visible if they are in the same space as another player
   * or in a neighboring space that isn't occupied by the pet.
   *
   * @param player the player to check visibility for
   * @return true if the player is visible to others, false otherwise
   * @throws IllegalArgumentException if the player is null
   */
  boolean isPlayerVisible(Player player);

  /**
   * Gets a string representation of the items currently carried by a player.
   *
   * @param playerIndex the index of the player
   * @return a string representation of the player's carried items
   * @throws IOException if there is an error retrieving the information
   */
  String getPlayerCurrentCarriedItems(int playerIndex) throws IOException;

  /**
   * Gets the maximum number of turns allowed in the game.
   *
   * @return the maximum number of turns
   */
  int getMaxTurns();
}