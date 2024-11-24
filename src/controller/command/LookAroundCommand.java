package controller.command;

import java.io.IOException;
import model.town.Town;

/**
 * Command to allow a player to look around in their current place.
 */
public class LookAroundCommand implements Command {
  private final Town town;

  /**
   * Constructs a new LookAroundCommand.
   *
   * @param gameTown the town model
   */
  public LookAroundCommand(Town gameTown) {
    this.town = gameTown;
  }

  @Override
  public void execute() throws IOException {
    town.lookAround();
  }
}