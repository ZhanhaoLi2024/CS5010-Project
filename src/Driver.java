import java.io.IOException;
import java.util.Scanner;
import place.Place;
import town.TownModel;

public class Driver {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		TownModel town = null;

		try {
			town = new TownModel("res/SmallTownWorld.txt");
		} catch (IOException e) {
			System.out.println("Error loading the town: " + e.getMessage());
			return;
		}

		System.out.println("Welcome to Kill Doctor Lucky!");
		displayMapInfo(town);

		while (true) {
			System.out.println("\nPlease choose an option:");
			System.out.println("1. Move 'The Mayor' to the next place");
			System.out.println("2. Show target's current space");
			System.out.println("3. Show neighbors of current space");
			System.out.println("4. Show space by index");
			System.out.println("5. Show neighbors by index");
			System.out.println("0. Exit");

			int choice = scanner.nextInt();

			switch (choice) {
			case 1:
				moveTargetCharacter(town);
				break;
			case 2:
				getCurrSpace(town);
				break;
			case 3:
				getCurrSpaceNeighbors(town);
				break;
			case 4:
				getSpaceByIndex(town, scanner);
				break;
			case 5:
				getNeighborsByIndex(town, scanner);
				break;
			case 0:
				System.out.println("Exiting...");
				scanner.close();
				return;
			default:
				System.out.println("Invalid choice, please try again.");
			}
		}
	}

	private static void displayMapInfo(TownModel town) {
		System.out.println("=== Map Information ===");
		System.out.println("Town: " + town.getTownName());
		System.out.println("Target name: " + town.getTargetName() + " (Health: " + town.getTargetHealth() + ")");
		int index = 1;
		for (Place place : town.getPlaces()) {
			System.out.println(index + " " + place.getName());
			place.getItems().forEach(item -> System.out
					.println("Items in the place: " + item.getName() + " (Damage: " + item.getDamage() + ")"));
			index++;
			System.out.println("--------------------");
		}
	}

	private static void moveTargetCharacter(TownModel town) {
		System.out.println("Moving the target character...");
		town.moveCharacter();
		getCurrSpace(town);
	}

	private static void getCurrSpace(TownModel town) {
		Place currentPlace = town.getCharacter().getCurrentPlace();
		System.out.println("Target character is in: " + currentPlace.getName());
		System.out.println("Items in the place:");
		currentPlace.getItems()
				.forEach(item -> System.out.println(item.getName() + " (Damage: " + item.getDamage() + ")"));
	}

	private static void getCurrSpaceNeighbors(TownModel town) {
		Place currentPlace = town.getCharacter().getCurrentPlace();
		System.out.println("Neighbors of " + currentPlace.getName() + ":");
		currentPlace.getNeighbors().forEach(neighbor -> System.out.println(neighbor.getName()));
	}

	private static void getSpaceByIndex(TownModel town, Scanner scanner) {
		System.out.println("Enter the index of the space (1 to " + (town.getPlaces().size()) + "):");
		int index = scanner.nextInt() - 1;
		if (index < 0 || index >= town.getPlaces().size()) {
			System.out.println("Invalid number.");
			return;
		}
		Place place = town.getPlaces().get(index);
		System.out.println("Place: " + place.getName());
		System.out.println("Items in the place:");
		place.getItems().forEach(item -> System.out.println(item.getName() + " (Damage: " + item.getDamage() + ")"));
	}

	private static void getNeighborsByIndex(TownModel town, Scanner scanner) {
		System.out.println("Enter the index of the space (1 to " + (town.getPlaces().size()) + "):");
		int index = scanner.nextInt() - 1;
		if (index < 0 || index >= town.getPlaces().size()) {
			System.out.println("Invalid number.");
			return;
		}
		Place place = town.getPlaces().get(index);
		System.out.println("Neighbors of " + place.getName() + ":");
		place.getNeighbors().forEach(neighbor -> System.out.println(neighbor.getName()));
	}
}