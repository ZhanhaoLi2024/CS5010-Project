package controller.command;

import java.io.IOException;
import java.util.Scanner;
import model.town.Town;

/**
 * Command to move a player to a neighboring place.
 */
public class MovePlayerCommand implements Command {
  private final Appendable output;
  private final Scanner scanner;
  private Town town;

  /**
   * Constructs a new MovePlayerCommand.
   *
   * @param output  the output stream to write messages to
   * @param scanner the scanner to get user input
   * @param town    the town where the player is located
   */
  public MovePlayerCommand(Town town, Appendable output, Scanner scanner) {
    this.output = output;
    this.scanner = scanner;
    this.town = town;
  }

  @Override
  public void execute() throws IOException {
//    player.moveToNextPlace();
    town.movePlayer();
  }

  /**
   * Moves the player to the neighbor at the given index.
   *
   * @param neighborNumber the index of the neighbor to move to
   * @throws IOException if an I/O error occurs
   */
//  private void movePlayer(int neighborNumber) throws IOException {
//    Place currentPlace = player.getCurrentPlace();
//    List<Place> neighbors = currentPlace.getNeighbors();
//    if (neighbors.isEmpty()) {
//      output.append("No neighbors found.\n");
//    } else {
//      Place newPlace = neighbors.get(neighborNumber - 1);
//      player.moveTo(newPlace);
//      output.append("Moved to ").append(newPlace.getName()).append("\n");
//    }
//  }
}