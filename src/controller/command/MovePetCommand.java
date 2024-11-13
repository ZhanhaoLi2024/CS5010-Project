package controller.command;

import java.io.IOException;
import java.util.Scanner;
import model.place.Place;
import model.town.Town;

/**
 * Command to handle a player's attempt to move the pet to a different place.
 * Implements the Command interface as part of the Command pattern.
 */
public class MovePetCommand implements Command {
  private final Town town;
  private final Appendable output;
  private final Scanner scanner;

  /**
   * Constructs a new MovePetCommand.
   *
   * @param gameTown    the town where the pet is located
   * @param gameOutput  the output stream to write messages to
   * @param gameScanner the scanner to get user input
   */
  public MovePetCommand(Town gameTown, Appendable gameOutput, Scanner gameScanner) {
    this.town = gameTown;
    this.output = gameOutput;
    this.scanner = gameScanner;
  }

  @Override
  public void execute() throws IOException {
    // Display all available places
    output.append("\nAvailable places to move the pet:\n");
    for (int i = 0; i < town.getPlaces().size(); i++) {
      Place place = town.getPlaces().get(i);
      output.append(String.valueOf(i + 1))
          .append(". ")
          .append(place.getName())
          .append("\n");
    }

    // Get user input for the destination
    output.append("Enter the number of the place to move the pet to (or 0 to cancel):\n");
    try {
      int placeNumber = Integer.parseInt(scanner.nextLine());

      if (placeNumber == 0) {
        output.append("Pet movement cancelled.\n");
        return;
      }

      if (placeNumber < 1 || placeNumber > town.getPlaces().size()) {
        output.append("Invalid place number.\n");
        return;
      }

      // Move the pet
      town.movePet(placeNumber);

      // This represents a turn, so switch to next player
      town.switchToNextPlayer();

    } catch (NumberFormatException e) {
      output.append("Invalid input. Please enter a number.\n");
    } catch (IllegalArgumentException e) {
      output.append("Error: ").append(e.getMessage()).append("\n");
    }
  }
}