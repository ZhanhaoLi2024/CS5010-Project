package view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import view.GuiGameView;

/**
 * A dialog window for displaying player information in the game.
 * Provides functionality to view all players' information or details about a specific player.
 */
public class PlayerInfoDialog extends JDialog {
  private final GuiGameView parentView;
  private final JTextArea infoArea;

  /**
   * Constructs a new PlayerInfoDialog.
   * Initializes a modal dialog window with a text area for displaying player information.
   *
   * @param parent the parent GuiGameView that owns this dialog
   */
  public PlayerInfoDialog(GuiGameView parent) {
    super(parent.getMainFrame(), "Player Information", true);
    this.parentView = parent;
    this.infoArea = new JTextArea(15, 40);
    infoArea.setEditable(false);
    initializeDialog();
  }

  /**
   * Initializes the dialog components and layout.
   * Sets up the main content area with a scrollable text display and button panel.
   */
  private void initializeDialog() {
    setLayout(new BorderLayout(10, 10));
    setSize(600, 400);
    setLocationRelativeTo(getParent());

    // Create main panel
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Add info area with scroll
    JScrollPane scrollPane = new JScrollPane(infoArea);
    mainPanel.add(scrollPane);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

    // Create button panel
    JPanel buttonPanel = createButtonPanel();

    // Add panels to dialog
    add(mainPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Creates the button panel with controls for viewing player information.
   * Contains buttons for showing all players, showing specific player, and exiting.
   *
   * @return a JPanel containing the control buttons
   */
  private JPanel createButtonPanel() {
    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

    JButton showAllButton = new JButton("Show All Players");
    JButton showSomeoneButton = new JButton("Show Someone");
    JButton exitButton = new JButton("Exit");

    // Set button actions
    showAllButton.addActionListener(e -> handleShowAllPlayers());
    showSomeoneButton.addActionListener(e -> handleShowSomeone());
    exitButton.addActionListener(e -> dispose());

    // Add buttons to panel
    buttonPanel.add(showAllButton);
    buttonPanel.add(showSomeoneButton);
    buttonPanel.add(exitButton);

    return buttonPanel;
  }

  /**
   * Handles the action to display information about all players in the game.
   * Retrieves player information from the model and updates the display.
   */
  private void handleShowAllPlayers() {
    List<String> playersInfo = parentView.getController().getTown().getAllPlayersInfo();
    displayPlayersInfo(playersInfo);

  }

  /**
   * Handles the action to display information about a specific player.
   * Prompts for player name and displays their information if found.
   */
  private void handleShowSomeone() {
    String playerName = JOptionPane.showInputDialog(this, "Enter player name:");
    if (playerName != null && !playerName.trim().isEmpty()) {
      try {
        String playerInfo = parentView.getController().getTown().getPlayerByName(playerName);
        if (!playerInfo.isEmpty()) {
          String[] info = playerInfo.split(",");
          infoArea.setText(String.format("Player: %s\nLocation: %s\nCarry Limit: %s",
              info[0], info[1], info[2]));
        } else {
          showError("Player not found!");
        }
      } catch (IOException e) {
        showError("Error getting player info: " + e.getMessage());
      }
    }
  }

  /**
   * Formats and displays player information in the text area.
   * Handles both single and multiple player information display.
   *
   * @param playersInfo list of strings containing player information to display
   */
  private void displayPlayersInfo(List<String> playersInfo) {
    if (playersInfo.isEmpty()) {
      infoArea.setText("No players in the game.");
      return;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("All Players Information:\n\n");
    for (String playerInfo : playersInfo) {
      String[] info = playerInfo.split(",");
      if (info.length >= 3) {
        sb.append(String.format("Player: %s\nLocation: %s\nCarry Limit: %s\n\n",
            info[0], info[1], info[2]));
      }
    }
    infoArea.setText(sb.toString());
  }

  /**
   * Displays an error message dialog with the specified message.
   *
   * @param message the error message to display
   */
  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }
}