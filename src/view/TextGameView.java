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
  private final Readable input;
  private final Appendable output;

  /**
   * Constructs a new TextGameView object.
   *
   * @param input  the input stream
   * @param output the output stream
   */
  public TextGameView(Readable input, Appendable output) {
    this.input = input;
    this.output = output;
  }

  @Override
  public void initialize() throws IOException {
    output.append("Welcome to Kill Doctor Lucky Game!\n");
  }

  @Override
  public void showWelcomeScreen() throws IOException {
    output.append("\n=== Kill Doctor Lucky ===\n")
        .append("Created by Zhanhao Li\n")
        .append("CS 5010 Project\n\n");
  }

  @Override
  public void updateMap(List<Place> places, List<Player> players, Target target, Image mapImage) {
    // Placeholder for map update logic
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
    // Placeholder for showing messages
  }

  @Override
  public int getMoveInput(List<Place> neighbors) throws IOException {
    output.append("Available moves:\n");
    for (int i = 0; i < neighbors.size(); i++) {
      output.append(String.valueOf(i + 1)).append(". ")
          .append(neighbors.get(i).getName()).append("\n");
    }
    output.append("Enter your choice (1-").append(String.valueOf(neighbors.size())).append("): ");
    return Integer.parseInt(new Scanner(input).nextLine());
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
    return Integer.parseInt(new Scanner(input).nextLine());
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
    return Integer.parseInt(new Scanner(input).nextLine());
  }

  @Override
  public int displayMainMenu() throws IOException {
    output.append("\nMain Menu:\n")
        .append("1. Show Map Information\n")
        .append("2. Add Human Player\n")
        .append("3. Add Computer Player\n")
        .append("4. Display Player Information\n")
        .append("5. Display Place Information\n")
        .append("6. Quit\n")
        .append("Enter your choice: ");
    return Integer.parseInt(new Scanner(input).nextLine());
  }

  @Override
  public void close() throws IOException {
    output.append("Thank you for playing Kill Doctor Lucky!\n");
  }
}