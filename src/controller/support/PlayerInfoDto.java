package controller.support;

import java.util.List;

/**
 * Data Transfer Object for player information between Controller and View.
 * Used to maintain MVC separation by avoiding direct model class dependencies.
 */
public class PlayerInfoDto {
  private final String playerName;
  private final List<String> items;
  private final int currentTurn;
  private final String currentPlace;
  private final String target;

  /**
   * Constructs a new PlayerInfoDTO.
   *
   * @param playerCurrentTurn  the current turn number
   * @param currentPlayerName  the player's name
   * @param currentItems       the player's items
   * @param playerCurrentPlace the player's current place
   * @param gameTarget         the player's target
   */
  public PlayerInfoDto(int playerCurrentTurn, String currentPlayerName, List<String> currentItems,
                       String playerCurrentPlace, String gameTarget) {
    this.playerName = currentPlayerName;
    this.items = currentItems;
    this.currentTurn = playerCurrentTurn;
    this.currentPlace = playerCurrentPlace;
    this.target = gameTarget;
  }

  /**
   * Get the name of the target character.
   *
   * @return the name of the target character
   */
  public String getTarget() {
    return target;
  }

  /**
   * Get the player's current place.
   *
   * @return the player's current place
   */
  public String getCurrentPlace() {
    return currentPlace;
  }

  /**
   * Get the names of other players.
   *
   * @return the names of other players
   */
  public String getPlayerName() {
    return playerName;
  }

  /**
   * Get the items in the player's current place.
   *
   * @return the items in the player's current place
   */
  public List<String> getItems() {
    if (items.get(0).equals("None")) {
      return null;
    }
    return items;
  }

  /**
   * Get the current turn number.
   *
   * @return the current turn number
   */
  public int getCurrentTurn() {
    return currentTurn;
  }
}