package controller.command;

import java.io.IOException;
import model.town.Town;

/**
 * Command to allow a player to pick up an item in their current place.
 */
public class PickUpItemCommand implements Command {
  private final Town town;

  /**
   * Constructs a new PickUpItemCommand.
   *
   * @param gameTown the town where the player is located.
   */
  public PickUpItemCommand(Town gameTown) {
    this.town = gameTown;
  }

  @Override
  public void execute() throws IOException {
    town.pickUpItem();
  }
}