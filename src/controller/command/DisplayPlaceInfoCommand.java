package controller.command;

import java.io.IOException;
import model.town.Town;
import view.GameView;

/**
 * Command to display information about all places or a specific place.
 */
public class DisplayPlaceInfoCommand implements Command {
  private final Town town;
  private final GameView view;

  /**
   * Constructs a new DisplayPlaceInfoCommand.
   *
   * @param gameTown the town model
   * @param gameView the game view
   */
  public DisplayPlaceInfoCommand(Town gameTown, GameView gameView) {
    this.town = gameTown;
    this.view = gameView;
  }

  @Override
  public void execute() throws IOException {
    showThePlaceInfo();
  }

  private void showThePlaceInfo() throws IOException {
    boolean showPlaceInfo = true;
    while (showPlaceInfo) {
      view.showMessage("Please choose an option:\n" +
          "1. Show All Places Info\n" +
          "2. Show Specific Place Info\n" +
          "0. Exit");

//      int choice = Integer.parseInt(scanner.nextLine());
//
//      switch (choice) {
//        case 1:
//          town.showAllPlacesInfo();
//          break;
//        case 2:
//          showSpecificPlaceInfo();
//          break;
//        case 0:
//          view.showMessage("Exiting...");
//          showPlaceInfo = false;
//          break;
//        default:
//          view.showMessage("Invalid choice, please try again.");
//      }
    }
  }

  private void showSpecificPlaceInfo() throws IOException {
    view.showMessage("Enter the place name:");
    String placeName = view.getStringInput();
    town.getPlaceByName(placeName);
  }
}