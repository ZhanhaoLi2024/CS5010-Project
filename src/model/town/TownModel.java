package model.town;

import java.io.IOException;
import java.util.ArrayList;
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
  public void showPetCurrentInfo() throws IOException {
    output.append("Pet info: ").append(pet.getName()).append("is in ")
        .append(getPlaceByNumber(pet.getPetCurrentPlaceNumber()).getName()).append("\n");
  }

  @Override
  public Place getPlaceByNumber(int placeNumber) {
    return places.get(placeNumber - 1);
  }

  @Override
  public int getPlaceNumberByName(String placeName) throws IOException {
    for (int i = 0; i < places.size(); i++) {
      if (places.get(i).getName().equals(placeName)) {
        return i + 1;
      }
    }
    output.append("Place not found.\n");
    return -1;
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

  /**
   * Moves the pet to a new location.
   *
   * @param placeNumber the number of the place to move the pet to
   * @throws IllegalArgumentException if the place number is invalid
   */
  @Override
  public void movePet(int placeNumber) throws IOException {
    // Validate place number
    if (placeNumber <= 0 || placeNumber > places.size()) {
      throw new IllegalArgumentException("Invalid place number!");
    }

    // Move the pet
    pet.movePet(placeNumber);
    output.append(String.format("Pet %s moved to %s\n",
        pet.getName(),
        getPlaceByNumber(placeNumber).getName()));
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
  public List<Place> getCurrentPlaceNeighbors(Place place) {
    List<Place> neighbors = new ArrayList<>();
    for (Place p : places) {
      if (!p.equals(place) && place.isNeighbor(p)) {
        neighbors.add(p);
      }
    }
    return neighbors;
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
  public void showAllPlacesInfo() throws IOException {
    if (places.isEmpty()) {
      output.append("No places found.\n");
      return;
    }
    output.append("All places info:\n");
    int index = 1;
    for (Place place : places) {
      output.append("----------\n");
      output.append("Place Number: ").append(String.valueOf(index)).append(". Place name: ")
          .append(place.getName()).append("\n");
      output.append("----------\n");
      output.append("Place items:\n");
      for (Item item : place.getItems()) {
        output.append("Item name: ").append(item.getName()).append("\n");
        output.append("Item damage: ").append(String.valueOf(item.getDamage())).append("\n");
      }
      output.append("----------\n");
      output.append("Place neighbors:\n");
      for (Place neighbor : place.getNeighbors()) {
        output.append("Neighbor name: ").append(neighbor.getName()).append("\n");
      }
      output.append("----------\n");
      if (players.isEmpty()) {
        output.append("No players in this place.\n");
      } else {
        for (Player player : players) {
          Place currentPlace = getPlaceByNumber(player.getPlayerCurrentPlaceNumber());
          if (currentPlace.equals(place)) {
            output.append(player.getName()).append(" is in this place.\n");
          }
        }
      }
      index++;
    }
  }

  @Override
  public void getPlaceByName(String placeNameString) throws IOException {
    Place place = getPlaces().stream()
        .filter(p -> p.getName().equals(placeNameString))
        .findFirst()
        .orElse(null);
    if (place != null) {
      output.append("Place name: ").append(place.getName()).append("\n");
      output.append("Place items:\n");
      for (Item item : place.getItems()) {
        output.append("Item name: ").append(item.getName()).append("\n");
        output.append("Item damage: ").append(String.valueOf(item.getDamage())).append("\n");
      }
      output.append("Place neighbors:\n");
      for (Place neighbor : place.getNeighbors()) {
        output.append("Neighbor name: ").append(neighbor.getName()).append("\n");
      }
      if (players.isEmpty()) {
        output.append("No players in this place.\n");
      } else {
        for (Player player : players) {
          Place currentPlace = getPlaceByNumber(player.getPlayerCurrentPlaceNumber());
          if (currentPlace.equals(place)) {
            output.append(player.getName()).append(" is in this place.\n");
          }
        }
      }
    } else {
      output.append("Place not found.\n");
    }
  }

  @Override
  public void showAllPlayersInfo() throws IOException {
    if (players.isEmpty()) {
      output.append("No players found.\n");
      return;
    }
    output.append("All players info:\n");
    int index = 1;
    output.append("--------------------\n");
    for (Player player : players) {
      output.append(String.valueOf(index)).append(". Player name: ").append(player.getName())
          .append("\n");
      output.append("Player current place: ")
          .append(getPlaceByNumber(player.getPlayerCurrentPlaceNumber()).getName())
          .append("\n");
      output.append("This player can carry up to ").append(String.valueOf(player.getCarryLimit()))
          .append(" items.\n");
      output.append("--------------------\n");
      index++;
    }
  }

  @Override
  public void showBasicLocationInfo() throws IOException {
    Player currentPlayer = players.get(currentPlayerIndex);
    Place currentPlace = getPlaceByNumber(currentPlayer.getPlayerCurrentPlaceNumber());

    // Show current place name
    output.append("\nHi ").append(currentPlayer.getName()).append(", you are in ")
        .append(currentPlace.getName())
        .append("\n");

    // Show other players in the same room
    List<Player> currentPlacePlayers = new ArrayList<>();
    for (Player p : players) {
      Place currentPlaceOfP = getPlaceByNumber(p.getPlayerCurrentPlaceNumber());
      if (currentPlaceOfP.equals(currentPlace) && !p.equals(currentPlayer)) {
        currentPlacePlayers.add(p);
      }
    }
    if (!currentPlacePlayers.isEmpty()) {
      output.append("Players in this place:\n");
      if (currentPlacePlayers.size() == 1) {
        output.append(" - ").append(currentPlacePlayers.get(0).getName()).append("\n");
      } else {
        for (Player player : currentPlacePlayers) {
          output.append(player.getName()).append(", ");
        }
        output.append("\n");
      }
    }

    // show target current place
    output.append("Target is in ").append(targetCharacter.getCurrentPlace().getName()).append("\n");

    // Show pet current place
    output.append("Pet is in ").append(getPlaceByNumber(pet.getPetCurrentPlaceNumber()).getName())
        .append("\n");

    // Show available items
    if (!currentPlace.getItems().isEmpty()) {
      for (Item item : currentPlace.getItems()) {
        output.append("Item in this place: \n");
        output.append(" - ").append(item.getName()).append(" (Damage: ")
            .append(String.valueOf(item.getDamage())).append(")\n");
      }
    }
  }

  @Override
  public void getPlayerByName(String playerName) throws IOException {
    if (players.isEmpty()) {
      output.append("No players found.\n");
      return;
    }
    Player player = players.stream()
        .filter(p -> p.getName().equals(playerName))
        .findFirst()
        .orElse(null);
    if (player != null) {
      output.append("Player name: ").append(player.getName()).append("\n");
      output.append("Player current place: ")
          .append(getPlaceByNumber(player.getPlayerCurrentPlaceNumber()).getName())
          .append("\n");
      output.append("This player can carry up to ").append(String.valueOf(player.getCarryLimit()))
          .append(" items.\n");
      if (player.getCurrentCarriedItems().isEmpty()) {
        output.append("Player is not carrying any items.\n");
      } else {
        output.append("Player is carrying the following items:\n");
        for (Item item : player.getCurrentCarriedItems()) {
          output.append("- ").append(item.getName()).append(" (Damage: ")
              .append(String.valueOf(item.getDamage())).append(")\n");
        }
      }
    } else {
      output.append("Player not found.\n");
    }
  }

  @Override
  public void lookAround() throws IOException {
    if (players.isEmpty()) {
      throw new IllegalStateException("Cannot look around: No players in the game");
    }

    Player currentPlayer = this.players.get(currentPlayerIndex);
    Place currentPlace = getPlaceByNumber(currentPlayer.getPlayerCurrentPlaceNumber());

    // Current Place Info: name, items, players
    // First show current place name
    output.append("Current place: ").append(currentPlace.getName()).append("\n");
    // Show items in current place
    List<Item> currentPlaceItems = currentPlace.getItems();
    output.append("Items in ").append(currentPlace.getName()).append(":\n");
    if (currentPlaceItems.isEmpty()) {
      output.append("No items found.\n");
    } else {
      for (Item item : currentPlaceItems) {
        output.append(item.getName()).append(" (Damage: ")
            .append(String.valueOf(item.getDamage()))
            .append(")\n");
      }
    }
    // Show players in current place
    List<Player> currentPlacePlayers = new ArrayList<>();
    for (Player p : players) {
      Place currentPlaceOfP = getPlaceByNumber(p.getPlayerCurrentPlaceNumber());
      if (currentPlaceOfP.equals(currentPlace) && !p.equals(currentPlayer)) {
        currentPlacePlayers.add(p);
      }
    }
    if (!currentPlacePlayers.isEmpty()) {
      output.append("Players in this place:");
      if (currentPlacePlayers.size() == 1) {
        output.append(currentPlacePlayers.get(0).getName()).append("\n");
      } else {
        for (Player player : currentPlacePlayers) {
          output.append(player.getName()).append(", ");
        }
        output.append("\n");
      }
    }

    // Current Place Neighbors Info: name, items, players
    // Show neighbors
    List<Place> neighbors = currentPlace.getNeighbors();
    if (neighbors.isEmpty()) {
      output.append("No neighbors found.\n");
    } else {
      output.append("Neighbors of ").append(currentPlace.getName())
          .append(":\n");
      for (Place neighbor : neighbors) {
        output.append(" - ").append(neighbor.getName());

        // Check if pet is in this neighbor
        if (!isPlaceVisible(neighbor)) {
          output.append(" (Pet is here)\n");
          continue; // Skip showing items and players for spaces with pet
        }
        output.append("\n");

        // Show items in visible neighbors
        List<Item> neighborItems = neighbor.getItems();
        if (!neighborItems.isEmpty()) {
          output.append("   Items in ").append(neighbor.getName()).append(":");
          for (Item item : neighborItems) {
            output.append(item.getName()).append(" (Damage: ")
                .append(String.valueOf(item.getDamage()))
                .append(")\n");
          }
        }

        // Show players in visible neighbors
        List<Player> neighborPlayers = new ArrayList<>();
        for (Player p : players) {
          Place currentPlaceOfP = getPlaceByNumber(p.getPlayerCurrentPlaceNumber());
          if (currentPlaceOfP.equals(neighbor)) {
            neighborPlayers.add(p);
          }
        }
        if (!neighborPlayers.isEmpty()) {
          output.append("   Players in this place:");
          if (neighborPlayers.size() == 1) {
            output.append(neighborPlayers.get(0).getName()).append("\n");
          } else {
            for (Player player : neighborPlayers) {
              output.append(player.getName()).append(", ");
            }
            output.append("\n");
          }
        }
      }
    }
    output.append("\n");
    this.switchToNextPlayer(); // Next player turn
  }

  @Override
  public void movePlayer() throws IOException {
    Player currentPlayer = this.players.get(currentPlayerIndex);
    Place currentPlace = getPlaceByNumber(currentPlayer.getPlayerCurrentPlaceNumber());
    List<Place> neighbors = currentPlace.getNeighbors();

    if (neighbors.isEmpty()) {
      output.append("No neighbors found.\n");
      return;
    }

    if (currentPlayer.isComputerControlled()) {
      handleComputerPlayerMove(currentPlayer, neighbors);
    } else {
      handleHumanPlayerMove(currentPlayer, currentPlace, neighbors);
    }

    this.switchToNextPlayer();
  }

  /**
   * Handles movement logic for computer-controlled player.
   */
  private void handleComputerPlayerMove(Player player, List<Place> neighbors)
      throws IOException {
    // First try to move towards target if visible
    Place targetPlace = getTarget().getCurrentPlace();

    // If target is in a neighboring space, move there
    for (Place neighbor : neighbors) {
      if (neighbor.equals(targetPlace)) {
        movePlayerToNeighbor(player, neighbor);
        return;
      }
    }

    // Otherwise, move randomly
    Random random = new Random();
    int randomNeighbor = random.nextInt(neighbors.size());
    movePlayerToNeighbor(player, neighbors.get(randomNeighbor));
  }

  /**
   * Handles movement logic for human player.
   */
  private void handleHumanPlayerMove(Player player, Place currentPlace, List<Place> neighbors)
      throws IOException {
    output.append("Neighbors of ").append(currentPlace.getName())
        .append(":\n");
    for (int i = 0; i < neighbors.size(); i++) {
      output.append(String.valueOf(i + 1)).append(". ")
          .append(neighbors.get(i).getName())
          .append("\n");
    }
    output.append("Enter the number of the neighbor to move to:\n");

    try {
      int neighborNumber = Integer.parseInt(scanner.nextLine());
      if (neighborNumber < 1 || neighborNumber > neighbors.size()) {
        output.append("Invalid neighbor number.\n");
        return;
      }
      movePlayerToNeighbor(player, neighbors.get(neighborNumber - 1));
    } catch (NumberFormatException e) {
      output.append("Invalid input. Please enter a number.\n");
    }
  }

  /**
   * Moves a player to the specified neighboring place.
   */
  private void movePlayerToNeighbor(Player player, Place neighbor) throws IOException {
    int newPlaceNumber = getPlaceNumberByName(neighbor.getName());
    player.moveToPlaceNumber(newPlaceNumber);
    output.append("Moved to ").append(neighbor.getName()).append("\n");
  }

  @Override
  public void pickUpItem() throws IOException {
    Player currentPlayer = this.players.get(currentPlayerIndex);
    Place currentPlace = getPlaceByNumber(currentPlayer.getPlayerCurrentPlaceNumber());
    List<Item> currentItems = currentPlace.getItems();
    if (currentItems.isEmpty()) {
      output.append("No items found.\n");
      return;
    } else {
      if (currentPlayer.isComputerControlled()) {
        Item item = currentItems.get(new Random().nextInt(currentItems.size()));
        currentPlayer.pickUpItem(item);
        currentPlace.removeItem(item);
        output.append("Picked up ").append(item.getName()).append(".\n");
      } else {
        output.append("Items in ").append(currentPlace.getName()).append(":\n");
        for (int i = 0; i < currentItems.size(); i++) {
          int currentIndex = i + 1;
          output.append(String.valueOf(currentIndex)).append(". ")
              .append(currentItems.get(i).getName())
              .append(" (Damage: ").append(String.valueOf(currentItems.get(i).getDamage()))
              .append(")\n");
        }
        output.append("Enter the item number to pick up:\n");
        int itemNumber = Integer.parseInt(scanner.nextLine());
        if (itemNumber < 1 || itemNumber > currentItems.size()) {
          output.append("Invalid item number.\n");
        } else {
          Item item = currentItems.get(itemNumber - 1);
          currentPlayer.pickUpItem(item);
          currentPlace.removeItem(item);
          output.append("Picked up ").append(item.getName()).append(".\n");
        }
      }
    }
    this.switchToNextPlayer();
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

  /**
   * Displays the current state of the target character.
   *
   * @throws IOException if there is an error writing output
   */
  @Override
  public void showTargetInfo() throws IOException {
    output.append(String.format("Target: %s (Health: %d) is in %s\n",
        targetCharacter.getName(),
        targetCharacter.getHealth(),
        targetCharacter.getCurrentPlace().getName()));
  }

  @Override
  public void startTurn() throws IOException {
    if (players.isEmpty()) {
      throw new IllegalStateException("Cannot start turn: No players in the game");
    }
    currentTurn++;
    showTargetInfo();
    showBriefPlayerInfo();
  }

  /**
   * Displays brief information about the current player.
   *
   * @throws IOException if there is an error writing output
   */
  private void showBriefPlayerInfo() throws IOException {
    Player currentPlayer = players.get(currentPlayerIndex);
    Place currentPlace = getPlaceByNumber(currentPlayer.getPlayerCurrentPlaceNumber());

    output.append("\nCurrent player: ").append(currentPlayer.getName())
        .append("\nLocation: ").append(currentPlace.getName())
        .append("\nItems carried: ")
        .append(String.valueOf(currentPlayer.getCurrentCarriedItems().size()))
        .append("/").append(String.valueOf(currentPlayer.getCarryLimit()))
        .append("\n");
  }

  /**
   * Executes an attack with a specific item.
   *
   * @param player the player performing the attack
   * @param item   the item being used
   * @throws IOException              if there is an error with output
   * @throws IllegalArgumentException if player or item is null
   */
  @Override
  public void executeItemAttack(Player player, Item item) throws IOException {
    if (player == null || item == null) {
      throw new IllegalArgumentException("Player and item cannot be null");
    }

    String playerPlaceNumber = String.valueOf(player.getPlayerCurrentPlaceNumber());
    String targetPlaceNumber = targetCharacter.getCurrentPlace().getPlaceNumber();

    // Validate player is in same room as target
    if (!playerPlaceNumber.equals(targetPlaceNumber)) {
      output.append("Attack failed: You must be in the same room as the target!\n");
      return;
    }

    // Check if player is visible to others
    if (isPlayerVisible(player)) {
      output.append("Attack failed: Other players have witnessed your attempt!\n");
      return;
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
      output.append(player.getName()).append(" has eliminated the target!\n");
    }
  }

  /**
   * Executes a basic poke attack that deals 1 damage.
   *
   * @param player the player performing the poke attack
   * @throws IOException              if there is an error with output
   * @throws IllegalArgumentException if player is null
   */
  @Override
  public void executePoke(Player player) throws IOException {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }

    String playerPlaceNumber = String.valueOf(player.getPlayerCurrentPlaceNumber());
    String targetPlaceNumber = targetCharacter.getCurrentPlace().getPlaceNumber();

    if (!playerPlaceNumber.equals(targetPlaceNumber)) {
      output.append("Attack failed: You must be in the same room as the target!\n");
      return;
    }

    if (isPlayerVisible(player)) {
      output.append("Attack failed: Other players have witnessed your attempt!\n");
      return;
    }

    // Poke attack (always successful if not seen)
    boolean targetDefeated = targetCharacter.takeDamage(1);
    output.append("Successfully poked the target in the eye for 1 damage! \n");
    output.append("Target health: ").append(String.valueOf(targetCharacter.getHealth()))
        .append("\n");

    if (targetDefeated) {
      output.append(player.getName())
          .append(" has eliminated the target with a poke in the eye!\n");
    }
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
      executePoke(player);
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
        executePoke(player);
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
