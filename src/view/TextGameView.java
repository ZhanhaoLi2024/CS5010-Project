package view;

import java.awt.Image;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import model.item.Item;
import model.place.Place;
import model.player.Player;
import model.target.Target;

/**
 * Implementation of GameView for text-based interface.
 */
public class TextGameView implements GameView {
  private static Appendable output;
  private static Scanner scanner;
  private final Readable input;

  /**
   * Constructs a TextGameView object.
   *
   * @param gameInput  Readable input source
   * @param gameOutput Appendable output destination
   */
  public TextGameView(Readable gameInput, Appendable gameOutput) {
    this.input = gameInput;
    output = gameOutput;
    scanner = new Scanner(input);
  }

  /**
   * Shows the player name input prompt and returns the user's input.
   *
   * @return the player name entered by the user
   * @throws IOException if there is an error reading input
   */
  public static String showAddNewPlayerName() throws IOException {
    while (true) {
      output.append("Enter the player's name:\n");
      String playerName = scanner.nextLine().trim();

      if (playerName.isEmpty()) {
        output.append("Name cannot be empty. Please try again.\n");
        continue;
      }

      return playerName;
    }
  }

  /**
   * Shows the starting place input prompt and returns the user's input.
   *
   * @return the starting place entered by the user
   * @throws IOException if there is an error reading input
   */
  public static int showAddNewPlayerStartingPlaceNumber() throws IOException {
    while (true) {
      try {
        output.append("Enter the place number:\n");
        int placeNumber = Integer.parseInt(scanner.nextLine().trim());

        if (placeNumber < 0) {
          output.append("Place number cannot be negative. Please try again.\n");
          continue;
        }

        if (placeNumber > 20) {
          output.append("Place number cannot be greater than 20. Please try again.\n");
          continue;
        }

        return placeNumber;
      } catch (NumberFormatException e) {
        output.append("Invalid input. Please enter a valid number.\n");
      }
    }
  }

  /**
   * Shows the carry limit input prompt and returns the user's input.
   *
   * @return the carry limit entered by the user
   * @throws IOException if there is an error reading input
   */
  public static int showNewPlayerCarryLimit() throws IOException {
    while (true) {
      try {
        output.append("Enter the carry limit (1-5):\n");
        int carryLimit = Integer.parseInt(scanner.nextLine().trim());

        if (carryLimit < 1 || carryLimit > 5) {
          output.append("Carry limit must be between 1 and 5. Please try again.\n");
          continue;
        }

        return carryLimit;
      } catch (NumberFormatException e) {
        output.append("Invalid input. Please enter a valid number.\n");
      }
    }
  }

  @Override
  public void initialize() throws IOException {
    output.append("\n");
    output.append("++++++++++++++++++++\n");
    output.append("Welcome to the game!\n");
  }

  @Override
  public void showWelcomeScreen() throws IOException {
    output.append("\n=== Kill Doctor Lucky ===\n")
        .append("Created by Zhanhao Li\n")
        .append("CS 5010 Project\n\n");
  }

  @Override
  public void updateMap(List<Place> places, List<Player> players, Target target, Image mapImage) {
    // Text view doesn't use graphical map
  }

  @Override
  public void updatePlayerInfo(Player player, Place currentPlace) throws IOException {
    output.append("\nCurrent Player: ").append(player.getName())
        .append("\nLocation: ").append(currentPlace.getName())
        .append("\nItems carried: ").append(String.valueOf(player.getCurrentCarriedItems().size()))
        .append("/").append(String.valueOf(player.getCarryLimit()))
        .append("\n");
  }

  @Override
  public void showMessage(String message) throws IOException {
    output.append(message).append("\n");
  }

  @Override
  public int getMoveInput(List<Place> neighbors) throws IOException {
    output.append("Available moves:\n");
    for (int i = 0; i < neighbors.size(); i++) {
      output.append(String.valueOf(i + 1)).append(". ")
          .append(neighbors.get(i).getName()).append("\n");
    }
    output.append("Enter your choice (1-").append(String.valueOf(neighbors.size())).append("): ");
    return Integer.parseInt(scanner.nextLine());
  }

  @Override
  public int getPickupInput(List<Item> items) throws IOException {
    if (items.isEmpty()) {
      output.append("No items available to pick up.\n");
      return -1;
    }

    output.append("Available items:\n");
    for (int i = 0; i < items.size(); i++) {
      output.append(String.valueOf(i + 1)).append(". ")
          .append(items.get(i).getName())
          .append(" (Damage: ").append(String.valueOf(items.get(i).getDamage())).append(")\n");
    }
    output.append("Enter item number to pick up: ");
    return Integer.parseInt(scanner.nextLine());
  }

  @Override
  public int getAttackInput(List<Item> carriedItems) throws IOException {
    output.append("\nChoose your attack:\n");
    output.append("0. Poke in the eye (1 damage)\n");
    for (int i = 0; i < carriedItems.size(); i++) {
      Item item = carriedItems.get(i);
      output.append(String.valueOf(i + 1)).append(". Use ")
          .append(item.getName())
          .append(" (").append(String.valueOf(item.getDamage())).append(" damage)\n");
    }
    output.append("Enter your choice: ");
    return Integer.parseInt(scanner.nextLine());
  }

  @Override
  public int displayMainMenu() throws IOException {
    output.append("Please choose an option:\n");
    output.append("1. Show the Map Information\n");
    output.append("2. Add the Human-controller player\n");
    output.append("3. Add the Computer-controller player\n");
    output.append("4. Display the player's information\n");
    output.append("5. Display the Place's information\n");
    output.append("6. Start the game\n");
    output.append("7. Print the map\n");
    output.append("0. Exit\n");

    try {
      return Integer.parseInt(scanner.nextLine());
    } catch (NumberFormatException e) {
      output.append("Invalid input. Please enter a number.\n");
      return -1;
    }
  }

  @Override
  public void close() throws IOException {
    output.append("Exiting the game...\n");
  }

  /**
   * Gets generic user input as string.
   * A wrapper around scanner.nextLine() that handles possible errors.
   *
   * @return user input string
   * @throws IOException if there is an error reading input
   */
  @Override
  public String getUserInput() throws IOException {
    String userInput = scanner.nextLine().trim();
    if (userInput.isEmpty()) {
      output.append("Input cannot be empty. Please try again.\n");
      return getUserInput();
    }
    return userInput;
  }

  /**
   * Gets numeric input from user.
   * Handles format checking and validation.
   *
   * @return numeric value entered by user
   * @throws IOException if there is an error reading input
   */
  @Override
  public int getNumberInput() throws IOException {
    try {
      String userInput = scanner.nextLine().trim();
      if (userInput.isEmpty()) {
        output.append("Input cannot be empty. Please enter a number.\n");
        return getNumberInput();
      }
      return Integer.parseInt(userInput);
    } catch (NumberFormatException e) {
      output.append("Invalid input. Please enter a valid number.\n");
      return getNumberInput();
    }
  }
}