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
    Place currentPlace = player.getCurrentPlace();
    List<Place> neighbors = currentPlace.getNeighbors();
    if (neighbors.isEmpty()) {
      output.append("No neighbors found.\n");
    } else {
      if (player.isComputerControlled()) {
        int randomNeighborIndex = (int) (Math.random() * neighbors.size());
        this.movePlayer(randomNeighborIndex + 1);
      } else {
        output.append("Neighbors of ").append(currentPlace.getName()).append(":\n");
        for (int i = 0; i < neighbors.size(); i++) {
          int currentIndex = i + 1;
          output.append(String.valueOf(currentIndex)).append(". ")
              .append(neighbors.get(i).getName())
              .append("\n");
        }
        output.append("Enter the neighbor number to move to:\n");
        String userInput = scanner.nextLine(); // 通过 scanner 获取输入
        try {
          int neighborNumber = Integer.parseInt(userInput);
          if (neighborNumber < 1 || neighborNumber > neighbors.size()) {
            output.append("Invalid neighbor number.\n");
          } else {
            this.movePlayer(neighborNumber);
          }
        } catch (NumberFormatException e) {
          output.append("Invalid input. Please enter a number.\n");
        }
      }
    }
  }

  /**
   * Moves the player to the neighbor at the given index.
   *
   * @param neighborNumber the index of the neighbor to move to
   * @throws IOException if an I/O error occurs
   */
  public void movePlayer(int neighborNumber) throws IOException {
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