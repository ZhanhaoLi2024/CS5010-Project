package model.town;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import model.item.Item;
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
  private String townName;
  private String targetName;
  private int targetHealth;
  private int currentPlayerIndex;
  private int currentTurn;
  private int maxTurns;

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
  public Boolean isGameOver() {
    return currentTurn > maxTurns;
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
  public void moveCharacter() {
    targetCharacter.moveToNextPlace();
  }

  @Override
  public String getName() {
    return townName;
  }

  @Override
  public Target getCharacter() {
    return targetCharacter;
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
    Player player = new PlayerModel("David(Computer)", true, 3, randomPlace, System.out, scanner);
    players.add(player);
    output.append("Computer player 'David' added.\n");
  }

  @Override
  public void addPlayer() throws IOException {
    output.append("Enter the player's name:\n");
    String playerName = scanner.nextLine();
    output.append("Enter your starting place number:\n");
    String placeIndex = scanner.nextLine();
    Place startingPlace = places.get(Integer.parseInt(placeIndex) - 1);
    output.append("Enter your limit of carrying items:\n");
    String carryLimit = scanner.nextLine();
    Player player = new PlayerModel(playerName, false, Integer.parseInt(carryLimit), startingPlace,
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
      output.append("Player current place: ").append(player.getCurrentPlace().getName())
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
      output.append("Player current place: ").append(player.getCurrentPlace().getName())
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
    Player currentPlayer = this.players.get(currentPlayerIndex);
    List<Place> neighbors = currentPlayer.getCurrentPlace().getNeighbors();
    if (neighbors.isEmpty()) {
      output.append("No neighbors found.\n");
    } else {
      output.append("Neighbors of ").append(currentPlayer.getCurrentPlace().getName())
          .append(":\n");
      for (Place neighbor : neighbors) {
        output.append(neighbor.getName()).append("\n");
      }
    }
    List<Item> items = currentPlayer.getCurrentPlace().getItems();
    if (items.isEmpty()) {
      output.append("No items found.\n");
    } else {
      output.append("Items in ").append(currentPlayer.getCurrentPlace().getName()).append(":\n");
      for (Item item : items) {
        output.append(item.getName()).append(" (Damage: ")
            .append(String.valueOf(item.getDamage()))
            .append(")\n");
      }
    }
    for (Player p : players) {
      if (p.getCurrentPlace().equals(currentPlayer.getCurrentPlace()) && !p.equals(currentPlayer)) {
        output.append(p.getName()).append(" is in this place.\n");
      }
    }

    this.switchToNextPlayer(); // Next player turn
  }

  @Override
  public void movePlayer() throws IOException {
    Player currentPlayer = this.players.get(currentPlayerIndex);
    List<Place> neighbors = currentPlayer.getCurrentPlace().getNeighbors();
    if (neighbors.isEmpty()) {
      output.append("No neighbors found.\n");
    } else {
      output.append("Neighbors of ").append(currentPlayer.getCurrentPlace().getName())
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
        Place newPlace = neighbors.get(neighborNumber - 1);
        currentPlayer.moveTo(newPlace);
        output.append("Moved to ").append(newPlace.getName()).append("\n");
      }
    }

    this.switchToNextPlayer();
  }

  @Override
  public void pickUpItem() throws IOException {
    Player currentPlayer = this.players.get(currentPlayerIndex);
    List<Item> items = currentPlayer.getCurrentPlace().getItems();
    if (items.isEmpty()) {
      output.append("No items found.\n");
      return;
    } else {
      if (currentPlayer.isComputerControlled()) {
        Item item = items.get(new Random().nextInt(items.size()));
        currentPlayer.pickUpItem(item);
        currentPlayer.getCurrentPlace().removeItem(item);
        output.append("Picked up ").append(item.getName()).append(".\n");
      } else {
        output.append("Items in ").append(currentPlayer.getCurrentPlace().getName()).append(":\n");
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
          currentPlayer.getCurrentPlace().removeItem(item);
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

}
