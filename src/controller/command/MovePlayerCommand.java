package controller.command;

import java.io.IOException;
import model.town.Town;

/**
 * Refactored MovePlayerCommand implementing the Command interface
 */
public class MovePlayerCommand implements Command {
  private final int targetPlaceNumber;

  public MovePlayerCommand(int targetPlaceNumber) {
    this.targetPlaceNumber = targetPlaceNumber;
  }

  @Override
  public void execute(Town town) throws IOException {
    town.movePlayer(targetPlaceNumber);
  }
}