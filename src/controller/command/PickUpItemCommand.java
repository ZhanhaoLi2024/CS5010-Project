package controller.command;

import java.io.IOException;
import model.town.Town;

/**
 * Refactored PickUpItemCommand implementing the Command interface
 */
public class PickUpItemCommand implements Command {
  private final int itemIndex;

  public PickUpItemCommand(int itemIndex) {
    this.itemIndex = itemIndex;
  }

  @Override
  public void execute(Town town) throws IOException {
    town.pickUpItem(itemIndex);
  }
}