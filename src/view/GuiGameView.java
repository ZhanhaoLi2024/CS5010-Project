package view;

import controller.Controller;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import model.place.Place;
import model.player.Player;
import view.dialog.AddPlayerDialog;
import view.dialog.PlayerInfoDialog;
import view.panel.MapPanel;

/**
 * GUI implementation of the GameView interface.
 */
public class GuiGameView implements GameView {
  private static final String WELCOME_CARD = "WELCOME";
  private static final String MENU_CARD = "MENU";
  private static final String GAME_CARD = "GAME";

  private final Controller controller;
  private final JFrame mainFrame;
  private final JPanel mainPanel;
  private final CardLayout cardLayout;
  private final JTextArea messageArea;

  /**
   * Constructs the GUI view.
   *
   * @param gameController the game controller
   */
  public GuiGameView(Controller gameController) {
    this.controller = gameController;
    this.mainFrame = new JFrame("Kill Doctor Lucky");
    this.cardLayout = new CardLayout();
    this.mainPanel = new JPanel(cardLayout);
    this.messageArea = new JTextArea(10, 40);
    messageArea.setEditable(false);

    setupMainFrame();
    createPanels();
  }

  private void setupMainFrame() {
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setPreferredSize(new Dimension(1024, 768));
    mainFrame.add(mainPanel);
    mainFrame.setLocationRelativeTo(null);
  }

  private void createPanels() {
    // Welcome Panel
    mainPanel.add(createWelcomePanel(), WELCOME_CARD);

    // Menu Panel
    mainPanel.add(createMenuPanel(), MENU_CARD);

    // Game Panel
    mainPanel.add(createGamePanel(), GAME_CARD);
  }

  private JPanel createWelcomePanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(new Color(245, 245, 245));
    panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

    // Title
    JLabel titleLabel = createStyledLabel("Kill Doctor Lucky", 48, Font.BOLD);

    // Author
    JLabel authorLabel = createStyledLabel("Created by Li,Zhanhao", 24, Font.PLAIN);

    // Course Info
    JLabel courseLabel = createStyledLabel("CS5010 Project", 20, Font.PLAIN);

    // Start Button
    JButton startButton = new JButton("Start Game");
    startButton.setFont(new Font("Arial", Font.BOLD, 24));
    startButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
    startButton.setMaximumSize(new Dimension(200, 50));
    startButton.addActionListener(e -> cardLayout.show(mainPanel, MENU_CARD));

    // Add components with spacing
    panel.add(Box.createVerticalGlue());
    panel.add(titleLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 30)));
    panel.add(authorLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
    panel.add(courseLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 50)));
    panel.add(startButton);
    panel.add(Box.createVerticalGlue());

    return panel;
  }

  private JPanel createMenuPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(new Color(245, 245, 245));
    panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

    String[] menuOptions = {
        "Add Human Player",
        "Add Computer Player",
        "Display Player Information",
        "Start Game",
        "Exit Game"
    };
    String[] commands = {
        "ADD_PLAYER_SCREEN",
        "ADD_COMPUTER_PLAYER",
        "SHOW_PLAYER_INFO",
        "START_TURNS",
        "QUIT"
    };

    // Add menu title
    JLabel menuTitle = createStyledLabel("Main Menu", 36, Font.BOLD);
    panel.add(menuTitle);
    panel.add(Box.createRigidArea(new Dimension(0, 30)));

    // Add menu buttons
    for (int i = 0; i < menuOptions.length; i++) {
      JButton button = createMenuButton(menuOptions[i], commands[i]);
      panel.add(button);
      panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    return panel;
  }

  private JPanel createGamePanel() {
    JPanel panel = new JPanel(new BorderLayout());

    // Create map panel with scrolling
    MapPanel mapPanel = new MapPanel(controller.getTown().getPlaces());
    JScrollPane mapScroll = new JScrollPane(mapPanel);

    // Create info panel
    JPanel infoPanel = createInfoPanel();

    // Create control panel
    JPanel controlPanel = createControlPanel();

    // Add components
    JSplitPane splitPane = new JSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        mapScroll,
        infoPanel
    );
    splitPane.setResizeWeight(0.7);

    panel.add(splitPane, BorderLayout.CENTER);
    panel.add(controlPanel, BorderLayout.SOUTH);

    return panel;
  }

  private JPanel createInfoPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Game Information"));

    // Add message area
    JScrollPane scrollPane = new JScrollPane(messageArea);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
  }

  private JPanel createControlPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    String[] buttonLabels = {"Move (M)", "Look (L)", "Pick (P)", "Attack (A)", "Pet Move (T)"};
    String[] commands = {"MOVE", "LOOK", "PICK", "ATTACK", "PETMOVE"};

    for (int i = 0; i < buttonLabels.length; i++) {
      panel.add(createActionButton(buttonLabels[i], commands[i]));
    }

    return panel;
  }

  private JLabel createStyledLabel(String text, int size, int style) {
    JLabel label = new JLabel(text);
    label.setFont(new Font("Arial", style, size));
    label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    return label;
  }

  private JButton createMenuButton(String text, String command) {
    JButton button = new JButton(text);
    button.setFont(new Font("Arial", Font.PLAIN, 18));
    button.setAlignmentX(JButton.CENTER_ALIGNMENT);
    button.setMaximumSize(new Dimension(300, 40));
    button.setActionCommand(command);
    button.addActionListener(e -> handleCommand(command));
    return button;
  }

  private JButton createActionButton(String text, String command) {
    JButton button = new JButton(text);
    button.setActionCommand(command);
    button.addActionListener(e -> handleCommand(command));
    return button;
  }

  private void handleCommand(String command) {
    try {
      if (command.equals("ADD_PLAYER_SCREEN")) {
        AddPlayerDialog dialog = new AddPlayerDialog(this);
        dialog.setVisible(true);
      } else if (command.equals("ADD_COMPUTER_PLAYER")) {
        controller.executeCommand("ADD_COMPUTER");
      } else if (command.equals("SHOW_PLAYER_INFO")) {
        PlayerInfoDialog dialog = new PlayerInfoDialog(this);
        dialog.setVisible(true);
      } else if (command.equals("START_TURNS")) {
        controller.executeCommand("START_TURNS");
      } else if (command.equals("QUIT")) {
        close();
      } else {
        System.out.println("Executing command: " + command);
      }
    } catch (Exception ex) {
      showError("Error executing command: " + ex.getMessage());
    }
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(mainFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  // Interface implementations
  @Override
  public void initialize() throws IOException {
    SwingUtilities.invokeLater(() -> {
      mainFrame.pack();
      mainFrame.setVisible(true);
      cardLayout.show(mainPanel, WELCOME_CARD);
    });
  }

  @Override
  public void showWelcomeScreen() throws IOException {
    SwingUtilities.invokeLater(() -> cardLayout.show(mainPanel, WELCOME_CARD));
  }

  @Override
  public void updatePlayerInfo(Player player, Place currentPlace) throws IOException {
    SwingUtilities.invokeLater(() -> {
      String info = String.format("Player: %s\nLocation: %s\nItems: %d/%d\n",
          player.getName(),
          currentPlace.getName(),
          player.getCurrentCarriedItems().size(),
          player.getCarryLimit());
      messageArea.append(info);
    });
  }

  @Override
  public void showMessage(String message) throws IOException {
    SwingUtilities.invokeLater(() -> {
      messageArea.append(message + "\n");
      messageArea.setCaretPosition(messageArea.getDocument().getLength());
    });
  }

  @Override
  public String getStringInput() throws IOException {
    return JOptionPane.showInputDialog(mainFrame, "Enter value:");
  }

  @Override
  public int getNumberInput() throws IOException {
    try {
      String input = JOptionPane.showInputDialog(mainFrame, "Enter number:");
      return Integer.parseInt(input);
    } catch (NumberFormatException e) {
      showError("Invalid number format. Please try again.");
      return getNumberInput();
    }
  }

  @Override
  public void close() throws IOException {
    SwingUtilities.invokeLater(() -> mainFrame.dispose());
  }

  public JFrame getMainFrame() {
    return mainFrame;
  }

  public Controller getController() {
    return controller;
  }
}