package model.town;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.item.Item;
import model.item.ItemModel;
import model.place.Place;
import model.place.PlaceModel;

/**
 * TownLoader is responsible for loading town data from a file.
 */
public class TownLoader implements TownLoaderInterface {

  /**
   * Loads the town data from the specified file.
   *
   * @param filename the name of the file to load the town data from
   * @return the town data loaded from the file
   * @throws IOException if an I/O error occurs
   */
  @Override
  public TownData loadTown(String filename) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filename));
    List<Place> places = new ArrayList<>();
    List<Item> items = new ArrayList<>();
    String townName;
    String targetName;
    String petName;
    int targetHealth;

    String[] townInfo = br.readLine().split(" ");
    townName = String.join(" ", Arrays.copyOfRange(townInfo, 2, townInfo.length));

    String[] targetInfo = br.readLine().split(" ");
    targetHealth = Integer.parseInt(targetInfo[0]);
    targetName = String.join(" ", Arrays.copyOfRange(targetInfo, 1, targetInfo.length));

    petName = br.readLine();

    int numPlaces = Integer.parseInt(br.readLine());
    for (int i = 0; i < numPlaces; i++) {
      String[] placeInfo = br.readLine().split(" ");
      int row1 = Integer.parseInt(placeInfo[0]);
      int col1 = Integer.parseInt(placeInfo[1]);
      int row2 = Integer.parseInt(placeInfo[2]);
      int col2 = Integer.parseInt(placeInfo[3]);
      String placeName = String.join(" ", Arrays.copyOfRange(placeInfo, 4, placeInfo.length));
      int placeNumber = i + 1;
      Place place = new PlaceModel(row1, col1, row2, col2, placeName, String.valueOf(placeNumber));
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
      items.add(item);
    }

    br.close();

    return new TownData(townName, targetName, petName, targetHealth, places, items);
  }
}