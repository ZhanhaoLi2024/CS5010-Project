import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import place.Place;
import town.TownModel;

/**
 * The Driver class contains the main method that runs the game. It allows the
 * user to interact with the game by moving the target character, displaying
 * information about the target character's current space, and showing the
 * neighbors of the target character's space.
 */
public class Driver {
  private static final int CELL_SIZE = 90;

  /**
   * The main method that runs the game. It allows the user to interact with the
   * game by moving the target character, displaying information about the target
   * character's current space, and showing the neighbors of the target character's
   * space.
   *
   * @param args the command-line arguments
   */
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
      System.out.println("3. Show neighbors of target's space");
      System.out.println("4. Show space by index");
      System.out.println("5. Show neighbors by index");
      System.out.println("6. Print the map");
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
        case 6:
          printMap(town);
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

  /**
   * Displays the information about the town, target character, places, and items
   * in the town.
   *
   * @param town the town to display the information of
   */
  private static void displayMapInfo(TownModel town) {
    System.out.println("=== Map Information ===");
    System.out.println("Town: " + town.getTownName());
    System.out.println(
        "Target name: " + town.getTargetName() + " (Health: " + town.getTargetHealth() + ")");
    System.out.println("Places in the town: ");
    System.out.println("--------------------");
    int index = 1;
    for (Place place : town.getPlaces()) {
      System.out.println(index + " " + place.getName());
      place.getItems().forEach(item -> System.out.println(
          "Items in the place: " + item.getName() + " (Damage: " + item.getDamage() + ")"));
      index++;
      System.out.println("--------------------");
    }
  }

  /**
   * Moves the target character to the next place.
   *
   * @param town the town where the target character is located
   */
  private static void moveTargetCharacter(TownModel town) {
    System.out.println("Moving the target character...");
    town.moveCharacter();
    getCurrSpace(town);
  }

  /**
   * Displays the information about the target character's current space.
   *
   * @param town the town where the target character is located
   */
  private static void getCurrSpace(TownModel town) {
    Place currentPlace = town.getCharacter().getCurrentPlace();
    System.out.println("Target character is in: " + currentPlace.getName());
    System.out.println("Items in the place:");
    currentPlace.getItems().forEach(
        item -> System.out.println(item.getName() + " (Damage: " + item.getDamage() + ")"));
  }

  /**
   * Displays the neighbors of the target character's space.
   *
   * @param town the town where the target character is located
   */
  private static void getCurrSpaceNeighbors(TownModel town) {
    Place currentPlace = town.getCharacter().getCurrentPlace();
    System.out.println("Neighbors of " + currentPlace.getName() + ":");
    currentPlace.getNeighbors().forEach(neighbor -> System.out.println(neighbor.getName()));
  }

  /**
   * Displays the information about the space in the town based on the index
   * provided by the user.
   *
   * @param town    the town where the target character is located
   * @param scanner the scanner object to read user input
   */
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
    place.getItems().forEach(
        item -> System.out.println(item.getName() + " (Damage: " + item.getDamage() + ")"));
  }

  /**
   * Displays the neighbors of the space in the town based on the index provided by
   * the user.
   *
   * @param town    the town where the target character is located
   * @param scanner the scanner object to read user input
   */
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

  /**
   * Prints the map of the town to a PNG file.
   *
   * @param town the town to print the map of
   */
  private static void printMap(TownModel town) {
    System.out.println("Printing the map...");

    int width = 11 * CELL_SIZE;
    int height = 12 * CELL_SIZE;
    BufferedImage mapImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    Graphics2D g2d = mapImage.createGraphics();

    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, width, height);

    g2d.setColor(Color.BLACK);

    for (Place place : town.getPlaces()) {
      int row1 = place.getRow1();
      int col1 = place.getCol1();
      int row2 = place.getRow2();
      int col2 = place.getCol2();

      drawPlace(g2d, place.getName(), row1, col1, row2, col2);

    }

    try {
      ImageIO.write(mapImage, "png", new File("res/map.png"));
      System.out.println("Map saved as map.png");
    } catch (IOException e) {
      e.printStackTrace();
    }

    g2d.dispose();
  }

  /**
   * Draws a place on the map image.
   *
   * @param g2d  the Graphics2D object to draw the place
   * @param name the name of the place
   * @param row1 the starting row of the place
   * @param col1 the starting column of the place
   * @param row2 the ending row of the place
   * @param col2 the ending column of the place
   */
  private static void drawPlace(Graphics2D g2d, String name, int row1, int col1, int row2,
      int col2) {
    int y = col1 * CELL_SIZE;
    int x = row1 * CELL_SIZE;
    int width = (row2 - row1) * CELL_SIZE;
    int height = (col2 - col1) * CELL_SIZE;
    g2d.drawRect(x, y, width, height);

    g2d.drawString(name, x + width / 4, y + height / 4);
  }
}