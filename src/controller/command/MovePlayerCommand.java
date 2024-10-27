package controller.command;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import model.place.Place;
import model.player.Player;

/**
 * Command to move a player to a neighboring place.
 */
public class MovePlayerCommand implements Command {
  private final Player player;
  private final Appendable output;
  private final Scanner scanner;

  /**
   * Constructs a new MovePlayerCommand.
   *
   * @param player the player to move
   * @param output the output stream to write messages to
   */
  public MovePlayerCommand(Player player, Appendable output, Scanner scanner) {
    this.player = player;
    this.output = output;
    this.scanner = scanner;
  }

  @Override
  public void execute() throws IOException {
    player.moveToNextPlace();
  }

  /**
   * Moves the player to the neighbor at the given index.
   *
   * @param neighborNumber the index of the neighbor to move to
   * @throws IOException if an I/O error occurs
   */
  private void movePlayer(int neighborNumber) throws IOException {
    Place currentPlace = player.getCurrentPlace();
    List<Place> neighbors = currentPlace.getNeighbors();
    if (neighbors.isEmpty()) {
      output.append("No neighbors found.\n");
    } else {
      Place newPlace = neighbors.get(neighborNumber - 1);
      player.moveTo(newPlace);
      output.append("Moved to ").append(newPlace.getName()).append("\n");
    }
  }
}