package controller.command;

import java.io.IOException;
import java.util.List;
import model.place.Place;
import model.town.Town;
import view.GameView;

/**
 * Command to handle a player's attempt to move the pet to a different place.
 * Implements the Command interface as part of the Command pattern.
 */
public class MovePetCommand implements Command {
  private final Town town;
  private final GameView view;

  /**
   * Constructs a new MovePetCommand.
   *
   * @param gameTown the town model
   * @param gameView the game view
   */
  public MovePetCommand(Town gameTown, GameView gameView) {
    this.town = gameTown;
    this.view = gameView;
  }

  @Override
  public void execute() throws IOException {
    view.showMessage("\nAvailable places to move the pet:");
    List<Place> places = town.getPlaces();
    for (int i = 0; i < places.size(); i++) {
      Place place = places.get(i);
      view.showMessage((i + 1) + ". " + place.getName());
    }

    view.showMessage("Enter the number of the place to move the pet to (or 0 to cancel):");
    int placeNumber = view.getNumberInput();

    if (placeNumber == 0) {
      view.showMessage("Pet movement cancelled.");
      return;
    }

    if (placeNumber < 1 || placeNumber > places.size()) {
      view.showMessage("Invalid place number.");
      return;
    }

    town.movePet(placeNumber);
    town.switchToNextPlayer();
  }
}