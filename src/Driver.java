import controller.GameController;
import java.io.IOException;
import java.io.InputStreamReader;
import model.town.Town;
import model.town.TownLoader;
import model.town.TownModel;

/**
 * The Driver class to launch the game and handle command-line arguments.
 */
public class Driver {

  /**
   * The main method to run the game.
   *
   * @param args the command-line arguments
   * @throws IOException if an I/O error occurs
   */
  public static void main(String[] args) throws IOException {

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

    TownLoader loader = new TownLoader();
    Town town = new TownModel(loader, worldFile);

    GameController controller =
        new GameController(town, new InputStreamReader(System.in), System.out, maxTurns);
    controller.startGame();
  }
}
