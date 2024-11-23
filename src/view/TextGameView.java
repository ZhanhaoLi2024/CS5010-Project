package view;

import java.awt.Image;
import java.io.IOException;
import java.util.List;
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
    return 0; // Placeholder for move input logic
  }

  @Override
  public int getPickupInput(List<Item> items) throws IOException {
    return 0; // Placeholder for pickup input logic
  }

  @Override
  public int getAttackInput(List<Item> carriedItems) throws IOException {
    return 0; // Placeholder for attack input logic
  }

  @Override
  public int displayMainMenu() throws IOException {
    return 0; // Placeholder for main menu logic
  }

  @Override
  public void close() throws IOException {
    // Placeholder for close logic
  }
}