package model.town;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.item.Item;
import model.pet.Pet;
import model.pet.PetModel;
import model.place.Place;
import model.player.Player;
import model.player.PlayerModel;
import model.target.Target;
import model.target.TargetModel;

/**
 * The TownModel class implements the Town interface and represents a town in a game. A town has a
 * name, a list of places, a list of items, and a target character that can move between different
 * places.
 */
public class TownModel implements Town {
  private final Appendable output;
  private final int maxTurns;
  private final String worldFile;
  private final TownLoaderInterface loader;
  private List<Place> places;
  private List<Item> items;
  private List<Player> players;
  private String targetName;
  private int targetHealth;
  private Target targetCharacter;
  private Pet pet;
  private int currentPlayerIndex;
  private int currentTurn;

  /**
   * Constructs a new TownModel with the specified town loader and filename.
   *
   * @param townLoader   the town loader to load the town data
   * @param filename     the name of the file to load the town from
   * @param townOutput   the appendable to write output to
   * @param townMaxTurns the maximum number of turns allowed in the game
   * @throws IOException if an I/O error occurs
   */
  public TownModel(TownLoaderInterface townLoader, String filename,
                   Appendable townOutput,
                   int townMaxTurns)
      throws IOException {
    if (townMaxTurns <= 0) {
      throw new IllegalArgumentException("Maximum turns must be positive");
    }
    this.loader = townLoader;
    TownData townData = townLoader.loadTown(filename);
    this.targetName = townData.getTargetName();
    this.targetHealth = townData.getTargetHealth();
    this.places = townData.getPlaces();
    this.items = townData.getItems();
    this.targetCharacter = new TargetModel(targetName, targetHealth, places.get(0), places);
    int targetCurrentPlaceNumber =
        Integer.parseInt(targetCharacter.getCurrentPlace().getPlaceNumber());
    this.pet = new PetModel(townData.getPetName(), targetCurrentPlaceNumber);
    this.players = new ArrayList<>();
    this.currentPlayerIndex = 0;
    this.output = townOutput;
    this.currentTurn = 1;
    this.maxTurns = townMaxTurns;
    this.worldFile = filename;
  }

  @Override
  public List<String> getAllPlayersInfo() {
    List<String> playerInfo = new ArrayList<>();
    if (players.isEmpty()) {
      return playerInfo;
    }
    for (Player player : players) {
      System.out.println(player.getName());
      playerInfo.add(
          player.getName() + "," + getPlaceByNumber(player.getPlayerCurrentPlaceNumber()).getName()
              + "," + player.getCarryLimit() + ";");
    }
    return playerInfo;
  }

  /**
   * Resets the game state to the initial state.
   */
  @Override
  public void resetGameState() throws IOException {
    TownData townData = loader.loadTown(this.worldFile);
    this.players = new ArrayList<>();
    this.currentPlayerIndex = 0;
    this.targetName = townData.getTargetName();
    this.targetHealth = townData.getTargetHealth();
    this.targetCharacter = new TargetModel(targetName, targetHealth, places.get(0), places);
    int targetCurrentPlaceNumber =
        Integer.parseInt(targetCharacter.getCurrentPlace().getPlaceNumber());
    this.pet = new PetModel(townData.getPetName(), targetCurrentPlaceNumber);
    this.items = townData.getItems();
    this.places = townData.getPlaces();
    this.currentTurn = 1;

  }

  @Override
  public boolean isGameOver() {
    return currentTurn > maxTurns || targetCharacter.isDefeated();
  }

  @Override
  public String petCurrentInfo() {
    return pet.getName() + ","
        + getPlaceByNumber(pet.getPetCurrentPlaceNumber()).getName();

  }

  @Override
  public Place getPlaceByNumber(int placeNumber) {
    return places.get(placeNumber - 1);
  }

  @Override
  public void moveTarget() {
    targetCharacter.moveToNextPlace();
  }

  @Override
  public Target getTarget() {
    return targetCharacter;
  }

  @Override
  public List<Place> getPlaces() {
    return places;
  }

  @Override
  public String getTargetName() {
    return targetName;
  }

  @Override
  public int getTargetHealth() {
    return targetHealth;
  }

  @Override
  public List<Player> getPlayers() {
    return players;
  }

  @Override
  public Integer getPlayerCurrPlaceNumber(int playerIndex) {
    return players.get(playerIndex).getPlayerCurrentPlaceNumber();
  }

  @Override
  public String getCurrentPlaceInfo(int placeNumber) throws IOException {
    Place place = getPlaceByNumber(placeNumber);
    List<String> currPlaceItems = new ArrayList<>();
    for (Item item : place.getItems()) {
      currPlaceItems.add(item.getName() + "-" + item.getDamage());
    }
    List<String> playerPlayers = new ArrayList<>();
    for (Player player : place.getCurrentPlacePlayers()) {
      playerPlayers.add(player.getName());
    }
    return place.getName() + ";" + currPlaceItems + ";" + playerPlayers;
  }

  @Override
  public String getCurrentPlaceNeighborsInfo(int placeNumber) throws IOException {
    List<List<String>> neighbors = new ArrayList<>();
    Place place = getPlaceByNumber(placeNumber);
    for (Place p : places) {
      boolean isTarget = p.equals(targetCharacter.getCurrentPlace());
      boolean isPet = p.getPlaceNumber().equals(String.valueOf(pet.getPetCurrentPlaceNumber()));
      if (!p.equals(place) && place.isNeighbor(p)) {
        List<String> items = new ArrayList<>();
        for (Item item : p.getItems()) {
          items.add(item.getName() + "-" + item.getDamage());
        }
        List<String> neighborPlayers = new ArrayList<>();
        for (Player player : p.getCurrentPlacePlayers()) {
          neighborPlayers.add(player.getName());
        }
        neighbors.add(Collections.singletonList(
            p.getName() + ";" + p.getPlaceNumber() + ";" + items + ";" + neighborPlayers + ";"
                + isTarget + ";" + isPet));
      }
    }
    return neighbors.toString();
  }

  @Override
  public void addPlayer(String playerName, int placeNumber, int carryLimit,
                        boolean isComputerController) {
    Place startingPlace = places.get(placeNumber - 1);
    Player player = new PlayerModel(playerName, isComputerController, carryLimit, placeNumber);
    players.add(player);
    startingPlace.addPlayer(player);
  }

  @Override
  public String showBasicLocationInfo() throws IOException {
    Player currentPlayer = players.get(currentPlayerIndex);
    Place currentPlace = getPlaceByNumber(currentPlayer.getPlayerCurrentPlaceNumber());

    List<List<String>> playerInfo = new ArrayList<>();
    List<String> playerBasicInfo = new ArrayList<>();
    List<Item> currentCarriedItems = currentPlayer.getCurrentCarriedItems();
    StringBuilder carryItems = new StringBuilder();
    if (currentCarriedItems.isEmpty()) {
      carryItems = new StringBuilder("None");
    } else {
      for (Item item : currentCarriedItems) {
        carryItems.append(item.getName()).append("-").append(item.getDamage()).append("|");
      }
    }
    playerBasicInfo.add(currentPlayer.getName() + "," + currentPlace.getName() + ","
        + currentPlayer.getCarryLimit() + "," + carryItems);
    playerInfo.add(playerBasicInfo);

    // Show other players in the same room
    List<String> playerNeighbours = new ArrayList<>();
    for (Player p : players) {
      Place currentPlaceOfP = getPlaceByNumber(p.getPlayerCurrentPlaceNumber());
      if (currentPlaceOfP.equals(currentPlace) && !p.equals(currentPlayer)) {
        playerNeighbours.add(p.getName());
      }
    }
    playerInfo.add(playerNeighbours);


    // show target current info(name, place, health)
    List<String> targetInfo = new ArrayList<>();
    targetInfo.add(targetCharacter.getName() + "," + targetCharacter.getCurrentPlace().getName()
        + "," + targetCharacter.getHealth());
    playerInfo.add(targetInfo);

    // Show pet current place
    List<String> petInfo = new ArrayList<>();
    petInfo.add(
        pet.getName() + "," + getPlaceByNumber(pet.getPetCurrentPlaceNumber()).getName());
    playerInfo.add(petInfo);

    // Show available items
    List<String> itemsInfo = new ArrayList<>();
    if (!currentPlace.getItems().isEmpty()) {
      for (Item item : currentPlace.getItems()) {
        itemsInfo.add(item.getName() + "," + item.getDamage());
      }
      playerInfo.add(itemsInfo);
    }

    return playerInfo.toString();
  }

  @Override
  public String getPlayerByName(String playerName) throws IOException {
    String playerInfo = "";
    if (players.isEmpty()) {
      output.append("No players found.\n");
      return playerInfo;
    }
    Player player = players.stream()
        .filter(p -> p.getName().equals(playerName))
        .findFirst()
        .orElse(null);
    if (player != null) {
      playerInfo =
          player.getName() + "," + getPlaceByNumber(player.getPlayerCurrentPlaceNumber()).getName()
              + "," + player.getCarryLimit();
      return playerInfo;
    } else {
      return playerInfo;
    }
  }

  @Override
  public void lookAround() throws IOException {
    this.switchToNextPlayer();
  }

  @Override
  public void movePlayer(int playerIndex, int newPlaceNumber) throws IOException {
    Player player = players.get(playerIndex);
    Place oldPlace = getPlaceByNumber(player.getPlayerCurrentPlaceNumber());
    Place newPlace = getPlaceByNumber(newPlaceNumber);

    oldPlace.removePlayer(player);
    newPlace.addPlayer(player);
    player.moveToPlaceNumber(newPlaceNumber);

    this.switchToNextPlayer();

  }

  @Override
  public void pickUpItem(String itemName) throws IOException {
    Player currentPlayer = this.players.get(currentPlayerIndex);
    Place currentPlace = getPlaceByNumber(currentPlayer.getPlayerCurrentPlaceNumber());
    List<Item> townItems = this.items;

    for (Item item : townItems) {
      if (item.getName().equals(itemName)) {
        currentPlayer.pickUpItem(item);
        currentPlace.removeItem(item);
      }
    }

    this.switchToNextPlayer();
  }

  @Override
  public void movePet(int newPlaceNumber) throws IOException {
    pet.movePet(newPlaceNumber);

    this.switchToNextPlayer();
  }

  private Item getItemByName(String itemName) {
    for (Item item : items) {
      if (item.getName().equals(itemName)) {
        return item;
      }
    }
    return null;
  }

  @Override
  public boolean attackTarget(String attackItemName) throws IOException {
    Player currentPlayer = players.get(currentPlayerIndex);
    if ("Poke Target".equals(attackItemName)) {
      System.out.println("Poke Target");
      return this.executePoke(currentPlayer);
    }

    Item attackItem = getItemByName(attackItemName);

    return this.executeItemAttack(currentPlayer, attackItem);

  }

  @Override
  public void switchToNextPlayer() throws IOException {
    if (players.size() <= 1) {
      return;
    }

    // Save previous player for reference
    Player previousPlayer = players.get(currentPlayerIndex);

    // Update player index and turn counter
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    if (currentPlayerIndex == 0) {
      currentTurn++;
      // Move target when we complete a full round
      moveTarget();
    }

    // Notify about turn change
    Player currentPlayer = players.get(currentPlayerIndex);
    output.append("\nTurn changed from ").append(previousPlayer.getName())
        .append(" to ").append(currentPlayer.getName())
        .append(" (Turn ").append(String.valueOf(currentTurn))
        .append(")\n");
  }

  @Override
  public int getCurrentTurn() {
    return currentTurn;
  }

  @Override
  public Boolean isComputerControllerPlayer() {
    return players.get(currentPlayerIndex).isComputerControlled();
  }

  /**
   * Gets the index of the current player.
   *
   * @return the index of the current player
   */
  @Override
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  @Override
  public boolean isPlayerVisible(Player player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    Place playerPlace = getPlaceByNumber(player.getPlayerCurrentPlaceNumber());
    for (Player otherPlayer : players) {
      if (otherPlayer != player) {
        Place otherPlace = getPlaceByNumber(otherPlayer.getPlayerCurrentPlaceNumber());
        Place petPlace = getPlaceByNumber(pet.getPetCurrentPlaceNumber());
        if ((playerPlace.equals(otherPlace)
            || (playerPlace.isNeighbor(otherPlace) && !petPlace.equals(playerPlace)))) {
          return true;
        }
      }
    }
    return false;
  }


  private boolean executeItemAttack(Player player, Item item) throws IOException {
    if (player == null || item == null) {
      throw new IllegalArgumentException("Player and item cannot be null");
    }

    // Execute attack (always successful if not seen)
    final boolean targetDefeated = targetCharacter.takeDamage(item.getDamage());

    targetHealth = targetCharacter.getHealth();
    System.out.println("Target health: " + targetHealth);

    // Remove used item from player's inventory
    player.getCurrentCarriedItems().remove(item);

    return targetDefeated;
  }

  private boolean executePoke(Player player) throws IOException {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }

    // Poke attack (always successful if not seen)
    boolean targetDefeated = targetCharacter.takeDamage(1); // true - 死

    targetHealth = targetCharacter.getHealth();
    System.out.println("Target health: " + targetHealth);

    return targetDefeated;
  }

  @Override
  public String getPlayerCurrentCarriedItems(int playerIndex) throws IOException {
    Player player = players.get(playerIndex);
    List<Item> carriedItems = player.getCurrentCarriedItems();
    List<String> itemNames = new ArrayList<>();
    for (Item item : carriedItems) {
      itemNames.add(item.getName() + "-" + item.getDamage());
    }
    return itemNames.toString();
  }

  @Override
  public int getMaxTurns() {
    return maxTurns;
  }
}
