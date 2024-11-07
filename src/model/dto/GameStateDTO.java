package model.dto;

import java.util.ArrayList;
import java.util.List;

public class GameStateDTO {
  private final List<PlayerDTO> players;
  private final TargetDTO target;
  private final PetDTO pet;
  private final int currentTurn;
  private final int maxTurns;
  private final boolean isGameOver;
  private final int currentPlayerIndex;

  public GameStateDTO(List<PlayerDTO> players, TargetDTO target, PetDTO pet,
                      int currentTurn, int maxTurns, boolean isGameOver,
                      int currentPlayerIndex) {
    this.players = new ArrayList<>(players);
    this.target = target;
    this.pet = pet;
    this.currentTurn = currentTurn;
    this.maxTurns = maxTurns;
    this.isGameOver = isGameOver;
    this.currentPlayerIndex = currentPlayerIndex;
  }

  // Getters
  public List<PlayerDTO> getPlayers() {
    return new ArrayList<>(players);
  }

  public TargetDTO getTarget() {
    return target;
  }

  public PetDTO getPet() {
    return pet;
  }

  public int getCurrentTurn() {
    return currentTurn;
  }

  public int getMaxTurns() {
    return maxTurns;
  }

  public boolean isGameOver() {
    return isGameOver;
  }

  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }
}