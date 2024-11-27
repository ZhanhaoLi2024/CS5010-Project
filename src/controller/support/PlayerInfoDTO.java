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

  public PlayerInfoDTO(String playerName, List<String> items, int currentTurn) {
    this.playerName = playerName;
    this.items = items;
    this.currentTurn = currentTurn;
  }

  public String getPlayerName() {
    return playerName;
  }

  public List<String> getItems() {
    return items;
  }

  public int getCurrentTurn() {
    return currentTurn;
  }
}