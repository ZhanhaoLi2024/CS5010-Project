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
  private final List<Place> places;
  private final List<Item> items;
  private final Target targetCharacter;
  private final List<Player> players;
  private final Appendable output;
  private final Scanner scanner;
  private final Pet pet;
  private final String townName;
  private final String targetName;
  private final int targetHealth;
  private final int maxTurns;
  private int currentPlayerIndex;
  private int currentTurn;

  /**
   * Constructs a new TownModel with the specified town loader and filename.
   *
   * @param loader   the town loader to load the town data
   * @param filename the name of the file to load the town from
   * @throws IOException if an I/O error occurs
   */
  public TownModel(TownLoaderInterface loader, String filename, Readable scanner, Appendable output,
                   int maxTurns)
      throws IOException {
    TownData townData = loader.loadTown(filename);
    this.townName = townData.getTownName();
    this.targetName = townData.getTargetName();
    this.targetHealth = townData.getTargetHealth();
    this.pet = new PetModel(townData.getPetName());
    this.places = townData.getPlaces();
    this.items = townData.getItems();
    this.targetCharacter = new TargetModel(targetName, targetHealth, places.get(0), places);
    this.players = new ArrayList<>();
    this.currentPlayerIndex = 0;
    this.scanner = new Scanner(scanner);
    this.output = output;
    this.currentTurn = 1;
    this.maxTurns = maxTurns;
  }

  @Override
  public boolean isGameOver() {
    return currentTurn > maxTurns || targetCharacter.isDefeated();
  }

  @Override
  public void getPlaceInfo(Place place) {
    System.out.println("Place: " + place.getName());
    System.out.println("Items in the place:");
    for (Item item : place.getItems()) {
      System.out.println("- " + item.getName() + " (Damage: " + item.getDamage() + ")");
    }
    System.out.println("Neighboring places:");
    for (Place neighbor : place.getNeighbors()) {
      System.out.println("- " + neighbor.getName());
    }
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

  @Override
  public void addComputerPlayer() throws IOException {
    Random random = new Random();
    Place randomPlace = getPlaces().get(random.nextInt(getPlaces().size()));
    Player player = new PlayerModel("David(Computer)", true, 3, 1, System.out, scanner);
    players.add(player);
    output.append("Computer player 'David' added.\n");
  }

  @Override
  public void addPlayer() throws IOException {
    output.append("Enter the player's name:\n");
    String playerName = scanner.nextLine();
    output.append("Enter your starting place number:\n");
    String placeIndex = scanner.nextLine();
    int placeNumber = Integer.parseInt(placeIndex);
    Place startingPlace = places.get(Integer.parseInt(placeIndex) - 1);
    output.append("Enter your limit of carrying items:\n");
    String carryLimit = scanner.nextLine();
    Player player = new PlayerModel(playerName, false, Integer.parseInt(carryLimit),
        placeNumber,
        output, scanner);
    output.append("Player name: ").append(player.getName()).append("\n");
    output.append(player.getName()).append("Current place: ").append(startingPlace.getName())
        .append("\n");
    output.append("You can carry up to ").append(String.valueOf(player.getCarryLimit()))
        .append(" items.\n");
    output.append("Player added.\n");
    players.add(player);
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
  public void getPlaceByName(String PlaceName) throws IOException {
    Place place = getPlaces().stream()
        .filter(p -> p.getName().equals(PlaceName))
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
      String currentPlaceName = getPlaceByNumber(player.getPlayerCurrentPlaceNumber()).getName();
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
  public void showPlayerCurrentInfo() throws IOException {
    String playerName = players.get(currentPlayerIndex).getName();
    getPlayerByName(playerName);
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
    List<Item> items = currentPlace.getItems();
    output.append("Items in ").append(currentPlace.getName()).append(":\n");
    if (items.isEmpty()) {
      output.append("No items found.\n");
    } else {
      for (Item item : items) {
        output.append(item.getName()).append(" (Damage: ")
            .append(String.valueOf(item.getDamage()))
            .append(")\n");
      }
    }
    // Show players in current place
    List<Player> currentPlacePlayers = new ArrayList<>();
    for (Player p : players) {
      Place pCurrentPlace = getPlaceByNumber(p.getPlayerCurrentPlaceNumber());
      if (pCurrentPlace.equals(currentPlace) && !p.equals(currentPlayer)) {
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
          Place pCurrentPlace = getPlaceByNumber(p.getPlayerCurrentPlaceNumber());
          if (pCurrentPlace.equals(neighbor)) {
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
    } else {
      output.append("Neighbors of ").append(currentPlace.getName())
          .append(":\n");
      for (int i = 0; i < neighbors.size(); i++) {
        output.append(String.valueOf(i + 1)).append(". ").append(neighbors.get(i).getName())
            .append("\n");
      }
      output.append("Enter the number of the neighbor to move to:\n");
      int neighborNumber = Integer.parseInt(scanner.nextLine());
      if (neighborNumber < 1 || neighborNumber > neighbors.size()) {
        output.append("Invalid neighbor number.\n");
      } else {
        int playerMoveNumber = getPlaceNumberByName(neighbors.get(neighborNumber - 1).getName());
        currentPlayer.moveToPlaceNumber(playerMoveNumber);
        output.append("Moved to ").append(neighbors.get(neighborNumber - 1).getName()).append("\n");
      }
    }

    this.switchToNextPlayer();
  }

  @Override
  public void pickUpItem() throws IOException {
    Player currentPlayer = this.players.get(currentPlayerIndex);
    Place currentPlace = getPlaceByNumber(currentPlayer.getPlayerCurrentPlaceNumber());
    List<Item> items = currentPlace.getItems();
    if (items.isEmpty()) {
      output.append("No items found.\n");
      return;
    } else {
      if (currentPlayer.isComputerControlled()) {
        Item item = items.get(new Random().nextInt(items.size()));
        currentPlayer.pickUpItem(item);
        currentPlace.removeItem(item);
        output.append("Picked up ").append(item.getName()).append(".\n");
      } else {
        output.append("Items in ").append(currentPlace.getName()).append(":\n");
        for (int i = 0; i < items.size(); i++) {
          int currentIndex = i + 1;
          output.append(String.valueOf(currentIndex)).append(". ").append(items.get(i).getName())
              .append(" (Damage: ").append(String.valueOf(items.get(i).getDamage())).append(")\n");
        }
        output.append("Enter the item number to pick up:\n");
        int itemNumber = Integer.parseInt(scanner.nextLine());
        if (itemNumber < 1 || itemNumber > items.size()) {
          output.append("Invalid item number.\n");
        } else {
          Item item = items.get(itemNumber - 1);
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
    if (getPlayers().size() == 1) {
      output.append("You have to add at least one player\n");
    }
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

    if (currentPlayerIndex == 0) {
      currentTurn++;
    }
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
    Place playerPlace = getPlaceByNumber(player.getPlayerCurrentPlaceNumber());

    for (Player otherPlayer : players) {
      if (otherPlayer != player) {
        Place otherPlace = getPlaceByNumber(otherPlayer.getPlayerCurrentPlaceNumber());
        if (playerPlace.equals(otherPlace) || playerPlace.isNeighbor(otherPlace)) {
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
    currentTurn++;
    showTargetInfo();
    showPlayerCurrentInfo();
  }
}
