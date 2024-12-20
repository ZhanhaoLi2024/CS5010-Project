package controller.command;

import java.io.IOException;
import model.town.Town;

/**
 * Command to handle a player's attempt to move the pet to a different place.
 * Implements the Command interface as part of the Command pattern.
 */
public class MovePetCommand implements Command {
  private final Town town;
  private final int placeNumber;

  /**
   * Constructs a new MovePetCommand.
   *
   * @param gameTown          the town model
   * @param petNewPlaceNumber the number of the place to move the pet to
   */
  public MovePetCommand(Town gameTown, int petNewPlaceNumber) {
    this.town = gameTown;
    this.placeNumber = petNewPlaceNumber;
  }

  @Override
  public boolean execute() throws IOException {
    town.movePet(placeNumber);
    return true;
  }
}