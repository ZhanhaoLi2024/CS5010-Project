package town;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import item.Item;
import item.ItemModel;
import place.Place;
import place.PlaceModel;
import character.Character;
import character.CharacterModel;

public class TownModel implements Town {

    private List<Place> places;
    private List<Item> items;
    private Character targetCharacter;

    public TownModel(String filename) throws IOException {
        places = new ArrayList<>();
        items = new ArrayList<>();
        loadTown(filename);
        targetCharacter = new CharacterModel("The Mayor", 100, places.get(0), places);
    }

    @Override
    public void loadTown(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));

        String[] townInfo = br.readLine().split(" ");
        int numRows = Integer.parseInt(townInfo[0]);
        int numCols = Integer.parseInt(townInfo[1]);
        String townName = townInfo[2];
        System.out.println("Town: " + townName + " (" + numRows + "x" + numCols + ")");

        String[] targetInfo = br.readLine().split(" ");
        int health = Integer.parseInt(targetInfo[0]);
        String targetName = targetInfo[1];
        System.out.println("Target Character: " + targetName + " (Health: " + health + ")");

        int numPlaces = Integer.parseInt(br.readLine());
        for (int i = 0; i < numPlaces; i++) {
            String[] placeInfo = br.readLine().split(" ");
            int row1 = Integer.parseInt(placeInfo[0]);
            int col1 = Integer.parseInt(placeInfo[1]);
            int row2 = Integer.parseInt(placeInfo[2]);
            int col2 = Integer.parseInt(placeInfo[3]);
            String placeName = placeInfo[4];
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
            String itemName = itemInfo[2];
            Item item = new ItemModel(itemName, damage);
            places.get(placeIndex).addItem(item);
        }

        br.close();
    }

    @Override
    public void displayPlaceInfo(Place place) {
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
    public Character getCharacter() {
        return targetCharacter;
    }

}
