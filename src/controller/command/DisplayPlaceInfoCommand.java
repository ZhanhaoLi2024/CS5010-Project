package controller.command;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import model.item.Item;
import model.place.Place;
import model.player.Player;
import model.town.Town;

/**
 * Command to display information about all places or a specific place.
 */
public class DisplayPlaceInfoCommand implements Command {
  private final Town town;
  private final Appendable output;
  private final Scanner scanner;
  private List<Player> players;

  /**
   * Constructs a new DisplayPlaceInfoCommand.
   *
   * @param town    the town to display information about
   * @param output  the output stream to write messages to
   * @param scanner the scanner to get user input
   */
  public DisplayPlaceInfoCommand(Town town, Appendable output,
                                 Scanner scanner) {
    this.town = town;
    this.output = output;
    this.scanner = scanner;
    this.players = players;
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
          showAllPlacesInfo();
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
   * Shows information about all places.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  private void showAllPlacesInfo() throws IOException {
    List<Place> places = town.getPlaces();
    if (places.isEmpty()) {
      output.append("No places found.\n");
      return;
    }
    output.append("All places info:\n");
    int index = 1;
    for (Place place : places) {
      output.append("----------\n");
      output.append("Place Number: " + place.getPlaceNumber() + ". Place name: ")
          .append(place.getName()).append("\n");
      output.append("----------\n");
      output.append("Place items:\n");
      for (Item item : place.getItems()) {
        output.append("Item name: ").append(item.getName()).append("\n");
        output.append("Item damage: ").append(String.valueOf(item.getDamage())).append("\n");
      }
      output.append("----------\n");
      output.append("Place neighbors:\n");
      for (Place neighbor : place.getNeighbors()) {
        output.append("Neighbor name: ").append(neighbor.getName()).append("\n");
      }
      output.append("----------\n");
      if (players.isEmpty()) {
        output.append("No players in this place.\n");
      } else {
        for (Player player : players) {
          if (player.getCurrentPlace().equals(place)) {
            output.append(player.getName()).append(" is in this place.\n");
          }
        }
      }
      output.append("----------\n");
      index++;
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
    Place place = town.getPlaces().stream()
        .filter(p -> p.getName().equals(placeName))
        .findFirst()
        .orElse(null);

    if (place != null) {
      output.append("----------\n");
      output.append("Place name: ").append(place.getName()).append("\n");
      output.append("----------\n");
      output.append("Place items:\n");
      output.append("----------\n");
      for (Item item : place.getItems()) {
        output.append("Item name: ").append(item.getName()).append("\n");
        output.append("Item damage: ").append(String.valueOf(item.getDamage())).append("\n");
      }
      output.append("----------\n");
      output.append("Place neighbors:\n");
      for (Place neighbor : place.getNeighbors()) {
        output.append("Neighbor name: ").append(neighbor.getName()).append("\n");
      }
      output.append("----------\n");
      if (players.isEmpty()) {
        output.append("No players in this place.\n");
      } else {
        for (Player player : players) {
          if (player.getCurrentPlace().equals(place)) {
            output.append(player.getName()).append(" is in this place.\n");
          }
        }
      }
      output.append("----------\n");
    } else {
      output.append("Place not found.\n");
    }
  }
}