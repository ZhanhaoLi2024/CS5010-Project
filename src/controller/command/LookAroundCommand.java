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
   * @param output the output stream to write messages to.
   * @param town   the town where the player is located
   */
  public LookAroundCommand(Appendable output, Town town) {
    this.town = town;
  }

  @Override
  public void execute() throws IOException {
    town.lookAround();
  }
}