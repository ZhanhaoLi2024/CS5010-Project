import controller.Controller;
import controller.GuiController;
import controller.TextController;
import java.io.IOException;
import java.io.InputStreamReader;
import model.town.Town;
import model.town.TownLoader;
import model.town.TownModel;
import view.GameView;
import view.GuiGameView;
import view.TextGameView;

public class Driver {
  /**
   * The main method to run the game.
   *
   * @param args the command-line arguments
   * @throws IOException if an I/O error occurs
   */
  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.out.println("Usage: java Driver <world_file> <max_turns> [--gui]");
      return;
    }

    String worldFile = args[0];
    int maxTurns;
    boolean useGui = args.length > 2 && args[2].equals("--gui");

    try {
      maxTurns = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      System.out.println("Invalid max turns argument. It should be an integer.");
      return;
    }

    try {
      // Initialize MVC components in the correct order
      // 1. Create the Model
      TownLoader loader = new TownLoader();
      Town town = new TownModel(
          loader,
          worldFile,
          new InputStreamReader(System.in),
          System.out,
          maxTurns
      );

      // 2. Create the Controller
      // 3. Create the View and connect it to the Controller
      // 4. Connect the View to the Controller
      Controller controller;
      GameView view;
      if (useGui) {
        controller = new GuiController(town, null, maxTurns);
        view = new GuiGameView(controller);
        controller.setView(view, true);
      } else {
        controller = new TextController(town, null, maxTurns);
        view = new TextGameView(controller, new InputStreamReader(System.in), System.out);
        controller.setView(view, false);
      }

      // 5. Start the game
      controller.startGame();

    } catch (IOException e) {
      System.err.println("Error starting game: " + e.getMessage());
      throw e;
    } catch (IllegalArgumentException e) {
      System.err.println("Invalid game configuration: " + e.getMessage());
    }
  }
}