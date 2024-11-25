package view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import model.place.Place;
import model.player.Player;

/**
 * Panel that displays game information and player status.
 */
public class InfoPanel extends JPanel {
  private final JLabel playerNameLabel;
  private final JLabel locationLabel;
  private final JLabel itemCountLabel;
  private final JTextArea statusArea;

  /**
   * Constructs a new InfoPanel.
   */
  public InfoPanel() {
    setLayout(new BorderLayout(5, 5));
    setPreferredSize(new Dimension(250, 0));
    setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

    // Create player info panel
    JPanel playerInfoPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    gbc.insets = new java.awt.Insets(2, 2, 2, 2);

    // Initialize labels
    playerNameLabel = createInfoLabel("Player: ");
    locationLabel = createInfoLabel("Location: ");
    itemCountLabel = createInfoLabel("Items: ");

    // Add labels to player info panel
    gbc.gridy = 0;
    playerInfoPanel.add(playerNameLabel, gbc);

    gbc.gridy = 1;
    playerInfoPanel.add(locationLabel, gbc);

    gbc.gridy = 2;
    playerInfoPanel.add(itemCountLabel, gbc);

    // Create status area
    statusArea = new JTextArea();
    statusArea.setEditable(false);
    statusArea.setLineWrap(true);
    statusArea.setWrapStyleWord(true);
    statusArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    JScrollPane scrollPane = new JScrollPane(statusArea);
    scrollPane.setPreferredSize(new Dimension(230, 200));

    // Add components to main panel
    add(createTitleLabel("Game Information"), BorderLayout.NORTH);
    add(playerInfoPanel, BorderLayout.CENTER);
    add(scrollPane, BorderLayout.SOUTH);
  }

  /**
   * Updates the displayed player information.
   *
   * @param player       the current player
   * @param currentPlace the player's current location
   */
  public void updatePlayerInfo(Player player, Place currentPlace) {
    playerNameLabel.setText("Player: " + player.getName());
    locationLabel.setText("Location: " + currentPlace.getName());
    itemCountLabel.setText(String.format("Items: %d/%d",
        player.getCurrentCarriedItems().size(),
        player.getCarryLimit()));
  }

  /**
   * Adds a status message to the status area.
   *
   * @param message the status message to add
   */
  public void addStatusMessage(String message) {
    statusArea.append(message + "\n");
    statusArea.setCaretPosition(statusArea.getDocument().getLength());
  }

  /**
   * Clears all status messages.
   */
  public void clearStatus() {
    statusArea.setText("");
  }

  private JLabel createTitleLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(new Font("SansSerif", Font.BOLD, 16));
    label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    label.setHorizontalAlignment(JLabel.CENTER);
    return label;
  }

  private JLabel createInfoLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(new Font("SansSerif", Font.PLAIN, 14));
    return label;
  }
}