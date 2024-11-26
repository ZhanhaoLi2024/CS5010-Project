package model.town;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
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
  private final Scanner scanner;
  private final String townName;
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
   * @param townScanner  the scanner to read input from
   * @param townOutput   the appendable to write output to
   * @param townMaxTurns the maximum number of turns allowed in the game
   * @throws IOException if an I/O error occurs
   */
  public TownModel(TownLoaderInterface townLoader, String filename, Readable townScanner,
                   Appendable townOutput,
                   int townMaxTurns)
      throws IOException {
    this.loader = townLoader;
    TownData townData = townLoader.loadTown(filename);
    this.townName = townData.getTownName();
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
    this.scanner = new Scanner(townScanner);
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
  public String petCurrentInfo() throws IOException {
//    output.append("Pet info: ").append(pet.getName()).append("is in ")
//        .append(getPlaceByNumber(pet.getPetCurrentPlaceNumber()).getName()).append("\n");
    String petInfo = pet.getName() + ","
        + getPlaceByNumber(pet.getPetCurrentPlaceNumber()).getName();
    return petInfo;

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
  public String getName() {
    return townName;
  }

  @Override
  public Target getTarget() {
    return targetCharacter;
  }

  @Override
  public Pet getPet() {
    return this.pet;
  }

  /**
   * Determines if a place is visible (not blocked by pet).
   *
   * @param place the place to check visibility for
   * @return true if the place is visible, false otherwise
   */
  @Override
  public boolean isPlaceVisible(Place place) {
    if (place == null) {
      throw new IllegalArgumentException("Place cannot be null");
    }
    return !place.getPlaceNumber().equals(String.valueOf(pet.getPetCurrentPlaceNumber()));
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
  public String getTownName() {
    return townName;
  }

  @Override
  public List<Item> getItems() {
    return items;
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
    List<String> currentPlace = new ArrayList<>();
    List<String> items = new ArrayList<>();
    for (Item item : place.getItems()) {
      items.add(item.getName() + "-" + item.getDamage());
    }
    List<String> players = new ArrayList<>();
    for (Player player : place.getCurrentPlacePlayers()) {
      players.add(player.getName());
    }
    return place.getName() + ";" + items + ";" + players;
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

  /**
   * Validates if a player name is unique (not already taken).
   *
   * @param name The name to check
   * @return true if the name is unique, false otherwise
   */
  private boolean isPlayerNameUnique(String name) {
    return players.stream()
        .noneMatch(p -> p.getName().equalsIgnoreCase(name));
  }

  @Override
  public void addPlayer(String playerName, int placeNumber, int carryLimit)
      throws IOException {

    output.append("Adding player...\n");
    output.append("Player name: ").append(playerName).append("\n");

    Place startingPlace = places.get(placeNumber - 1);
    Player player = new PlayerModel(playerName, false, carryLimit, placeNumber);
    players.add(player);
    startingPlace.addPlayer(player);

    // Confirm addition
    output.append("Player added successfully:\n")
        .append("Name: ").append(playerName).append("\n")
        .append("Location: ").append(startingPlace.getName()).append("\n")
        .append("Carry limit: ").append(String.valueOf(carryLimit)).append("\n");
  }

  @Override
  public void addComputerPlayer() throws IOException {
    // Generate a random valid position
    Random random = new Random();
    int randomPlaceNumber = random.nextInt(places.size()) + 1;

    // Generate a unique name
    String computerName = "Computer-" + (players.size() + 1);
    while (!isPlayerNameUnique(computerName)) {
      computerName = "Computer-" + random.nextInt(1000);
    }

    // Create computer player with random starting position
    Player player = new PlayerModel(computerName, true, 3, randomPlaceNumber);
    players.add(player);

    output.append("Computer player '").append(computerName)
        .append("' added at ").append(places.get(randomPlaceNumber - 1).getName())
        .append(".\n");
  }

  @Override
  public String showBasicLocationInfo() throws IOException {
    Player currentPlayer = players.get(currentPlayerIndex);
    Place currentPlace = getPlaceByNumber(currentPlayer.getPlayerCurrentPlaceNumber());

    List<List<String>> playerInfo = new ArrayList<>();
    List<String> playerBasicInfo = new ArrayList<>();
    playerBasicInfo.add(currentPlayer.getName() + "," + currentPlace.getName() + ","
        + currentPlayer.getCarryLimit());
    playerInfo.add(playerBasicInfo);

    // Show other players in the same room
    List<Player> currentPlacePlayers = new ArrayList<>();
    List<String> playerNeighbours = new ArrayList<>();
    for (Player p : players) {
      Place currentPlaceOfP = getPlaceByNumber(p.getPlayerCurrentPlaceNumber());
      if (currentPlaceOfP.equals(currentPlace) && !p.equals(currentPlayer)) {
        currentPlacePlayers.add(p);
        playerNeighbours.add(p.getName());
      }
    }
    playerInfo.add(playerNeighbours);


    // show target current place
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
    if (attackItemName.equals("Poke Target")) {
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

  /**
   * Checks if a player can be seen by other players.
   * A player is visible if another player is in the same room or a neighboring room.
   *
   * @param player the player to check visibility for
   * @return true if the player can be seen by others, false otherwise
   */
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

    // Remove used item from player's inventory
    player.getCurrentCarriedItems().remove(item);
    output.append("Attack successful with ").append(item.getName())
        .append(" for ").append(String.valueOf(item.getDamage())).append(" damage!\n");
    output.append("Target health: ").append(String.valueOf(targetCharacter.getHealth()))
        .append("\n");

    if (targetDefeated) {
      return true;
    } else {
      return false;
    }
  }

  private boolean executePoke(Player player) throws IOException {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }

//    String playerPlaceNumber = String.valueOf(player.getPlayerCurrentPlaceNumber());
//    String targetPlaceNumber = targetCharacter.getCurrentPlace().getPlaceNumber();

//    if (!playerPlaceNumber.equals(targetPlaceNumber)) {
//      output.append("Attack failed: You must be in the same room as the target!\n");
//      return;
//    }
//
//    if (isPlayerVisible(player)) {
//      output.append("Attack failed: Other players have witnessed your attempt!\n");
//      return;
//    }

    // Poke attack (always successful if not seen)
    boolean targetDefeated = targetCharacter.takeDamage(1); // true - 死
    output.append("Successfully poked the target in the eye for 1 damage! \n");
    output.append("Target health: ").append(String.valueOf(targetCharacter.getHealth()))
        .append("\n");

    if (targetDefeated) {
//      output.append(player.getName())
//          .append(" has eliminated the target with a poke in the eye!\n");
      return true;
    } else {
      return false;
    }
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

  /**
   * Executes an attack for a computer-controlled player.
   *
   * @param player the computer-controlled player
   * @throws IOException              if there is an error with output
   * @throws IllegalArgumentException if player is null or not computer-controlled
   */
  @Override
  public void executeComputerAttack(Player player) throws IOException {
    if (player == null || !player.isComputerControlled()) {
      throw new IllegalArgumentException("Player must be a non-null computer-controlled player");
    }

    // Computer always attempts to attack if in the same room as target
    List<Item> computerItems = player.getCurrentCarriedItems();

    if (computerItems.isEmpty()) {
//      executePoke(player);
      return;
    }

    // Find and use item with the highest damage
    Item bestItem = computerItems.stream()
        .max((i1, i2) -> Integer.compare(i1.getDamage(), i2.getDamage()))
        .get();

    executeItemAttack(player, bestItem);
  }

  /**
   * Handles attack options and execution for human players.
   *
   * @param player the human player making the attack
   * @throws IOException              if there is an error with output
   * @throws IllegalArgumentException if player is null or is computer-controlled
   */
  @Override
  public void handleHumanAttack(Player player) throws IOException {
    if (player == null || player.isComputerControlled()) {
      throw new IllegalArgumentException("Player must be a non-null human player");
    }

    List<Item> humanItems = player.getCurrentCarriedItems();

    output.append("\nChoose your attack:\n");
    output.append("0. Poke in the eye (1 damage)\n");
    // Show item options
    for (int i = 0; i < humanItems.size(); i++) {
      Item item = humanItems.get(i);
      output.append(String.valueOf(i + 1)).append(". Use ")
          .append(item.getName())
          .append(" (").append(String.valueOf(item.getDamage())).append(" damage)\n");
    }

    try {
      int choice = Integer.parseInt(scanner.nextLine());
      if (choice == 0) {
//        executePoke(player);
      } else if (choice >= 1 && choice <= humanItems.size()) {
        executeItemAttack(player, humanItems.get(choice - 1));
      } else {
        output.append("Invalid choice. Attack cancelled.\n");
      }
    } catch (NumberFormatException e) {
      output.append("Invalid input. Attack cancelled.\n");
    }
  }

  @Override
  public int getMaxTurns() {
    return maxTurns;
  }
}
