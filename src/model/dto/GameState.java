package model.dto;

import java.util.ArrayList;
import java.util.List;
import model.pet.Pet;
import model.player.Player;
import model.target.Target;

public class GameState {
  private final List<Player> players;
  private final Target target;
  private final Pet pet;
  private final int currentTurn;
  private final int maxTurns;
  private final boolean isGameOver;
  private final int currentPlayerIndex;

  public GameState(List<Player> players, Target target, Pet pet,
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
  public List<Player> getPlayers() {
    return new ArrayList<>(players);
  }

  public Target getTarget() {
    return target;
  }

  public Pet getPet() {
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