package model.town;

import java.io.IOException;
import java.util.List;
import model.dto.GameStateDTO;
import model.dto.PlaceDTO;
import model.dto.PlayerDTO;
import model.dto.TargetDTO;
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

  void pickUpItem(int itemIndex) throws IOException;

  /**
   * Switches to the next player in the game.
   */
  void switchToNextPlayer() throws IOException;

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

  void movePlayer(int targetPlaceNumber) throws IOException;

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
   * Executes an attack with a specific item.
   *
   * @param player the player performing the attack
   * @param item   the item being used
   * @throws IOException              if there is an error with output
   * @throws IllegalArgumentException if player or item is null
   */
  void executeItemAttack(Player player, Item item) throws IOException;

  AttackResult attackTarget(Integer itemIndex) throws IOException;

  /**
   * Retrieves the maximum number of turns allowed in the game.
   *
   * @return the maximum number of turns allowed in the game
   */
  int getMaxTurns();

  /**
   * Shows information about the current player's state.
   *
   * @throws IOException if there is an error with output
   */
  void showPlayerCurrentInfo() throws IOException;

  /**
   * Gets the current game state as a DTO
   *
   * @return GameStateDTO containing the current game state
   */
  GameStateDTO getGameState();

  /**
   * Gets information about a specific place as a DTO
   *
   * @param placeNumber the number of the place
   * @return PlaceDTO containing place information
   */
  PlaceDTO getPlaceInfo(int placeNumber);

  /**
   * Gets information about the current player as a DTO
   *
   * @return PlayerDTO containing current player information
   */
  PlayerDTO getCurrentPlayerInfo();

  /**
   * Gets information about the target as a DTO
   *
   * @return TargetDTO containing target information
   */
  TargetDTO getTargetInfo();
}
