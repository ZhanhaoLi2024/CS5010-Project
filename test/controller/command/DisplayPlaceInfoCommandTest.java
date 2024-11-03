package controller.command;

/**
 * Test class for the DisplayPlaceInfoCommand.
 */
public class DisplayPlaceInfoCommandTest {
//
//  private Town town;
//  private StringBuilder output;
//  private Scanner scanner;
//  private List<Place> places;
//  private List<Player> players;
//
//  /**
//   * Sets up the test fixture.
//   */
//  @Before
//  public void setUp() {
//    output = new StringBuilder();
//
//    Item item1 = new ItemModel("Sword", 10);
//    Item item2 = new ItemModel("Shield", 5);
//
//    Place place1 = new PlaceModel(0, 0, 1, 1, "Place1", String.valueOf(1));
//    Place place2 = new PlaceModel(2, 2, 3, 3, "Place2", String.valueOf(2));
//    place1.addItem(item1);
//    place2.addItem(item2);
//
//    place1.addNeighbor(place2);
//    place2.addNeighbor(place1);
//
//    places = new ArrayList<>();
//    places.add(place1);
//    places.add(place2);
//
//    town = new Town() {
//      @Override
//      public List<Place> getPlaces() {
//        return places;
//      }
//
//      @Override
//      public List<Player> getPlayers() {
//        return new ArrayList<>();
//      }
//
//      @Override
//      public List<Place> getCurrentPlaceNeighbors(Place place) {
//        return List.of();
//      }
//
//      @Override
//      public void getPlaceInfo(Place place) {
//      }
//
//      @Override
//      public void moveCharacter() {
//      }
//
//      @Override
//      public Target getCharacter() {
//        return null;
//      }
//
//      @Override
//      public String getName() {
//        return "TestTown";
//      }
//
//      @Override
//      public String getTargetName() {
//        return "Target";
//      }
//
//      @Override
//      public int getTargetHealth() {
//        return 100;
//      }
//
//      @Override
//      public String getTownName() {
//        return "TestTown";
//      }
//
//      @Override
//      public List<Item> getItems() {
//        return new ArrayList<>();
//      }
//
//      @Override
//      public void addComputerPlayer() throws IOException {
//
//      }
//
//      @Override
//      public void addPlayer() throws IOException {
//      }
//
//      @Override
//      public void showAllPlayersInfo() {
//      }
//
//      @Override
//      public void showPlayerCurrentInfo() {
//      }
//
//      @Override
//      public void getPlayerByName(String playerName) {
//      }
//
//      @Override
//      public void lookAround() {
//      }
//
//      @Override
//      public void switchToNextPlayer() {
//      }
//
//      @Override
//      public Boolean isGameOver() {
//        return false;
//      }
//    };
//  }
//
//  /**
//   * Tears down the test fixture.
//   */
//  @Test
//  public void testShowAllPlacesInfo() throws IOException {
//    String input = "1\n0\n";
//    scanner = new Scanner(input);
//
//    DisplayPlaceInfoCommand command = new DisplayPlaceInfoCommand(town, output, scanner);
//    command.execute();
//
//    assertTrue(output.toString().contains("All places info:"));
//    assertTrue(output.toString().contains("Place1"));
//    assertTrue(output.toString().contains("Place2"));
//    assertTrue(output.toString().contains("Sword"));
//    assertTrue(output.toString().contains("Shield"));
//  }
//
//  /**
//   * Tests the DisplayPlaceInfoCommand.
//   *
//   * @throws IOException if there is an issue with I/O operations.
//   */
//  @Test
//  public void testShowSpecificPlaceInfo_Found() throws IOException {
//    String input = "2\nPlace1\n0\n";
//    scanner = new Scanner(input);
//
//    DisplayPlaceInfoCommand command = new DisplayPlaceInfoCommand(town, output, scanner);
//    command.execute();
//
//    assertTrue(output.toString().contains("Place name: Place1"));
//    assertTrue(output.toString().contains("Sword"));
//    assertTrue(output.toString().contains("Neighbor name: Place2"));
//  }
//
//  /**
//   * Tests the DisplayPlaceInfoCommand.
//   *
//   * @throws IOException if there is an issue with I/O operations.
//   */
//  @Test
//  public void testShowSpecificPlaceInfo_NotFound() throws IOException {
//    String input = "2\nUnknownPlace\n0\n";
//    scanner = new Scanner(input);
//
//    DisplayPlaceInfoCommand command = new DisplayPlaceInfoCommand(town, output, scanner);
//    command.execute();
//
//    assertTrue(output.toString().contains("Place not found."));
//  }
//
//  /**
//   * Tests the DisplayPlaceInfoCommand.
//   *
//   * @throws IOException if there is an issue with I/O operations.
//   */
//  @Test
//  public void testNoPlacesFound() throws IOException {
//    places.clear();
//
//    String input = "1\n0\n";
//    scanner = new Scanner(input);
//
//    DisplayPlaceInfoCommand command = new DisplayPlaceInfoCommand(town, output, scanner);
//    command.execute();
//
//    assertTrue(output.toString().contains("No places found."));
//  }
}