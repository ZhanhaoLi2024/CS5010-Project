package controller.command;

import java.io.IOException;
import model.town.Town;

/**
 * Command to move a player to a neighboring place.
 */
public class MovePlayerCommand implements Command {
  private final Town town;

  /**
   * Constructs a new MovePlayerCommand.
   *
   * @param gameTown the town where the player is located
   */
  public MovePlayerCommand(Town gameTown) {
    this.town = gameTown;
  }

  @Override
  public void execute() throws IOException {
    town.movePlayer();
  }
}