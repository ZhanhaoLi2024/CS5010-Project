package controller.command;

import java.io.IOException;
import java.util.Scanner;
import model.town.Town;

/**
 * Command to display information about all places or a specific place.
 */
public class DisplayPlaceInfoCommand implements Command {
  private final Town town;
  private final Appendable output;
  private final Scanner scanner;

  /**
   * Constructs a new DisplayPlaceInfoCommand.
   *
   * @param gameTown    the town to display information about
   * @param gameOutput  the output stream to write messages to
   * @param gameScanner the scanner to get user input
   */
  public DisplayPlaceInfoCommand(Town gameTown, Appendable gameOutput,
                                 Scanner gameScanner) {
    this.town = gameTown;
    this.output = gameOutput;
    this.scanner = gameScanner;
  }

  @Override
  public void execute() throws IOException {
    showThePlaceInfo();
  }

  /**
   * Shows the place information.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  private void showThePlaceInfo() throws IOException {
    boolean showPlaceInfo = true;
    while (showPlaceInfo) {
      output.append("Please choose an option:\n");
      output.append("1. Show All Places Info\n");
      output.append("2. Show Specific Place Info\n");
      output.append("0. Exit\n");

      int choice = 0;
      try {
        choice = Integer.parseInt(scanner.nextLine());
      } catch (NumberFormatException e) {
        output.append("Invalid input. Please enter a number.\n");
      }

      switch (choice) {
        case 1:
          town.showAllPlacesInfo();
          break;
        case 2:
          showSpecificPlaceInfo();
          break;
        case 0:
          output.append("Exiting...\n");
          showPlaceInfo = false;
          break;
        default:
          output.append("Invalid choice, please try again.\n");
      }
    }
  }

  /**
   * Shows information about a specific place.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  private void showSpecificPlaceInfo() throws IOException {
    output.append("Enter the place name:\n");
    String placeName = scanner.nextLine();
    town.getPlaceByName(placeName);
  }
}