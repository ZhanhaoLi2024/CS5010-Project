package controller.command;

import java.io.IOException;
import model.town.Town;
import view.GameView;

/**
 * Command to add a human player or computer player to the game.
 */
public class AddPlayerCommand implements Command {
  private final Town town;
  private final GameView view;
  private final boolean isComputerPlayer;
  private final String playerName;
  private final int startingPlace;
  private final int carryLimit;

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
    this.playerName = name;
    this.startingPlace = place;
    this.carryLimit = limit;
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
//      addPlayerMenu();
      if (playerName == null) {
        // 文本界面的处理逻辑
        addPlayerMenu();
      } else {
        // GUI界面的处理逻辑
        addGuiPlayer();
      }
    }
  }

  private void addPlayerMenu() throws IOException {
    boolean addPlayerContinue = true;
    town.addPlayer();
    while (addPlayerContinue) {
      view.showMessage("Do you want to add another player? (yes/no)");
      String choice = view.getUserInput();
      if ("yes".equalsIgnoreCase(choice)) {
        town.addPlayer();
      } else if ("no".equalsIgnoreCase(choice)) {
        addPlayerContinue = false;
      } else {
        view.showMessage("Invalid input. Please enter 'yes' or 'no'.");
      }
    }
  }

  private void addGuiPlayer() throws IOException {
    if (playerName == null || playerName.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be empty");
    }
    if (startingPlace < 1 || startingPlace > town.getPlaces().size()) {
      throw new IllegalArgumentException("Invalid starting place");
    }
    if (carryLimit < 1 || carryLimit > 10) {
      throw new IllegalArgumentException("Invalid carry limit");
    }

    // 创建新玩家
    Player player = new PlayerModel(playerName, false, carryLimit, startingPlace);

    // 添加到游戏中
    town.getPlayers().add(player);

    // 通知视图
    view.showMessage("Player " + playerName + " added successfully!");
  }
}