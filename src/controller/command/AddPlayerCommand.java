package controller.command;

import java.io.IOException;
import model.town.Town;
import view.GameView;
import view.TextGameView;

/**
 * Command to add a human player or computer player to the game.
 */
public class AddPlayerCommand implements Command {
  private final Town town;
  private final GameView view;
  private final boolean isComputerPlayer;
  private final String guiPlayerName;
  private final int guiStartingPlace;
  private final int guiCarryLimit;

  /**
   * Constructs a new AddPlayerCommand. GUI version.
   *
   * @param gameTown   the town model
   * @param gameView   the game view
   * @param playerType true if computer player, false if human player
   * @param name       the name of the player
   * @param place      the starting place of the player
   * @param limit      the carry limit of the player
   */
  public AddPlayerCommand(Town gameTown, GameView gameView, boolean playerType,
                          String name, int place, int limit) {
    this.town = gameTown;
    this.view = gameView;
    this.isComputerPlayer = playerType;
    this.guiPlayerName = name;
    this.guiStartingPlace = place;
    this.guiCarryLimit = limit;
  }


  /**
   * Constructs a new AddPlayerCommand. Text-base version.
   *
   * @param gameTown   the town model
   * @param gameView   the game view
   * @param playerType true if computer player, false if human player
   */
  public AddPlayerCommand(Town gameTown, GameView gameView, boolean playerType) {
    this(gameTown, gameView, playerType, null, -1, -1);
  }

  @Override
  public void execute() throws IOException {
    if (isComputerPlayer) {
      town.addComputerPlayer();
    } else {
      if (guiPlayerName == null) {
        addPlayerMenu();
      } else {
        addGuiPlayer();
      }
    }
  }

  private void addPlayerMenu() throws IOException {
    boolean addPlayerContinue = true;
    String firstPlayerName = TextGameView.showAddNewPlayerName();
    int firstPlaceNumber = TextGameView.showAddNewPlayerStartingPlaceNumber();
    int firstCarryLimit = TextGameView.showNewPlayerCarryLimit();
    town.addPlayer(firstPlayerName, firstPlaceNumber, firstCarryLimit);
    while (addPlayerContinue) {
      view.showMessage("Do you want to add another player? (yes/no)");
      String choice = view.getUserInput();
      if ("yes".equalsIgnoreCase(choice)) {
        String humanPlayerName = TextGameView.showAddNewPlayerName();
        int humanPlaceNumber = TextGameView.showAddNewPlayerStartingPlaceNumber();
        int humanCarryLimit = TextGameView.showNewPlayerCarryLimit();
        town.addPlayer(humanPlayerName, humanPlaceNumber, humanCarryLimit);
      } else if ("no".equalsIgnoreCase(choice)) {
        addPlayerContinue = false;
      } else {
        view.showMessage("Invalid input. Please enter 'yes' or 'no'.");
      }
    }
  }

  private void addGuiPlayer() throws IOException {
    if (guiPlayerName == null || guiPlayerName.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be empty");
    }
    if (guiStartingPlace < 1 || guiStartingPlace > town.getPlaces().size()) {
      throw new IllegalArgumentException("Invalid starting place");
    }
    if (guiCarryLimit < 1 || guiCarryLimit > 10) {
      throw new IllegalArgumentException("Invalid carry limit");
    }

    town.addPlayer(guiPlayerName, guiStartingPlace, guiCarryLimit);

  }
}