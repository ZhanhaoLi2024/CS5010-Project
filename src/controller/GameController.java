package controller;

import controller.command.AddPlayerCommand;
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
  private List<Place> places;
  private int currentTurn;
  private boolean quitGame;
  private boolean continueGame;

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
    this.places = new ArrayList<>();
    this.continueGame = true;
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
    // Add Computer-controlled player
    new AddPlayerCommand(players, output, town, scanner, true).execute();
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
        new AddPlayerCommand(players, output, town, scanner, false).execute();
        break;
      case 3:
        new DisplayPlayerInfoCommand(town, output, scanner).execute();
        break;
      case 4:
        new DisplayPlaceInfoCommand(town, players, output, scanner).execute();
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

  @Override
  public void takeTurn() throws IOException {
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
            new MovePlayerCommand(player, output, scanner).execute();
            break;
          case 2:
            new PickUpItemCommand(player, output, scanner).execute();
            break;
          case 3:
            new LookAroundCommand(player, output, players).execute();
            break;
          default:
            this.output.append("Invalid choice, please try again.\n");
        }
      }
      currentTurn++;
      if (currentTurn > maxTurns) {
        endGame();
      }
    }
  }

  @Override
  public void endGame() throws IOException {
    continueGame = false;
    output.append("Game over.\n");
    output.append("++++++++++++++++++++\n");
    output.append("\n");
    this.currentTurn = 1;
    this.quitGame = false;
    this.players = new ArrayList<>();
    this.startGame();
  }

  /**
   * Displays the map information.
   */
  @Override
  public void displayMapInfo() {
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

  /**
   * Prints the map of the town to a PNG file.
   */
  @Override
  public void printMap() throws IOException {
    output.append("Printing the map...\n");

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
      output.append("Map saved as map.png\n");
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

  @Override
  public void showPlayerCurrentInfo(Player player) throws IOException {
//    new DisplayPlayerInfoCommand(players, output, scanner).showSpecificPlayerInfo(player);
    System.out.println("不能用了");
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

  @Override
  public List<Player> getPlayers() {
    return players;
  }
}