package controller.command;

import java.io.IOException;
import model.town.Town;

/**
 * Command to move a player to a neighboring place.
 */
public class MovePlayerCommand implements Command {
  private final Town town;
  private final int placeNumber;
  private final int playerIndex;

  /**
   * Constructs a new MovePlayerCommand.
   *
   * @param gameTown the town model
   */
  public MovePlayerCommand(Town gameTown, int currentPlayerIndex, int newPlaceNumber) {
    this.town = gameTown;
    this.placeNumber = newPlaceNumber;
    this.playerIndex = currentPlayerIndex;
  }

  @Override
  public boolean execute() throws IOException {
    town.movePlayer(playerIndex, placeNumber);
    return true;
  }
}