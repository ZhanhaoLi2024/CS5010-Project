package controller.command;

import java.io.IOException;
import model.town.Town;

/**
 * Refactored MovePetCommand implementing the Command interface
 */
public class MovePetCommand implements Command {
  private final int targetPlaceNumber;

  public MovePetCommand(int targetPlaceNumber) {
    this.targetPlaceNumber = targetPlaceNumber;
  }

  @Override
  public void execute(Town town) throws IOException {
    town.movePet(targetPlaceNumber);
  }
}