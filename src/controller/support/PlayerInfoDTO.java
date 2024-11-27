package controller.support;

import java.util.List;

/**
 * Data Transfer Object for player information between Controller and View.
 * Used to maintain MVC separation by avoiding direct model class dependencies.
 */
public class PlayerInfoDTO {
  private final String playerName;
  private final List<String> items;
  private final int currentTurn;
  private final String currentPlace;
  private final String target;
  private final List<String> otherPlayers;
  private final List<String> currentPlaceItems;
  private final String petInfo;

  public PlayerInfoDTO(int currentTurn, String playerName, List<String> items,
                       String currentPlace, String target, List<String> otherPlayers,
                       List<String> currentPlaceItems, String petInfo) {
    this.playerName = playerName;
    this.items = items;
    this.currentTurn = currentTurn;
    this.currentPlace = currentPlace;
    this.target = target;
    this.otherPlayers = otherPlayers;
    this.currentPlaceItems = currentPlaceItems;
    this.petInfo = petInfo;
  }

  public String getPetInfo() {
    return petInfo;
  }

  public List<String> getCurrentPlaceItems() {
    return currentPlaceItems;
  }

  public List<String> getOtherPlayers() {
    return otherPlayers;
  }

  public String getTarget() {
    return target;
  }

  public String getCurrentPlace() {
    return currentPlace;
  }

  public String getPlayerName() {
    return playerName;
  }

  public List<String> getItems() {
    if (items.get(0).equals("None")) {
      return null;
    }
    return items;
  }

  public int getCurrentTurn() {
    return currentTurn;
  }
}