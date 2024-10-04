package town;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import item.Item;
import item.ItemModel;
import place.Place;
import place.PlaceModel;
import character.Character;
import character.CharacterModel;

/**
 * The TownModel class represents a town with a name, a list of places, and a
 * character that can move between different places. It allows the character to
 * move to the next place, display the information of a place, and retrieve the
 * character in the town.
 */
public class TownModel implements Town {

	private final List<Place> places;
	private final List<String> items;
	private final Character targetCharacter;
	private String townName;
	private String targetName;
	private int targetHealth;

	/**
	 * Constructs a new TownModel with the specified filename. The town is loaded
	 * from the file, and the target character is created with the specified name
	 * and health status.
	 *
	 * @param filename the name of the file to load the town from
	 * @throws IOException if an I/O error occurs
	 */
	public TownModel(String filename) throws IOException {
		places = new ArrayList<>();
		items = new ArrayList<>();
		loadTown(filename);
		targetCharacter = new CharacterModel(targetName, targetHealth, places.get(0), places);
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
			Place place = new PlaceModel(row1, col1, row2, col2, placeName);
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
	public Character getCharacter() {
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
	public List<String> getItems() {
		for (Place place : places) {
			for (Item item : place.getItems()) {
				items.add(item.getName());
			}
		}
		return items;
	}

}
