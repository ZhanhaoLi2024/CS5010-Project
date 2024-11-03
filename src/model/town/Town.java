package model.town;

import java.io.IOException;
import java.util.List;
import model.item.Item;
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
  void getPlaceInfo(Place place);

  /**
   * Moves the character to the next place.
   */
  void moveCharacter();

  /**
   * Retrieves the character in the town.
   *
   * @return the character in the town
   */
  Target getCharacter();

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
  void showAllPlayersInfo() throws IOException;

  /**
   * Shows information about a specific player.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  void showSpecificPlayerInfo(String playerName) throws IOException;

  /**
   * Allows the player to look around the current place.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  void lookAround(Player player) throws IOException;

  /**
   * Switches to the next player in the game.
   */
  void switchToNextPlayer();
}
