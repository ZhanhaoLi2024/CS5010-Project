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
   * @param gameTown the town model
   */
  public MovePlayerCommand(Town gameTown) {
    this.town = gameTown;
  }

  @Override
  public void execute() throws IOException {
    town.movePlayer();
  }
}