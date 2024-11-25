package view;

import controller.Controller;
import java.io.IOException;
import java.util.Scanner;
import model.place.Place;
import model.player.Player;

/**
 * Implementation of GameView for text-based interface.
 */
public class TextGameView implements GameView {
  private static Appendable output;
  private static Scanner scanner;
  private final Readable input;
  private final Controller controller;

  /**
   * Constructs a TextGameView object.
   *
   * @param gameInput  Readable input source
   * @param gameOutput Appendable output destination
   */
  public TextGameView(Controller gameController, Readable gameInput, Appendable gameOutput) {
    this.input = gameInput;
    output = gameOutput;
    scanner = new Scanner(input);
    this.controller = gameController;
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
  public String getStringInput() throws IOException {
    String userInput = scanner.nextLine();
    if (userInput.isEmpty()) {
      output.append("Input cannot be empty. Please try again.\n");
      return getStringInput();
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
      String userInput = scanner.nextLine();
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