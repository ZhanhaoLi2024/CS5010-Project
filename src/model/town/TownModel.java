package model.town;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import model.item.Item;
import model.item.ItemModel;
import model.place.Place;
import model.place.PlaceModel;
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

  /**
   * Constructs a new TownModel with the specified town loader and filename.
   *
   * @param loader   the town loader to load the town data
   * @param filename the name of the file to load the town from
   * @throws IOException if an I/O error occurs
   */
  public TownModel(TownLoaderInterface loader, String filename, Readable scanner, Appendable output)
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
  }

  @Override
  public void loadTown(String filename) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filename));

    String[] townInfo = br.readLine().split(" ");
    int numRows = Integer.parseInt(townInfo[0]);
    int numCols = Integer.parseInt(townInfo[1]);
    townName = String.join(" ", Arrays.copyOfRange(townInfo, 2, townInfo.length));

    String[] targetInfo = br.readLine().split(" ");
    targetHealth = Integer.parseInt(targetInfo[0]);
    targetName = String.join(" ", Arrays.copyOfRange(targetInfo, 1, targetInfo.length));

    int numPlaces = Integer.parseInt(br.readLine());
    for (int i = 0; i < numPlaces; i++) {
      String[] placeInfo = br.readLine().split(" ");
      int row1 = Integer.parseInt(placeInfo[0]);
      int col1 = Integer.parseInt(placeInfo[1]);
      int row2 = Integer.parseInt(placeInfo[2]);
      int col2 = Integer.parseInt(placeInfo[3]);
      String placeName = String.join(" ", Arrays.copyOfRange(placeInfo, 4, placeInfo.length));
      Place place = new PlaceModel(row1, col1, row2, col2, placeName, String.valueOf(i + 1));
      places.add(place);
    }

    for (Place place : places) {
      for (Place otherPlace : places) {
        if (!place.equals(otherPlace) && place.isNeighbor(otherPlace)) {
          place.addNeighbor(otherPlace);
        }
      }
    }

    int numItems = Integer.parseInt(br.readLine());
    for (int i = 0; i < numItems; i++) {
      String[] itemInfo = br.readLine().split(" ");
      int placeIndex = Integer.parseInt(itemInfo[0]);
      int damage = Integer.parseInt(itemInfo[1]);
      String itemName = String.join(" ", Arrays.copyOfRange(itemInfo, 2, itemInfo.length));
      Item item = new ItemModel(itemName, damage);
      places.get(placeIndex).addItem(item);
    }

    br.close();
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
//    for (Place place : places) {
//      players.addAll(place.getCurrentPlacePlayers());
//    }
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
}
