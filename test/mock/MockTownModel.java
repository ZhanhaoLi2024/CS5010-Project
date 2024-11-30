package mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  public String placeInfoToReturn;
  private List<Place> places;
  private List<Player> players;
  private Target target;
  private int currentPlayerIndex;
  private int currentTurn;
  private int maxTurns;
  private boolean gameOver;
  private String lastMethodCalled;
  private Map<Integer, Integer> playerPlaceNumbers = new HashMap<>();
  private Map<Integer, List<Integer>> neighborPlaces = new HashMap<>();
  private boolean playerVisible;

  /**
   * Constructs a new MockTownModel with initial test data.
   */
  public MockTownModel() {
    this.log = new StringBuilder();
    this.places = new ArrayList<>();
    this.players = new ArrayList<>();
    this.currentPlayerIndex = 0;
    this.currentTurn = 1;
    this.maxTurns = 50;
    this.gameOver = false;
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
   * Gets the last method called.
   *
   * @return name of the last method called
   */
  public String getLastMethodCalled() {
    return lastMethodCalled;
  }

  /**
   * Clears the log of method calls.
   */
  public void clearLog() {
    log.setLength(0);
    lastMethodCalled = null;
  }

  private void logMethodCall(String methodName) {
    log.append(methodName).append(" called\n");
    lastMethodCalled = methodName;
  }

  // Town interface implementations
  @Override
  public String petCurrentInfo() {
    logMethodCall("petCurrentInfo");
    return "MockPet,MockPlace";
  }

  @Override
  public Place getPlaceByNumber(int placeNumber) {
    logMethodCall("getPlaceByNumber");
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

  public void setTarget(Target target) {
    this.target = target;
  }

  @Override
  public void movePet(int placeNumber) throws IOException {
    logMethodCall("movePet");
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

  public void setPlayers(List<Player> players) {
    this.players = players;
  }

  public void setPlayerCurrPlaceNumber(int playerIndex, int placeNumber) {
    playerPlaceNumbers.put(playerIndex, placeNumber);
  }

  public void setNeighborPlaces(int placeNumber, List<Integer> places) {
    neighborPlaces.put(placeNumber, places);
  }

  @Override
  public Integer getPlayerCurrPlaceNumber(int playerIndex) {
    return playerPlaceNumbers.getOrDefault(playerIndex, 1);
  }

  @Override
  public String getCurrentPlaceNeighborsInfo(int placeNumber) throws IOException {
    return "[[MockNeighbor;2;[];[];false;false], [AnotherNeighbor;3;[];[];true;false]]";
  }

//  @Override
//  public String getCurrentPlaceInfo(int placeNumber) throws IOException {
//    logMethodCall("getCurrentPlaceInfo");
//    return "MockPlace;[];[]";
//  }

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
  public String getPlayerByName(String playerName) throws IOException {
    logMethodCall("getPlayerByName");
    return "MockPlayer,MockPlace,5";
  }

  @Override
  public void lookAround() throws IOException {
    logMethodCall("lookAround");
    switchToNextPlayer();
  }

  @Override
  public boolean attackTarget(String itemName) throws IOException {
    logMethodCall("attackTarget");
    return true; // 返回true表示目标被击败
  }

  @Override
  public void switchToNextPlayer() throws IOException {
    logMethodCall("switchToNextPlayer");
    if (players != null && !players.isEmpty()) {
      currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
      if (currentPlayerIndex == 0) {
        currentTurn++;
        moveTarget();
      }
    }
  }

  public void setPlayerVisible(boolean isVisible) {
    this.playerVisible = isVisible;
  }

  @Override
  public String showBasicLocationInfo() throws IOException {
    logMethodCall("showBasicLocationInfo");
    return "[[MockPlayer,MockPlace,5,None], [], [MockTarget,MockPlace,50], [MockPet,MockPlace]]";
  }

  @Override
  public List<String> getAllPlayersInfo() {
    logMethodCall("getAllPlayersInfo");
    List<String> playerInfo = new ArrayList<>();
    playerInfo.add("MockPlayer,MockPlace,5");
    return playerInfo;
  }

  @Override
  public void resetGameState() throws IOException {
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

  // Additional test helper methods
  public void setGameOver(boolean isGameOver) {
    this.gameOver = isGameOver;
  }

  @Override
  public Boolean isComputerControllerPlayer() {
    logMethodCall("isComputerControllerPlayer");
    return false;
  }

  @Override
  public int getCurrentTurn() {
    logMethodCall("getCurrentTurn");
    return currentTurn;
  }

  public void setCurrentTurn(int turn) {
    this.currentTurn = turn;
  }

  public void setPlaceInfoString(String placeInfo) {
    log.append("Place info: ").append(placeInfo).append("\n");
  }

  @Override
  public void movePlayer(int playerIndex, int newPlaceNumber) throws IOException {
    logMethodCall("movePlayer");
    log.append("Player ").append(playerIndex)
        .append(" moved to place ").append(newPlaceNumber).append("\n");
  }

  @Override
  public void pickUpItem(String itemName) throws IOException {
    logMethodCall("pickUpItem");
    log.append("Item picked up: ").append(itemName).append("\n");
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

  @Override
  public boolean isPlayerVisible(Player player) {
    logMethodCall("isPlayerVisible");
    return playerVisible;
  }

  @Override
  public String getPlayerCurrentCarriedItems(int playerIndex) throws IOException {
    logMethodCall("getPlayerCurrentCarriedItems");
    return "[Sword-10]";
  }

  @Override
  public int getMaxTurns() {
    logMethodCall("getMaxTurns");
    return maxTurns;
  }

  public void setMaxTurns(int maxTurns) {
    this.maxTurns = maxTurns;
  }

  @Override
  public String getCurrentPlaceInfo(int placeNumber) throws IOException {
    logMethodCall("getCurrentPlaceInfo");
    return placeInfoToReturn != null ? placeInfoToReturn : "MockPlace;[];[]";
  }

}