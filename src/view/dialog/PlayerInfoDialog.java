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
 * Dialog for displaying player information.
 */
public class PlayerInfoDialog extends JDialog {
  private final GuiGameView parentView;
  private final JTextArea infoArea;

  /**
   * Constructs a new PlayerInfoDialog.
   *
   * @param parent the parent GuiGameView
   */
  public PlayerInfoDialog(GuiGameView parent) {
    super(parent.getMainFrame(), "Player Information", true);
    this.parentView = parent;
    this.infoArea = new JTextArea(15, 40);
    infoArea.setEditable(false);
    initializeDialog();
  }

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

  private JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

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

  private void handleShowAllPlayers() {
    try {
      List<String> playersInfo = parentView.getController().getTown().getAllPlayersInfo();
      displayPlayersInfo(playersInfo);
    } catch (Exception e) {
      showError("Error getting players info: " + e.getMessage());
    }
  }

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

  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }
}