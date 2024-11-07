package view;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * ConsoleGameView implements the GameView interface for console-based interaction.
 * It handles all input/output through the console without direct model dependencies.
 */
public class ConsoleGameView implements GameView {
  private final Scanner scanner;
  private final Appendable output;

  /**
   * Constructs a new ConsoleGameView with the specified input and output sources.
   *
   * @param in  the input source
   * @param out the output destination
   */
  public ConsoleGameView(Readable in, Appendable out) {
    this.scanner = new Scanner(in);
    this.output = out;
  }

  @Override
  public void displayMainMenu() throws IOException {
    output.append("\nPlease choose an option:\n")
        .append("1. Show the Map Information\n")
        .append("2. Add the player\n")
        .append("3. Display the player's information\n")
        .append("4. Display the Place's information\n")
        .append("5. Start the game\n")
        .append("6. Print the map\n")
        .append("0. Exit\n");
  }

  @Override
  public void displayMapInfo(String worldName, String targetName,
                             int targetHealth, List<String> placesInfo) throws IOException {
    output.append("=== Map Information ===\n")
        .append("World: ").append(worldName).append("\n")
        .append("Target: ").append(targetName)
        .append(" (Health: ").append(String.valueOf(targetHealth)).append(")\n")
        .append("\nPlaces in the world:\n")
        .append("--------------------\n");

    for (String info : placesInfo) {
      output.append(info).append("\n");
    }
    output.append("--------------------\n");
  }

  @Override
  public void displayPlayerInfo(List<String> playerInfo) throws IOException {
    output.append("=== Player Information ===\n");
    for (String info : playerInfo) {
      output.append(info).append("\n");
    }
    output.append("--------------------\n");
  }

  @Override
  public void displayPlaceInfo(List<String> placeInfo) throws IOException {
    output.append("=== Place Information ===\n");
    for (String info : placeInfo) {
      output.append(info).append("\n");
    }
    output.append("--------------------\n");
  }

  @Override
  public void displayGameState(int currentTurn, int maxTurns,
                               List<String> currentPlayerInfo) throws IOException {
    output.append("\nTurn ").append(String.valueOf(currentTurn))
        .append(" of ").append(String.valueOf(maxTurns)).append("\n\n");

    for (String info : currentPlayerInfo) {
      output.append(info).append("\n");
    }
  }

  @Override
  public void displayMoveOptions(List<String> neighbors) throws IOException {
    output.append("\nAvailable moves:\n");
    for (int i = 0; i < neighbors.size(); i++) {
      output.append(String.valueOf(i + 1)).append(". ")
          .append(neighbors.get(i)).append("\n");
    }
    output.append("Enter the number of your choice:\n");
  }

  @Override
  public void displayItems(List<String> items) throws IOException {
    output.append("\nAvailable items:\n");
    for (int i = 0; i < items.size(); i++) {
      output.append(String.valueOf(i + 1)).append(". ")
          .append(items.get(i)).append("\n");
    }
    output.append("Enter the number of the item to pick up:\n");
  }

  @Override
  public void displayLookAroundInfo(List<String> lookAroundInfo) throws IOException {
    output.append("\n=== Looking Around ===\n");
    for (String info : lookAroundInfo) {
      output.append(info).append("\n");
    }
    output.append("--------------------\n");
  }

  @Override
  public void displayAttackInfo(List<String> attackInfo) throws IOException {
    output.append("\n=== Attack Options ===\n");
    for (String info : attackInfo) {
      output.append(info).append("\n");
    }
    output.append("--------------------\n");
  }

  @Override
  public void displayPetInfo(List<String> petInfo) throws IOException {
    output.append("\n=== Pet Movement Options ===\n");
    for (String info : petInfo) {
      output.append(info).append("\n");
    }
    output.append("--------------------\n");
  }

  @Override
  public void displayMessage(String message) throws IOException {
    output.append(message).append("\n");
  }

  @Override
  public String getInput() throws IOException {
    return scanner.nextLine();
  }

  @Override
  public void close() throws IOException {
    scanner.close();
  }
}