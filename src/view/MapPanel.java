package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import model.place.Place;
import model.player.Player;
import model.target.Target;

/**
 * Custom panel for rendering the game map, players, and target.
 */
public class MapPanel extends JPanel {
  private static final int CELL_SIZE = 40;
  private static final int PLAYER_SIZE = 20;
  private static final int TARGET_SIZE = 25;
  private static final Color PLACE_COLOR = new Color(200, 200, 255);
  private static final Color PLAYER_COLOR = new Color(255, 100, 100);
  private static final Color TARGET_COLOR = new Color(255, 0, 0);
  private final Map<String, Point> playerPositions;
  private List<Place> places;
  private List<Player> players;
  private Target target;
  private Point targetPosition;

  /**
   * Constructs a new MapPanel for displaying the game map.
   */
  public MapPanel() {
    setBackground(Color.WHITE);
    playerPositions = new HashMap<>();
    setPreferredSize(new Dimension(800, 600));
  }

  /**
   * Updates the map data and triggers a repaint.
   *
   * @param places  List of places in the world
   * @param players List of players
   * @param target  The target character
   */
  public void updateMap(List<Place> places, List<Player> players, Target target) {
    this.places = places;
    this.players = players;
    this.target = target;
    calculatePositions();
    repaint();
  }

  private void calculatePositions() {
    // Calculate positions for players and target
    if (players != null) {
      for (Player player : players) {
        int placeNum = player.getPlayerCurrentPlaceNumber();
        Place place = places.get(placeNum - 1);
        int centerX = (place.getCol1() + place.getCol2()) * CELL_SIZE / 2;
        int centerY = (place.getRow1() + place.getRow2()) * CELL_SIZE / 2;
        playerPositions.put(player.getName(), new Point(centerX, centerY));
      }
    }

    if (target != null) {
      Place targetPlace = target.getCurrentPlace();
      int centerX = (targetPlace.getCol1() + targetPlace.getCol2()) * CELL_SIZE / 2;
      int centerY = (targetPlace.getRow1() + targetPlace.getRow2()) * CELL_SIZE / 2;
      targetPosition = new Point(centerX, centerY);
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;

    // Enable anti-aliasing
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if (places != null && !places.isEmpty()) {
      // 先计算地图的实际大小
      int maxCol = 0;
      int maxRow = 0;
      for (Place place : places) {
        maxCol = Math.max(maxCol, place.getCol2());
        maxRow = Math.max(maxRow, place.getRow2());
      }

      // 设置面板的首选大小
      setPreferredSize(new Dimension((maxCol + 1) * CELL_SIZE, (maxRow + 1) * CELL_SIZE));

      // 绘制所有地方
      for (Place place : places) {
        drawPlace(g2d, place);
      }

      // 绘制玩家
      if (players != null) {
        for (Player player : players) {
          Point pos = playerPositions.get(player.getName());
          if (pos != null) {
            drawPlayer(g2d, pos.x, pos.y, player.getName());
          }
        }
      }

      // 绘制目标
      if (target != null && targetPosition != null) {
        drawTarget(g2d, targetPosition.x, targetPosition.y);
      }

      // 通知布局管理器重新计算滚动条
      revalidate();
    }
  }

  private void drawPlace(Graphics2D g2d, Place place) {
    int x1 = place.getCol1() * CELL_SIZE;
    int y1 = place.getRow1() * CELL_SIZE;
    int width = (place.getCol2() - place.getCol1() + 1) * CELL_SIZE;
    int height = (place.getRow2() - place.getRow1() + 1) * CELL_SIZE;

    // Draw place rectangle
    g2d.setColor(PLACE_COLOR);
    g2d.fillRect(x1, y1, width, height);
    g2d.setColor(Color.BLACK);
    g2d.setStroke(new BasicStroke(2));
    g2d.drawRect(x1, y1, width, height);

    // Draw place name
    g2d.setFont(new Font("Arial", Font.BOLD, 12));
    g2d.drawString(place.getName(), x1 + 5, y1 + 20);

    // Draw place number
    g2d.setFont(new Font("Arial", Font.PLAIN, 10));
    g2d.drawString("(" + place.getPlaceNumber() + ")", x1 + 5, y1 + 35);
  }

  private void drawPlayer(Graphics2D g2d, int x, int y, String name) {
    g2d.setColor(PLAYER_COLOR);
    g2d.fillOval(x - PLAYER_SIZE / 2, y - PLAYER_SIZE / 2, PLAYER_SIZE, PLAYER_SIZE);
    g2d.setColor(Color.BLACK);
    g2d.drawOval(x - PLAYER_SIZE / 2, y - PLAYER_SIZE / 2, PLAYER_SIZE, PLAYER_SIZE);

    // Draw player name
    g2d.setFont(new Font("Arial", Font.PLAIN, 10));
    g2d.drawString(name, x - PLAYER_SIZE / 2, y + PLAYER_SIZE);
  }

  private void drawTarget(Graphics2D g2d, int x, int y) {
    g2d.setColor(TARGET_COLOR);
    g2d.fillRect(x - TARGET_SIZE / 2, y - TARGET_SIZE / 2, TARGET_SIZE, TARGET_SIZE);
    g2d.setColor(Color.BLACK);
    g2d.drawRect(x - TARGET_SIZE / 2, y - TARGET_SIZE / 2, TARGET_SIZE, TARGET_SIZE);

    // Draw target name
    g2d.setFont(new Font("Arial", Font.BOLD, 12));
    g2d.drawString(target.getName(), x - TARGET_SIZE / 2, y + TARGET_SIZE);
  }

  /**
   * Gets the preferred size of the panel based on the map dimensions.
   *
   * @return The preferred dimension for the panel
   */
  @Override
  public Dimension getPreferredSize() {
    if (places == null || places.isEmpty()) {
      return super.getPreferredSize();
    }

    int maxCol = 0;
    int maxRow = 0;
    for (Place place : places) {
      maxCol = Math.max(maxCol, place.getCol2());
      maxRow = Math.max(maxRow, place.getRow2());
    }

    return new Dimension((maxCol + 1) * CELL_SIZE + 50, (maxRow + 1) * CELL_SIZE + 50);
  }
}