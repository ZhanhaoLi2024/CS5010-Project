package mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.item.Item;
import model.place.Place;
import model.player.Player;
import model.target.Target;
import model.town.Town;

/**
 * Mock implementation of the Town interface for testing purposes.
 * Records method calls and returns preset test data.
 */
public class MockTownModel implements Town {
  private final StringBuilder log;
  private final Map<Integer, Integer> playerPlaceNumbers = new HashMap<>();
  private final Map<String, Integer> methodCallCount = new HashMap<>();
  private final Map<Integer, String> neighborInfoMap = new HashMap<>();
  public String placeInfoToReturn;
  private List<Place> places;
  private List<Player> players;
  private Target target;
  private int currentPlayerIndex;
  private int currentTurn;
  private int maxTurns;
  private boolean gameOver;
  private boolean playerVisible;
  private boolean isComputerPlayer = false;
  private String currentPlayerInfo;
  private boolean initialized = false;

  /**
   * Constructs a new MockTownModel with initial test data.
   */
  public MockTownModel() {
    this.log = new StringBuilder();
    this.places = new ArrayList<>();
    this.players = new ArrayList<>();
    this.currentPlayerIndex = 0;
    this.currentTurn = 1;
    this.maxTurns = 5;
    this.gameOver = false;
  }

  /**
   * Sets whether the player is a computer player.
   *
   * @param isComputer true if the player is a computer player, false otherwise
   */
  public void setIsComputerPlayer(boolean isComputer) {
    this.isComputerPlayer = isComputer;
  }

  @Override
  public Boolean isComputerControllerPlayer() {
    logMethodCall("isComputerControllerPlayer");
    if (players != null && !players.isEmpty() && currentPlayerIndex >= 0) {
      return players.get(currentPlayerIndex).isComputerControlled();
    }
    return isComputerPlayer;
  }

  /**
   * Gets the log of method calls.
   *
   * @return String containing all logged method calls
   */
  public String getLog() {
    return log.toString();
  }

  /**
   * Clears the log of method calls.
   */
  public void clearLog() {
    log.setLength(0);
  }

  /**
   * Appends the method name to the log and sets it as the last method called.
   *
   * @param methodName the name of the method called
   */
  private void logMethodCall(String methodName) {
    log.append(methodName).append(" called\n");
  }

  @Override
  public String petCurrentInfo() {
    logMethodCall("petCurrentInfo");
    return "MockPet,MockPlace";
  }

  @Override
  public Place getPlaceByNumber(int placeNumber) {
    if (places != null && placeNumber > 0 && placeNumber <= places.size()) {
      return places.get(placeNumber - 1);
    }
    return null;
  }

  @Override
  public void moveTarget() {
    logMethodCall("moveTarget");
  }

  @Override
  public Target getTarget() {
    logMethodCall("getTarget");
    return target;
  }

  /**
   * Sets the target for testing purposes.
   *
   * @param target the target to set
   */
  public void setTarget(Target target) {
    this.target = target;
  }

  @Override
  public void movePet(int placeNumber) {
    logMethodCall("movePet");
    log.append("Moving pet to: ").append(placeNumber).append("\n");

    // 添加边界检查
    if (placeNumber <= 0) {
      throw new IllegalArgumentException("Error in Pet movePet: place number must be positive!");
    }
    if (placeNumber > 20) {
      throw new IllegalArgumentException(
          "Error in Pet movePet: place number must be less than or equal to 20!");
    }
  }

  @Override
  public List<Place> getPlaces() {
    logMethodCall("getPlaces");
    // Return the places list, even if empty
    if (places == null) {
      places = new ArrayList<>();
    }
    return places;
  }

  /**
   * Sets the list of places for testing purposes.
   *
   * @param places the list of places to set
   */
  public void setPlaces(List<Place> places) {
    this.places = places;
  }

  @Override
  public String getTargetName() {
    logMethodCall("getTargetName");
    return "MockTarget";
  }

  @Override
  public int getTargetHealth() {
    logMethodCall("getTargetHealth");
    return 50;
  }

  @Override
  public List<Player> getPlayers() {
    logMethodCall("getPlayers");
    return players;
  }

  /**
   * Sets the list of players for testing purposes.
   *
   * @param players the list of players to set
   */
  public void setPlayers(List<Player> players) {
    this.players = players;
  }

  /**
   * Sets the place info to return for testing purposes.
   */
  public void setPlayerCurrPlaceNumber(int playerIndex, int placeNumber) {
    playerPlaceNumbers.put(playerIndex, placeNumber);
  }

  @Override
  public Integer getPlayerCurrPlaceNumber(int playerIndex) {
    return playerPlaceNumbers.getOrDefault(playerIndex, 1);
  }

  @Override
  public String getCurrentPlaceNeighborsInfo(int placeNumber) {
    logMethodCall("getCurrentPlaceNeighborsInfo");
    if (shouldSkipExecution("getCurrentPlaceNeighborsInfo")) {
      return "";
    }
    return neighborInfoMap.getOrDefault(placeNumber,
        "[[TestPlace3;3;[];[];true;false]]");
  }

  @Override
  public void addPlayer(String playerName, int placeNumber, int carryLimit,
                        boolean isComputerPlayer) {
    logMethodCall("addPlayer");

    // Modified validation to match actual implementation
    if (placeNumber < 1 || placeNumber > getPlaces().size()) {
      throw new IllegalArgumentException("Invalid place number");
    }
    if (playerName == null || playerName.trim().isEmpty()) {
      throw new IllegalArgumentException("Invalid player name");
    }
    if (carryLimit < 1 || carryLimit > 10) {
      throw new IllegalArgumentException("Invalid carry limit");
    }

    String playerType = isComputerPlayer ? "computer" : "human";
    log.append("Player added: ").append(playerName)
        .append(" (").append(playerType).append(")\n");
  }

  @Override
  public String getPlayerByName(String playerName) {
    logMethodCall("getPlayerByName");
    return "MockPlayer,MockPlace,5";
  }

  @Override
  public void lookAround() {
    logMethodCall("lookAround");
    switchToNextPlayer();
  }

  @Override
  public boolean attackTarget(String itemName) {
    logMethodCall("attackTarget");
    return true;
  }

  @Override
  public void switchToNextPlayer() {
    logMethodCall("switchToNextPlayer");
    if (shouldSkipExecution("switchToNextPlayer")) {
      return;
    }

    if (players != null && !players.isEmpty()) {
      currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

      if (currentPlayerIndex == 0) {
        currentTurn++;
        moveTarget();
      }

      log.append("Switched to player ").append(currentPlayerIndex)
          .append(" on turn ").append(currentTurn).append("\n");
    }
  }

  /**
   * Sets whether the player is visible for testing purposes.
   *
   * @param isVisible true if the player is visible, false otherwise
   */
  public void setPlayerVisible(boolean isVisible) {
    this.playerVisible = isVisible;
  }

  /**
   * Sets the place info to return for testing purposes.
   */
  public void setBasicLocationInfo() {
  }

  @Override
  public String showBasicLocationInfo() {
    logMethodCall("showBasicLocationInfo");

    if (!initialized) {
      initialized = true;
      return String.format("[[%s,%s,5,%s], [], [%s,%s,%d], [%s,%s]]",
          players.get(currentPlayerIndex).getName(),
          "TestPlace",
          "None",
          "MockTarget",
          "TestPlace",
          50,
          "MockPet",
          "TestPlace");
    }

    return currentPlayerInfo != null ? currentPlayerInfo :
        "[[MockPlayer,TestPlace,5,None], [], [MockTarget,TestPlace,50], [MockPet,TestPlace]]";
  }

  /**
   * Sets the place info to return for testing purposes.
   */
  public void setCurrentPlayerInfo(String info) {
    this.currentPlayerInfo = info;
  }

  @Override
  public List<String> getAllPlayersInfo() {
    logMethodCall("getAllPlayersInfo");
    List<String> playerInfo = new ArrayList<>();
    playerInfo.add("MockPlayer,MockPlace,5");
    return playerInfo;
  }

  @Override
  public void resetGameState() {
    logMethodCall("resetGameState");
    currentPlayerIndex = 0;
    currentTurn = 1;
    gameOver = false;
  }

  @Override
  public boolean isGameOver() {
    logMethodCall("isGameOver");
    return gameOver;
  }

  /**
   * Sets whether the game is over for testing purposes.
   *
   * @param isGameOver true if the game is over, false otherwise
   */
  public void setGameOver(boolean isGameOver) {
    this.gameOver = isGameOver;
  }

  @Override
  public int getCurrentTurn() {
    logMethodCall("getCurrentTurn");
    return currentTurn;
  }

  /**
   * Sets the current turn for testing purposes.
   *
   * @param turn the turn to set
   */
  public void setCurrentTurn(int turn) {
    this.currentTurn = turn;
  }

  @Override
  public void movePlayer(int playerIndex, int newPlaceNumber) {
    logMethodCall("movePlayer");
    if (shouldSkipExecution("movePlayer")) {
      return;
    }

    log.append("Player ").append(playerIndex)
        .append(" moved to place ").append(newPlaceNumber).append("\n");

    playerPlaceNumbers.put(playerIndex, newPlaceNumber);

    switchToNextPlayer();
  }

  @Override
  public void pickUpItem(String itemName) {
    logMethodCall("pickUpItem");
    if (shouldSkipExecution("pickUpItem")) {
      return;
    }

    log.append("Item picked up: ").append(itemName).append("\n");
    switchToNextPlayer();
  }

  @Override
  public int getCurrentPlayerIndex() {
    logMethodCall("getCurrentPlayerIndex");
    return currentPlayerIndex;
  }

  /**
   * Sets the current player index for testing purposes
   *
   * @param index the index to set
   */
  public void setCurrentPlayerIndex(int index) {
    this.currentPlayerIndex = index;
  }

  /**
   * Sets the neighbor places for testing purposes.
   *
   * @param placeNumber the place number
   * @param hasTarget   true if the neighbor has the target, false otherwise
   */
  public void setNeighborInfoForPlace(int placeNumber, boolean hasTarget) {
    StringBuilder info = new StringBuilder("[[");
    if (hasTarget) {
      info.append(String.format("Neighbor1;%d;[];[];true;false", placeNumber));
    } else {
      info.append(String.format("Neighbor1;%d;[];[];false;false", placeNumber));
    }
    info.append("]]");
    neighborInfoMap.put(placeNumber, info.toString());
  }

  @Override
  public boolean isPlayerVisible(Player player) {
    logMethodCall("isPlayerVisible");
    return playerVisible;
  }

  @Override
  public String getPlayerCurrentCarriedItems(int playerIndex) {
    logMethodCall("getPlayerCurrentCarriedItems");
    return "[Sword-10]";
  }

  @Override
  public int getMaxTurns() {
    logMethodCall("getMaxTurns");
    return maxTurns;
  }

  /**
   * Sets the maximum number of turns for testing purposes.
   *
   * @param maxTurns the maximum number of turns to set
   */
  public void setMaxTurns(int maxTurns) {
    this.maxTurns = maxTurns;
  }

  @Override
  public String getCurrentPlaceInfo(int placeNumber) {
    logMethodCall("getCurrentPlaceInfo");
    if (placeNumber > 0 && placeNumber <= places.size()) {
      Place place = places.get(placeNumber - 1);
      StringBuilder itemsStr = new StringBuilder("[");
      boolean first = true;
      for (Item item : place.getItems()) {
        if (!first) {
          itemsStr.append(", ");
        }
        itemsStr.append(item.getName()).append("-").append(item.getDamage());
        first = false;
      }
      itemsStr.append("]");

      return String.format("%s;%s;[]", place.getName(), itemsStr);
    }
    return placeInfoToReturn != null ? placeInfoToReturn : "MockPlace;[];[]";
  }

  /**
   * Sets the place info to return for testing purposes.
   */
  private boolean shouldSkipExecution(String methodName) {
    int count = methodCallCount.getOrDefault(methodName, 0) + 1;
    methodCallCount.put(methodName, count);
    return count > 1;
  }
}