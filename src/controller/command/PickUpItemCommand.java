package controller.command;

import java.io.IOException;
import model.town.Town;

/**
 * Command to allow a player to pick up an item in their current place.
 */
public class PickUpItemCommand implements Command {
  private final Town town;
  private final String itemName;

  /**
   * Constructs a new PickUpItemCommand.
   *
   * @param gameTown the town model
   */
  public PickUpItemCommand(Town gameTown, String pickItemName) {
    this.town = gameTown;
    this.itemName = pickItemName;
  }

  @Override
  public void execute() throws IOException {
    town.pickUpItem(itemName);
  }
}