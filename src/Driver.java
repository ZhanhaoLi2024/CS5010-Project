import controller.Controller;
import controller.GameController;
import java.io.IOException;
import java.io.InputStreamReader;
import model.town.Town;
import model.town.TownLoader;
import model.town.TownModel;
import view.ConsoleGameView;
import view.GameView;

/**
 * Driver class serves as the entry point for the game application.
 * It follows the MVC pattern by initializing and connecting the Model, View, and Controller components.
 */
public class Driver {
  private static final String USAGE_MESSAGE =
      "Usage: java -jar game.jar <world_file> <max_turns>\n" +
          "  world_file: path to the world specification file\n" +
          "  max_turns: maximum number of turns allowed (positive integer)";

  /**
   * Entry point of the application.
   * Validates command line arguments and initializes the game components.
   *
   * @param args command line arguments: world_file and max_turns
   */
  public static void main(String[] args) {
    try {
      // Validate command line arguments
      if (!validateArguments(args)) {
        System.out.println(USAGE_MESSAGE);
        return;
      }

      // Parse arguments
      String worldFile = args[0];
      int maxTurns = Integer.parseInt(args[1]);

      // Initialize MVC components
      Town model = initializeModel(worldFile, maxTurns);
      GameView view = initializeView();
      Controller controller = initializeController(model, view);

      // Start the game
      controller.startGame();

    } catch (IOException e) {
      System.err.println("Error occurred: " + e.getMessage());
      System.exit(1);
    }
  }

  /**
   * Validates the command line arguments.
   *
   * @param args the command line arguments to validate
   * @return true if arguments are valid, false otherwise
   */
  private static boolean validateArguments(String[] args) {
    if (args.length != 2) {
      return false;
    }

    try {
      int maxTurns = Integer.parseInt(args[1]);
      return maxTurns > 0;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Initializes the Model component.
   *
   * @param worldFile file path to the world specification
   * @param maxTurns  maximum number of turns allowed
   * @return initialized Town model
   * @throws IOException if there's an error reading the world file
   */
  private static Town initializeModel(String worldFile, int maxTurns) throws IOException {
    TownLoader loader = new TownLoader();
    return new TownModel(loader, worldFile,
        new InputStreamReader(System.in),
        System.out,
        maxTurns);
  }

  /**
   * Initializes the View component.
   *
   * @return initialized GameView
   */
  private static GameView initializeView() {
    return new ConsoleGameView(new InputStreamReader(System.in), System.out);
  }

  /**
   * Initializes the Controller component.
   *
   * @param model the game model
   * @param view  the game view
   * @return initialized Controller
   */
  private static Controller initializeController(Town model, GameView view) {
    return new GameController(model, view);
  }
}