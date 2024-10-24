package controller;

import controller.command.AddHumanPlayerCommand;
import controller.command.DisplayPlaceInfoCommand;
import controller.command.DisplayPlayerInfoCommand;
import controller.command.LookAroundCommand;
import controller.command.MovePlayerCommand;
import controller.command.PickUpItemCommand;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.imageio.ImageIO;
import model.item.Item;
import model.place.Place;
import model.player.Player;
import model.town.Town;

/**
 * The game controller responsible for handling user input and game commands.
 */
public class GameController implements Controller {
  private static final int CELL_SIZE = 90;
  private final Town town;
  private final Scanner scanner;
  private final Appendable output;
  private final int maxTurns;
  private List<Player> players;
  private int currentTurn;
  private boolean quitGame;

  /**
   * Creates a new game controller with the given town, input, output, and maximum turns.
   *
   * @param town     The town where the game takes place.
   * @param input    The input source for the game.
   * @param output   The output destination for the game.
   * @param maxTurns The maximum number of turns for the game.
   */
  public GameController(Town town, Readable input, Appendable output, int maxTurns) {
    this.town = town;
    this.scanner = new Scanner(input);
    this.output = output;
    this.maxTurns = maxTurns;
    this.currentTurn = 1;
    this.quitGame = false;
    this.players = new ArrayList<>();
  }

  /**
   * Starts the game and handles user input.
   *
   * @throws IOException if an I/O error occurs
   */
  public void startGame() throws IOException {
    output.append("\n");
    output.append("++++++++++++++++++++\n");
    this.output.append("Welcome to the game! You have ").append(String.valueOf(maxTurns))
        .append(" turns.\n");
//    addComputerPlayer();
    new AddHumanPlayerCommand(players, output, town, scanner).addComputerPlayer();
    while (!quitGame) {
      this.displayMainMenu();
    }
  }

  private void displayMainMenu() throws IOException {
    output.append("Please choose an option:\n");
    output.append("1. Show the Map Information\n");
    output.append("2. Add the player\n");
    output.append("3. Display the player's information\n");
    output.append("4. Display the Place's information\n");
    output.append("5. Start the game\n");
    output.append("6. Print the map\n");
    output.append("0. Exit\n");
    int choice = 0;
    try {
      choice = Integer.parseInt(scanner.nextLine());
    } catch (NumberFormatException e) {
      this.output.append("Invalid input. Please enter a number.\n");
    }
    switch (choice) {
      case 1:
        displayMapInfo();
        break;
      case 2:
        new AddHumanPlayerCommand(players, output, town, scanner).execute();
        break;
      case 3:
        new DisplayPlayerInfoCommand(players, output, scanner).execute();
        break;
      case 4:
        new DisplayPlaceInfoCommand(town, output, scanner).execute();
        break;
      case 5:
        takeTurn();
        break;
      case 6:
        printMap();
        break;
      case 0:
        this.output.append("Exiting...\n");
        this.quitGame = true;
        break;
      default:
        this.output.append("Invalid choice, please try again.\n");
    }

  }

  private void takeTurn() throws IOException {
    boolean continueGame = true;
    if (players.size() == 1) {
      output.append("You have to add more than one player\n");
      continueGame = false;
    }
    while (continueGame) {
      output.append("\n");
      output.append(String.valueOf(currentTurn)).append(" of ").append(String.valueOf(maxTurns))
          .append("\n");
      for (Player player : players) {
        output.append("--------------------\n");
        this.output.append("Player: ").append(player.getName()).append("'s turn\n");
        showPlayerCurrentInfo(player);
        int choice = 0;
        if (player.isComputerControlled()) {
          choice = new Random().nextInt(3) + 1;
        } else {
          this.output.append("Please choose an option:\n");
          this.output.append("1. Move player\n");
          this.output.append("2. Pick up item\n");
          this.output.append("3. Look around\n");
          try {
            choice = Integer.parseInt(scanner.nextLine());
          } catch (NumberFormatException e) {
            this.output.append("Invalid input. Please enter a number.\n");
          }
        }
        switch (choice) {
          case 1:
            new MovePlayerCommand(player, output).execute();
            break;
          case 2:
            new PickUpItemCommand(player, output).execute();
            break;
          case 3:
            new LookAroundCommand(player, output).execute();
            break;
          default:
            this.output.append("Invalid choice, please try again.\n");
        }
      }
      currentTurn++;
      if (currentTurn > maxTurns) {
        continueGame = false;
        output.append("Game over.\n");
        output.append("++++++++++++++++++++\n");
        output.append("\n");
        this.currentTurn = 1;
        this.quitGame = false;
        this.players = new ArrayList<>();
        this.startGame();
      }
    }
  }

  private void displayMapInfo() {
    System.out.println("=== Map Information ===");
    System.out.println("Town: " + town.getTownName());
    System.out.println(
        "Target name: " + town.getTargetName() + " (Health: " + town.getTargetHealth() + ")");
    System.out.println("Places in the town:");
    System.out.println("--------------------");
    int index = 1;
    for (Place place : town.getPlaces()) {
      System.out.println(index + " " + place.getName());
      place.getItems().forEach(item -> System.out.println(
          "Items in the place: " + item.getName() + " (Damage: " + item.getDamage() + ")"));
      index++;
      System.out.println("--------------------");
    }
  }

  private void moveTargetCharacter() {
    System.out.println("Moving the target character...");
    town.moveCharacter();
    getCurrSpace();
  }

  private void getCurrSpace() {
    Place currentPlace = town.getCharacter().getCurrentPlace();
    System.out.println("Target character is in: " + currentPlace.getName());
    System.out.println("Items in the place:");
    currentPlace.getItems()
        .forEach(
            item -> System.out.println(item.getName() + " (Damage: " + item.getDamage() + ")"));
  }

  /**
   * Displays the neighbors of the target character's current space.
   */
  private void getCurrSpaceNeighbors() {
    Place currentPlace = town.getCharacter().getCurrentPlace();
    System.out.println("Neighbors of " + currentPlace.getName() + ":");
    currentPlace.getNeighbors().forEach(neighbor -> System.out.println(neighbor.getName()));
  }

  /**
   * Displays information about the place by index.
   */
  private void getSpaceByIndex() {
    System.out.println("Enter the index of the space (1 to " + (town.getPlaces().size()) + "): ");
    int index = scanner.nextInt() - 1;
    if (index < 0 || index >= town.getPlaces().size()) {
      System.out.println("Invalid number.");
      return;
    }
    Place place = town.getPlaces().get(index);
    System.out.println("Place: " + place.getName());
    if (place.getItems().isEmpty()) {
      System.out.println("No items in the place.");
      return;
    } else {
      System.out.println("Items in the place:");
      place.getItems().forEach(
          item -> System.out.println(item.getName() + " (Damage: " + item.getDamage() + ")"));
    }
  }

  /**
   * Displays the neighbors of the place by index.
   */
  private void getNeighborsByIndex() {
    System.out.println("Enter the index of the space (1 to " + (town.getPlaces().size()) + "): ");
    int index = scanner.nextInt() - 1;
    if (index < 0 || index >= town.getPlaces().size()) {
      System.out.println("Invalid number.");
      return;
    }
    Place place = town.getPlaces().get(index);
    System.out.println("Neighbors of " + place.getName() + ":");
    place.getNeighbors().forEach(neighbor -> System.out.println(neighbor.getName()));
  }

  /**
   * Prints the map of the town to a PNG file.
   */
  private void printMap() {
    System.out.println("Printing the map...");

    int width = 11 * CELL_SIZE;
    int height = 12 * CELL_SIZE;
    BufferedImage mapImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    Graphics2D g2d = mapImage.createGraphics();

    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, width, height);

    g2d.setColor(Color.BLACK);

    for (Place place : town.getPlaces()) {
      int row1 = place.getRow1();
      int col1 = place.getCol1();
      int row2 = place.getRow2();
      int col2 = place.getCol2();
      drawPlace(g2d, place.getName(), row1, col1, row2, col2);
    }

    try {
      ImageIO.write(mapImage, "png", new File("res/map.png"));
      System.out.println("Map saved as map.png");
    } catch (IOException e) {
      e.printStackTrace();
    }

    g2d.dispose();
  }

  /**
   * Draws a place on the map image.
   */
  private void drawPlace(Graphics2D g2d, String name, int row1, int col1, int row2, int col2) {
    int y = col1 * CELL_SIZE;
    int x = row1 * CELL_SIZE;
    int width = (row2 - row1) * CELL_SIZE;
    int height = (col2 - col1) * CELL_SIZE;
    g2d.drawRect(x, y, width, height);
    g2d.drawString(name, x + width / 4, y + height / 4);
  }

//  @Override
//  public void movePlayer(Player currentPlayer) throws IOException {
//    Place currentPlace = currentPlayer.getCurrentPlace();
//    List<Place> neighbors = currentPlace.getNeighbors();
//    if (neighbors.isEmpty()) {
//      output.append("No neighbors found.\n");
//    } else {
//      if (currentPlayer.isComputerControlled()) {
//        Command moveCommand =
//            new MovePlayerCommand(currentPlayer,
//                neighbors.get(new Random().nextInt(neighbors.size())));
//        moveCommand.execute(town);
//      } else {
//        output.append("Neighbors of ").append(currentPlace.getName()).append(":\n");
//        for (int i = 0; i < neighbors.size(); i++) {
//          int currentIndex = i + 1;
//          output.append(String.valueOf(currentIndex)).append(". ")
//              .append(neighbors.get(i).getName())
//              .append("\n");
//        }
//        output.append("Enter the neighbor number to move to:\n");
//        int neighborNumber = Integer.parseInt(scanner.nextLine());
//        if (neighborNumber < 1 || neighborNumber > neighbors.size()) {
//          output.append("Invalid neighbor number.\n");
//        } else {
//          Command moveCommand =
//              new MovePlayerCommand(currentPlayer, neighbors.get(neighborNumber - 1));
//          moveCommand.execute(town);
//        }
//      }
//    }
//  }

//  @Override
//  public void pickUpItem(Player currentPlayer) throws IOException {
//    Place currentPlace = currentPlayer.getCurrentPlace();
//    List<Item> items = currentPlace.getItems();
//    if (items.isEmpty()) {
//      output.append("No items found.\n");
//      return;
//    } else {
//      if (currentPlayer.isComputerControlled()) {
//        Item item = items.get(new Random().nextInt(items.size()));
//        currentPlayer.pickUpItem(item);
//        currentPlace.removeItem(item);
//        output.append("Picked up ").append(item.getName()).append(".\n");
//      } else {
//        output.append("Items in ").append(currentPlace.getName()).append(":\n");
//        for (int i = 0; i < items.size(); i++) {
//          int currentIndex = i + 1;
//          output.append(String.valueOf(currentIndex)).append(". ").append(items.get(i).getName())
//              .append(" (Damage: ").append(String.valueOf(items.get(i).getDamage())).append(")\n");
//        }
//        output.append("Enter the item number to pick up:\n");
//        int itemNumber = Integer.parseInt(scanner.nextLine());
//        if (itemNumber < 1 || itemNumber > items.size()) {
//          output.append("Invalid item number.\n");
//        } else {
//          Item item = items.get(itemNumber - 1);
//          currentPlayer.pickUpItem(item);
//          currentPlace.removeItem(item);
//          output.append("Picked up ").append(item.getName()).append(".\n");
//        }
//      }
//    }
//  }

//  @Override
//  public void lookAround(Player currentPlayer) throws IOException {
//    Place currentPlace = currentPlayer.getCurrentPlace();
//    List<Place> neighbors = currentPlace.getNeighbors();
//    if (neighbors.isEmpty()) {
//      output.append("No neighbors found.\n");
//    } else {
//      output.append("Neighbors of ").append(currentPlace.getName()).append(":\n");
//      for (Place neighbor : neighbors) {
//        output.append(neighbor.getName()).append("\n");
//      }
//    }
//    List<Item> items = currentPlace.getItems();
//    if (items.isEmpty()) {
//      output.append("No items found.\n");
//    } else {
//      output.append("Items in ").append(currentPlace.getName()).append(":\n");
//      for (Item item : items) {
//        output.append(item.getName()).append(" (Damage: ").append(String.valueOf(item.getDamage()))
//            .append(")\n");
//      }
//    }
//  }

  @Override
  public void showPlayerCurrentInfo(Player player) throws IOException {
    output.append("Your current place: ").append(player.getCurrentPlace().getName()).append("\n");
    if (player.getInventory().isEmpty()) {
      output.append("Player has no item in the bag.\n");
    } else {
      output.append("Items in the player's bag:\n");
      player.getInventory().forEach((name, damage) -> {
        try {
          output.append(name).append(" (Damage: ").append(String.valueOf(damage)).append(")\n");
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  @Override
  public Player findPlayerByName(String name) {
    return players.stream()
        .filter(player -> player.getName().equals(name))
        .findFirst()
        .orElse(null);
  }

  @Override
  public Place findPlaceByName(String name) {
    return town.getPlaces().stream()
        .filter(place -> place.getName().equals(name))
        .findFirst()
        .orElse(null);
  }

  @Override
  public Item findItemByName(String name, Place place) {
    return place.getItems().stream()
        .filter(item -> item.getName().equals(name))
        .findFirst()
        .orElse(null);
  }
}