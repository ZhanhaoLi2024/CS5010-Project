package view.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;
import model.place.Place;

public class MapPanel extends JPanel {
  private static int CELL_SIZE = 0;
  private final List<Place> places;
  private BufferedImage mapImage;
  private String targetPlaceName;
  private String playerPlaceName;

  public MapPanel(List<Place> places, int cellSize) {
    this.places = places;
    CELL_SIZE = cellSize;
    setPreferredSize(new Dimension(11 * CELL_SIZE, 12 * CELL_SIZE));
    createMapImage();
  }

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

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (mapImage != null) {
      g.drawImage(mapImage, 0, 0, this);

      if (targetPlaceName != null || playerPlaceName != null) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

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

  public void updateLocations(String targetPlace, String playerPlace) {
    this.targetPlaceName = targetPlace;
    this.playerPlaceName = playerPlace;
    repaint(); // 触发重绘
  }

  private void drawTarget(Graphics2D g2d, int x, int y, int width, int height) {
    int size = 14;
    int centerX = x + (width - size) / 3;
    int centerY = y + (height - size) / 3;
    Color transparentRed = new Color(255, 0, 0, 128);
    g2d.setColor(transparentRed);
    g2d.fillRect(centerX, centerY, size, size);
  }

  private void drawPlayer(Graphics2D g2d, int x, int y, int width, int height) {
    int size = 14;
    int centerX = x + (width - size) / 3 + size;
    int centerY = y + (height - size) / 3;

    int[] xPoints = {
        centerX,
        centerX + size,
        centerX + size / 2
    };
    int[] yPoints = {
        centerY + size,
        centerY + size,
        centerY
    };

    Color transparentBlue = new Color(0, 0, 255, 128);
    g2d.setColor(transparentBlue);

    g2d.fillPolygon(xPoints, yPoints, 3);
  }
}