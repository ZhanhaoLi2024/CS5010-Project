import controller.ComputerPlayerController;
import controller.HumanPlayerController;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.imageio.ImageIO;
import place.Place;
import player.ComputerPlayer;
import player.HumanPlayer;
import player.PlayerModel;
import town.TownModel;

/**
 * The Driver class is responsible for running the game and handling user input.
 */
public class Driver {
  private static final int CELL_SIZE = 90;
  //  private static int maxTurns;
  private final List<PlayerModel> players;
  private final Random random;
  private final Scanner scanner;
  private TownModel town;

  /**
   * Constructs the Driver class, responsible for running the game.
   *
   * @param input the source of user input, typically System.in
   */
  public Driver(Readable input) {
    this.players = new ArrayList<>();
    this.random = new Random();
    this.scanner = new Scanner(input);
  }

  /**
   * The main method for running the game.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {
    // Command-line arguments handling
    if (args.length < 2) {
      System.out.println("Usage: java Driver <world_file> <max_turns>");
      return;
    }

    String worldFile = args[0];
    int maxTurns;
    try {
      maxTurns = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      System.out.println("Invalid max turns argument. It should be an integer.");
      return;
    }

    Driver driver = new Driver(new InputStreamReader(System.in));
    driver.runGame(worldFile, maxTurns);
  }

  /**
   * Runs the game based on the specified world file and max number of turns.
   *
   * @param worldFile the file containing the world specification
   * @param maxTurns  the maximum number of turns allowed
   */
  public void runGame(String worldFile, int maxTurns) {
    try {
      town = new TownModel(worldFile);
    } catch (IOException e) {
      System.out.println("Error loading the town: " + e.getMessage());
      return;
    }

    System.out.println("Welcome to Kill Doctor Lucky!");
    displayMapInfo();

    System.out.println("Adding Human players...");
    addHumanControlledPlayers();
    System.out.println("Adding Computer player...");
    addComputerControlledPlayer();

    while (true) {
      System.out.println("\nPlease choose an option:");
      System.out.println("1. Move 'The Mayor' to the next place");
      System.out.println("2. Show target's current space");
      System.out.println("3. Show neighbors of target's space");
      System.out.println("4. Show space by index");
      System.out.println("5. Show neighbors by index");
      System.out.println("6. Print the map");
      System.out.println("7. Start game");
      System.out.println("0. Exit");

      int choice = scanner.nextInt();

      switch (choice) {
        case 1:
          moveTargetCharacter();
          break;
        case 2:
          getCurrSpace();
          break;
        case 3:
          getCurrSpaceNeighbors();
          break;
        case 4:
          getSpaceByIndex();
          break;
        case 5:
          getNeighborsByIndex();
          break;
        case 6:
          printMap();
          break;
        case 7:
          showThePlayerInfo(maxTurns);
          break;
        case 0:
          System.out.println("Exiting...");
          scanner.close();
          return;
        default:
          System.out.println("Invalid choice, please try again.");
      }
    }
  }

  private void showThePlayerInfo(int maxTurns) {
    while (true) {
      System.out.println("Do you want to display info for specific player? ");
      System.out.println("1. Yes");
      System.out.println("2. No");
      System.out.println("3. Display Computer Player Info");
      System.out.println("4. Display all players info");

      int choice = scanner.nextInt();
      scanner.nextLine();
      switch (choice) {
        case 1:
          displayPlayerInfo("Human");
          break;
        case 2:
          startGameRounds(maxTurns);
          return;
        case 3:
          displayPlayerInfo("Computer");
          break;
        case 4:
          displayAllPlayerInfo();
          break;
        default:
          System.out.println("Invalid choice, please try again.");
      }
    }
  }

  /**
   * Starts game rounds between human and computer players.
   */
  private void startGameRounds(int maxTurns) {
    int currentTurn = 0;
    while (currentTurn < maxTurns) {
      System.out.println("\nTurn " + (currentTurn + 1) + " of " + maxTurns);

      for (PlayerModel player : players) {
        System.out.println("\n" + player.getName() + "'s turn:");
        if (player instanceof HumanPlayer) {
          System.out.println(
              "Do you want to display information for " + player.getName() + "? (yes/no)");
          String response = scanner.next();
          if ("yes".equalsIgnoreCase(response)) {
            displayCurrentPlayerInfo(player);
          }
          HumanPlayerController humanController = new HumanPlayerController((HumanPlayer) player);
          humanController.takeTurn();
        } else if (player instanceof ComputerPlayer) {
          System.out.println("Computer player: " + player.getName() + "'s turn:");
          System.out.println(
              "Do you want to display information for " + player.getName() + "? (yes/no)");
          String response = scanner.next();
          if ("yes".equalsIgnoreCase(response)) {
            displayCurrentPlayerInfo(player);
          }
          ComputerPlayerController computerController =
              new ComputerPlayerController((ComputerPlayer) player, random);
          computerController.takeTurn();
        }
      }
      currentTurn++;
    }
    System.out.println("Maximum turns reached. Game over.");
  }

  /**
   * Displays the map information including places and items.
   */
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

  /**
   * Moves the target character to the next place.
   */
  private void moveTargetCharacter() {
    System.out.println("Moving the target character...");
    town.moveCharacter();
    getCurrSpace();
  }

  /**
   * Displays the current space of the target character.
   */
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

  /**
   * Adds multiple human-controlled players to the world.
   */
  private void addHumanControlledPlayers() {
    while (true) {
      System.out.println("Enter the name of the human player: ");
      String playerName = scanner.next();
      Place startPlace = town.getPlaces().get(0);
      HumanPlayer humanPlayer = new HumanPlayer(playerName, startPlace, 3);
      players.add(humanPlayer);
      System.out.println(
          playerName + " (Human) has entered the town at " + startPlace.getName() + ".");

      System.out.println("Do you want to add another player?");
      System.out.println("1 to continue");
      System.out.println("2 to stop");
      int response = scanner.nextInt();
      switch (response) {
        case 1:
          break;
        case 2:
          return;
        default:
          System.out.println("Invalid choice. Exiting...");
      }
    }
  }

  /**
   * Adds a computer-controlled player to the world.
   */
  private void addComputerControlledPlayer() {
    String playerName = "David(Computer)";
    Place startPlace = town.getPlaces().get(1);
    ComputerPlayer computerPlayer = new ComputerPlayer(playerName, startPlace, 3, random);
    players.add(computerPlayer);
    System.out.println(
        playerName + " (Computer) has entered the town at " + startPlace.getName() + ".");
  }

  /**
   * Displays the current player's information.
   */
  private void displayCurrentPlayerInfo(PlayerModel player) {
    System.out.println("Current place: " + player.getCurrentPlace().getName());
    if (player.getItems().isEmpty()) {
      System.out.println("No items carried.");
    } else {
      System.out.println("Items carried:");
      player.getItems().forEach(
          item -> System.out.println(item.getName() + " (Damage: " + item.getDamage() + ")"));
    }
  }

  /**
   * Displays information about all players in the game.
   */
  private void displayAllPlayerInfo() {
    System.out.println("Players in the game:");
    int index = 1;
    for (PlayerModel player : players) {
      System.out.println(index + ". " + player.getName());
      System.out.println("Current place: " + player.getCurrentPlace().getName());
      index++;
    }
  }

  /**
   * Displays information about a specific player in the game.
   */
  private void displayPlayerInfo(String playerType) {
    if ("Computer".equals(playerType)) {
      for (PlayerModel player : players) {
        if (player.getName().equals("David(Computer)")) {
          System.out.println("Computer Player: " + player.getName());
          System.out.println("Current place: " + player.getCurrentPlace().getName());
          return;
        }
      }
    } else if ("Human".equals(playerType)) {
      System.out.println("Enter the player name to display information: ");
      String playerName = scanner.next();
      for (PlayerModel player : players) {
        if (player.getName().equals(playerName)) {
          System.out.println("Player: " + player.getName());
          System.out.println("Current place: " + player.getCurrentPlace().getName());
          return;
        }
      }
      System.out.println("Player not found.");
    }
  }
}