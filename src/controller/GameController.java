package controller;

import controller.command.AddPlayerCommand;
import controller.command.AttackTargetCommand;
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
import java.util.Scanner;
import javax.imageio.ImageIO;
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
    new AddPlayerCommand(town, output, scanner, true).execute();
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
        new AddPlayerCommand(town, output, scanner, false).execute();
        break;
      case 3:
        new DisplayPlayerInfoCommand(town, output, scanner).execute();
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

  /**
   * Handles a turn for the current player. Distinguishes between human and computer-controlled
   * players and delegates to the appropriate handler method. This method enforces the game's
   * turn-based mechanics and ensures each player takes valid actions according to the game rules.
   * For human players, it presents a menu of possible actions.
   * For computer players, it automatically determines and executes the optimal action.
   *
   * @throws IOException if there is an error in reading input or writing output
   */
  private void takeTurnForPlayer() throws IOException {
    boolean isComputerControlled = town.isComputerControllerPlayer();
    if (!isComputerControlled) {
      handleHumanTurn();
    } else {
      handleComputerTurn();
    }
  }

  /**
   * Handles a turn for a human player by displaying available actions and processing their choice.
   * Available actions include:
   * 1. Moving to a neighboring space
   * 2. Picking up items from the current space
   * 3. Looking around to gather information
   * 4. Attempting to attack the target character
   * <p>
   * The method validates user input and executes the chosen action through the appropriate command.
   * Invalid inputs are handled gracefully with error messages.
   * Each action consumes one turn, whether successful or not.
   *
   * @throws IOException if there is an error in reading input or writing output
   */
  private void handleHumanTurn() throws IOException {
    output.append("Please choose an option:\n");
    output.append("1. Move player\n");
    output.append("2. Pick up item\n");
    output.append("3. Look around\n");
    output.append("4. Attack target\n");

    int choice = 0;
    try {
      choice = Integer.parseInt(scanner.nextLine());
    } catch (NumberFormatException e) {
      this.output.append("Invalid input. Please enter a number.\n");
    }

    switch (choice) {
      case 1:
        new MovePlayerCommand(town, output, scanner).execute();
        break;
      case 2:
        new PickUpItemCommand(town, output, scanner).execute();
        break;
      case 3:
        new LookAroundCommand(output, town).execute();
        break;
      case 4:
        new AttackTargetCommand(town, output, scanner).execute();
        break;
      default:
        this.output.append("Invalid choice, please try again.\n");
    }
  }

  /**
   * Handles a turn for a computer-controlled player by automatically selecting and executing
   * the optimal action based on the current game state. The computer player follows a strict
   * priority system for decision making:
   * <p>
   * Priority 1: Attack the target if:
   * - In the same room as the target
   * - Not visible to other players
   * - Has items (will use highest damage item) or can use poke attack
   * <p>
   * Priority 2: Pick up items if:
   * - Items are available in the current space
   * - Has room in inventory (below carry limit)
   * <p>
   * Priority 3: Move towards target if:
   * - Carrying items that can be used for attack
   * - Not in the same room as target
   * <p>
   * Priority 4: Look around if:
   * - No higher priority actions are available
   * - Need to gather information about surroundings
   * <p>
   * Each action is executed through the appropriate command pattern implementation
   * and consumes one turn. The method provides feedback about the computer's actions
   * through the output interface.
   *
   * @throws IOException if there is an error in writing output
   */
  private void handleComputerTurn() throws IOException {
    output.append("Computer player's turn.\n");
    Player computerPlayer = town.getPlayers().get(town.getCurrentPlayerIndex());
    Place currentPlace = town.getPlaceByNumber(computerPlayer.getPlayerCurrentPlaceNumber());

    // First priority: Attack if in same room as target and not visible
    if (currentPlace.getPlaceNumber().equals(
        town.getTarget().getCurrentPlace().getPlaceNumber())
        && !town.isPlayerVisible(computerPlayer)) {
      output.append("Computer player attempts to attack the target.\n");
      new AttackTargetCommand(town, output, scanner).execute();
      return;
    }

    // Second priority: Pick up items if available and has space
    if (!currentPlace.getItems().isEmpty()
        && computerPlayer.getCurrentCarriedItems().size() < computerPlayer.getCarryLimit()) {
      output.append("Computer player picks up an item.\n");
      new PickUpItemCommand(town, output, scanner).execute();
      return;
    }

    // Third priority: Move towards target if carrying items
    if (!computerPlayer.getCurrentCarriedItems().isEmpty()) {
      output.append("Computer player moves to find the target.\n");
      new MovePlayerCommand(town, output, scanner).execute();
      return;
    }

    // Fourth priority: Look around to gather information
    output.append("Computer player looks around.\n");
    new LookAroundCommand(output, town).execute();
  }

  @Override
  public void takeTurn() throws IOException {
    if (town.getPlayers().size() == 1) {
      output.append("You have to add at least one player\n");
      continueGame = false;
    }
    while (continueGame) {
      output.append("\n");
      output.append(String.valueOf(town.getCurrentTurn())).append(" of ")
          .append(String.valueOf(maxTurns))
          .append("\n");
      town.showPlayerCurrentInfo();
      this.takeTurnForPlayer();
      boolean isGameOver = town.isGameOver();
      if (isGameOver) {
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
}