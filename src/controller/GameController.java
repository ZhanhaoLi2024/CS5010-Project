package controller;

import controller.command.LookAroundCommand;
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
import view.GameView;

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
   * @param gameTown     The town where the game takes place.
   * @param input        The input source for the game.
   * @param gameOutput   The output destination for the game.
   * @param gameMaxTurns The maximum number of turns for the game.
   */
  public GameController(Town gameTown, Readable input, Appendable gameOutput, int gameMaxTurns) {
    this.town = gameTown;
    this.scanner = new Scanner(input);
    this.output = gameOutput;
    this.maxTurns = gameMaxTurns;
    this.currentTurn = 1;
    this.quitGame = false;
  }

  @Override
  public void setView(GameView gameView, boolean gui) {

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
    while (!quitGame) {
      this.displayMainMenu();
    }
  }

  @Override
  public Town getTown() {
    return null;
  }

  @Override
  public void executeCommand(String command) throws IOException {

  }

  /**
   * Displays the main menu of the game.
   *
   * @throws IOException if an I/O error occurs
   */
  private void displayMainMenu() throws IOException {
    output.append("Please choose an option:\n");
    output.append("1. Show the Map Information\n");
    output.append("2. Add the Human-controller player\n");
    output.append("3. Add the Computer-controller player\n");
    output.append("4. Display the player's information\n");
    output.append("5. Display the Place's information\n");
    output.append("6. Start the game\n");
    output.append("7. Print the map\n");
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
//        new AddPlayerCommand(town, output, scanner, false).execute();
        break;
      case 3:
//        new AddPlayerCommand(town, output, scanner, true).execute();
        break;
      case 4:
//        new DisplayPlayerInfoCommand(town, output, scanner).execute();
        break;
      case 5:
//        new DisplayPlaceInfoCommand(town, output, scanner).execute();
        break;
      case 6:
        takeTurn();
        break;
      case 7:
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
    output.append("5. Move pet\n");

    int choice = 0;
    try {
      choice = Integer.parseInt(scanner.nextLine());
    } catch (NumberFormatException e) {
      this.output.append("Invalid input. Please enter a number.\n");
    }

    switch (choice) {
      case 1:
//        new MovePlayerCommand(town).execute();
        break;
      case 2:
//        new PickUpItemCommand(town).execute();
        break;
      case 3:
//        new LookAroundCommand(town).execute();
        break;
      case 4:
//        new AttackTargetCommand(town, output).execute();
        break;
      case 5:
//        new MovePetCommand(town, output, scanner).execute();
        break;
      default:
        this.output.append("Invalid choice, please try again.\n");
    }
  }

  /**
   * Handles a turn for a computer-controlled player by automatically selecting and executing
   * the optimal action based on the current game state. The computer player follows a strict
   * priority system for decision-making:
   * <p>
   * Priority 1: Attack the target if:
   * - In the same room as the target
   * - Not visible to other players
   * - Has items (will use the highest damage item) or can use poke attack
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
//      new AttackTargetCommand(town, output).execute();
      return;
    }

    // Second priority: Pick up items if available and has space
    if (!currentPlace.getItems().isEmpty()
        && computerPlayer.getCurrentCarriedItems().size() < computerPlayer.getCarryLimit()) {
      output.append("Computer player picks up an item.\n");
//      new PickUpItemCommand(town).execute();
      return;
    }

    // Third priority: Move towards target if carrying items
    if (!computerPlayer.getCurrentCarriedItems().isEmpty()) {
      output.append("Computer player moves to find the target.\n");
//      new MovePlayerCommand(town).execute();
      return;
    }

    // Fourth priority: Look around to gather information
    output.append("Computer player looks around.\n");
    new LookAroundCommand(town).execute();
  }

  /**
   * Executes a turn in the game. This method is responsible for managing the game state and
   * ensuring that each player takes a turn in the correct order. The game continues until the
   * maximum number of turns is reached or the game ends due to a player winning or quitting.
   * <p>
   * The method displays the current turn number, player information, and prompts the current player
   * to take their turn. It checks if the game is over after each turn and ends the game
   * if necessary.
   * The game loop continues until the game is over or the player quits.
   *
   * @throws IOException if there is an error in reading input or writing output
   */


//  @Override
  public void takeTurn() throws IOException {
    if (town.getPlayers().size() < 2) {
      output.append("You have to add at least two player\n");
      continueGame = false;
    } else {
      continueGame = true;
    }
    output.append("Game started!\n");
    output.append("Target: ").append(town.getTargetName()).append(" (Health: ")
        .append(String.valueOf(town.getTargetHealth())).append(") in ")
        .append(town.getTarget().getCurrentPlace().getName()).append("\n");
//    town.showPetCurrentInfo();
    while (continueGame) {
      output.append("\n");
      output.append(String.valueOf(town.getCurrentTurn())).append(" of ")
          .append(String.valueOf(maxTurns))
          .append("\n");
//      town.showBasicLocationInfo();
      this.takeTurnForPlayer();
      boolean isGameOver = town.isGameOver();
      if (isGameOver) {
        endGame();
      }
    }
  }

  /**
   * Ends the game and resets the game state for a new game.
   *
   * @throws IOException if there is an error in writing output
   */
//  @Override
  public void endGame() throws IOException {
    continueGame = false;
    if (town.getTarget().isDefeated()) {
      Player currentPlayer = town.getPlayers().get(town.getCurrentPlayerIndex());
      output.append("Game Over! ").append(currentPlayer.getName())
          .append(" has successfully eliminated the target and won the game!\n");
    } else if (town.getCurrentTurn() > town.getMaxTurns()) {
      output.append("Game Over! The target has escaped and nobody wins!\n");
    }
    output.append("++++++++++++++++++++\n");
    output.append("\n");
    this.currentTurn = 1;
    this.quitGame = false;
    town.resetGameState();
  }

  /**
   * Displays the map information.
   */
//  @Override
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
//  @Override
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