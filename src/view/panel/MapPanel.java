package view.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;
import model.place.Place;

/**
 * A custom JPanel that provides a graphical representation of the game world map.
 * Displays places, highlights valid moves, and shows current positions of the target and player.
 * Supports interactive clicking for player movement.
 */
public class MapPanel extends JPanel {
  private static int CELL_SIZE = 0;
  private final List<Place> places;
  private final List<Place> highlightedPlaces = new ArrayList<>();
  private BufferedImage mapImage;
  private String targetPlaceName;
  private String playerPlaceName;
  private boolean showMoveHighlight = false;
  private Timer highlightTimer;
  private MapClickListener clickListener;

  /**
   * Constructs a new MapPanel with the specified game map layout.
   *
   * @param mapPlaces the list of places that make up the game world
   * @param cellSize  the size in pixels of each cell in the grid
   */
  public MapPanel(List<Place> mapPlaces, int cellSize) {
    this.places = mapPlaces;
    CELL_SIZE = cellSize;
    setPreferredSize(new Dimension(11 * CELL_SIZE, 12 * CELL_SIZE));

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (showMoveHighlight) {
          handleMapClick(e.getX(), e.getY());
        }
      }
    });
    createMapImage();
  }

  /**
   * Sets a listener to handle clicks on the map.
   * The listener will be notified when a player clicks on a place.
   *
   * @param listener the MapClickListener to handle click events
   */
  public void setClickListener(MapClickListener listener) {
    this.clickListener = listener;
  }

  /**
   * Displays visual highlights for valid movement options from the player's current position.
   * Highlights will automatically clear after 5 seconds.
   *
   * @param playerPlace the place where the player is currently located
   */
  public void showMoveOptions(Place playerPlace) {
    this.showMoveHighlight = true;
    this.highlightedPlaces.clear();

    // Find neighboring places
    for (Place place : places) {
      if (playerPlace.isNeighbor(place)) {
        highlightedPlaces.add(place);
      }
    }

    // Start timer to clear highlights after 5 seconds
    if (highlightTimer != null && highlightTimer.isRunning()) {
      highlightTimer.stop();
    }

    highlightTimer = new Timer(5000, e -> {
      showMoveHighlight = false;
      highlightedPlaces.clear();
      repaint();
      ((Timer) e.getSource()).stop();
    });
    highlightTimer.setRepeats(false);
    highlightTimer.start();

    repaint();
  }

  /**
   * Processes a mouse click on the map and notifies the click listener if appropriate.
   * Only processes clicks when move highlighting is active and a listener is set.
   *
   * @param x the x-coordinate of the click
   * @param y the y-coordinate of the click
   */
  private void handleMapClick(int x, int y) {
    if (!showMoveHighlight || clickListener == null) {
      return;
    }

    Place clickedPlace = getPlaceAtCoordinates(x, y);
    assert clickedPlace != null;
    System.out.println("Clicked place: " + clickedPlace.getName());
    boolean isValidMove = highlightedPlaces.contains(clickedPlace);
    clickListener.onPlaceClicked(clickedPlace, isValidMove);
  }

  /**
   * Determines which place on the map was clicked based on coordinates.
   *
   * @param x the x-coordinate of the click
   * @param y the y-coordinate of the click
   * @return the Place that was clicked, or null if no place was clicked
   */
  private Place getPlaceAtCoordinates(int x, int y) {
    for (Place place : places) {
      int placeX = place.getRow1() * 58;
      int placeY = place.getCol1() * 58;
      int placeWidth = (place.getRow2() - place.getRow1()) * 58;
      int placeHeight = (place.getCol2() - place.getCol1()) * 58;

      if (x >= placeX && x <= placeX + placeWidth
          && y >= placeY && y <= placeY + placeHeight) {
        return place;
      }
    }
    return null;
  }

  /**
   * Creates the base map image showing all places.
   * This image is created once and reused for efficiency.
   */
  private void createMapImage() {
    mapImage = new BufferedImage(11 * CELL_SIZE, 12 * CELL_SIZE,
        BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = mapImage.createGraphics();

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    g2d.setColor(new Color(245, 245, 245));
    g2d.fillRect(0, 0, getWidth(), getHeight());

    g2d.setColor(Color.BLACK);
    for (int i = 0; i < places.size(); i++) {
      drawPlace(g2d, places.get(i), i + 1);
    }

    g2d.dispose();
  }

  /**
   * Draws a single place on the map with its name and number.
   *
   * @param g2d         the Graphics2D context to draw with
   * @param place       the Place to draw
   * @param placeNumber the number identifier of the place
   */
  private void drawPlace(Graphics2D g2d, Place place, int placeNumber) {
    int x = place.getRow1() * CELL_SIZE;
    int y = place.getCol1() * CELL_SIZE;
    int width = (place.getRow2() - place.getRow1()) * CELL_SIZE;
    int height = (place.getCol2() - place.getCol1()) * CELL_SIZE;

    // Draw room background
    g2d.setColor(new Color(230, 230, 230));
    g2d.fillRect(x, y, width, height);

    // Draw room border
    g2d.setColor(Color.BLACK);
    g2d.drawRect(x, y, width, height);

    // Draw room name and number
    Font originalFont = g2d.getFont();
    Font smallerFont = originalFont.deriveFont(10f);
    g2d.setFont(smallerFont);

    FontMetrics fm = g2d.getFontMetrics();
    String name = place.getName();
    String number = "(" + placeNumber + ")";

    int lineHeight = fm.getHeight();
    int nameX = x + (width - fm.stringWidth(name)) / 2;
    int numberX = x + (width - fm.stringWidth(number)) / 2;

    // Calculate Y positions for name and number
    int nameY = y + ((height - (lineHeight * 2)) / 2) + fm.getAscent();
    int numberY = nameY + lineHeight;

    // Draw name and number
    g2d.drawString(name, nameX, nameY);
    g2d.drawString(number, numberX, numberY);

    g2d.setFont(originalFont);
  }

  /**
   * Overrides the paintComponent method to draw the map and all dynamic elements.
   * Includes the base map, movement highlights, and position markers.
   *
   * @param g the Graphics context to draw with
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (mapImage != null) {
      g.drawImage(mapImage, 0, 0, this);

      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);

      // Draw highlights for movable places
      if (showMoveHighlight) {
        Color highlightColor = new Color(0, 255, 0, 64); // Semi-transparent green
        g2d.setColor(highlightColor);

        for (Place place : highlightedPlaces) {
          int x = place.getRow1() * 58;
          int y = place.getCol1() * 58;
          int width = (place.getRow2() - place.getRow1()) * 58;
          int height = (place.getCol2() - place.getCol1()) * 58;
          g2d.fillRect(x, y, width, height);
        }
      }

      // Draw target and player markers
      if (targetPlaceName != null || playerPlaceName != null) {
        for (Place place : places) {
          int x = place.getRow1() * 58;
          int y = place.getCol1() * 58;
          int width = (place.getRow2() - place.getRow1()) * 58;
          int height = (place.getCol2() - place.getCol1()) * 58;

          if (place.getName().equals(targetPlaceName)) {
            drawTarget(g2d, x, y, width, height);
          }

          if (place.getName().equals(playerPlaceName)) {
            drawPlayer(g2d, x, y, width, height);
          }
        }
      }
    }
  }

  /**
   * Updates the displayed positions of the target and player on the map.
   *
   * @param targetPlace the name of the place where the target is located
   * @param playerPlace the name of the place where the player is located
   */
  public void updateLocations(String targetPlace, String playerPlace) {
    this.targetPlaceName = targetPlace;
    this.playerPlaceName = playerPlace;
    repaint(); // 触发重绘
  }

  /**
   * Draws the target marker on the map.
   * The target is represented by a semi-transparent red square.
   *
   * @param g2d    the Graphics2D context to draw with
   * @param x      the x-coordinate to draw at
   * @param y      the y-coordinate to draw at
   * @param width  the width of the containing place
   * @param height the height of the containing place
   */
  private void drawTarget(Graphics2D g2d, int x, int y, int width, int height) {
    int size = 14;
    int centerX = x + (width - size) / 3;
    int centerY = y + (height - size) / 3;
    Color transparentRed = new Color(255, 0, 0, 128);
    g2d.setColor(transparentRed);
    g2d.fillRect(centerX, centerY, size, size);
  }

  /**
   * Draws the player marker on the map.
   * The player is represented by a semi-transparent blue triangle.
   *
   * @param g2d    the Graphics2D context to draw with
   * @param x      the x-coordinate to draw at
   * @param y      the y-coordinate to draw at
   * @param width  the width of the containing place
   * @param height the height of the containing place
   */
  private void drawPlayer(Graphics2D g2d, int x, int y, int width, int height) {
    int size = 14;
    int centerX = x + (width - size) / 3 + size;
    int centerY = y + (height - size) / 3;

    int[] xpoints = {
        centerX,
        centerX + size,
        centerX + size / 2
    };
    int[] ypoints = {
        centerY + size,
        centerY + size,
        centerY
    };

    Color transparentBlue = new Color(0, 0, 255, 128);
    g2d.setColor(transparentBlue);

    g2d.fillPolygon(xpoints, ypoints, 3);
  }

  /**
   * Interface for handling click events on the map.
   * Implementers can receive notifications when places are clicked.
   */
  public interface MapClickListener {
    /**
     * Called when a place on the map is clicked.
     *
     * @param clickedPlace the Place that was clicked
     * @param isValidMove  whether the clicked place is a valid move destination
     */
    void onPlaceClicked(Place clickedPlace, boolean isValidMove);
  }
}