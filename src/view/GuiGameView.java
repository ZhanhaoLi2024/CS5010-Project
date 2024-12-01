package view;

import controller.Controller;
import controller.support.PlayerInfoDTO;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
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
import view.dialog.MessageDialog;
import view.dialog.PlayerInfoDialog;
import view.panel.MapPanel;

/**
 * A GUI implementation of the game view using Swing components.
 * This class implements both View and GuiView interfaces to provide
 * a graphical user interface for the game, managing all visual components
 * and user interactions.
 */
public class GuiGameView implements View, GuiView, KeyListener {
  private static final String WELCOME_CARD = "WELCOME";
  private static final String MENU_CARD = "MENU";
  private static final String GAME_CARD = "GAME";

  private final Controller controller;
  private final JFrame mainFrame;
  private final JPanel mainPanel;
  private final CardLayout cardLayout;
  private final JTextArea messageArea;
  private JLabel turnLabel;
  private JLabel playerNameLabel;
  private JLabel itemsLabel;
  private MapPanel mapPanel;

  /**
   * Constructs a new GUI view for the game.
   *
   * @param gameController the controller that will handle game logic and user actions
   */
  public GuiGameView(Controller gameController) {
    this.controller = gameController;
    this.mainFrame = new JFrame("Kill Doctor Lucky");
    this.cardLayout = new CardLayout();
    this.mainPanel = new JPanel(cardLayout);
    this.messageArea = new JTextArea(10, 40);
    messageArea.setEditable(false);

    mainFrame.addKeyListener(this);
    mainFrame.setFocusable(true);

    setupMainFrame();
    createPanels();
  }

  /**
   * Sets up the main application window with default size and properties.
   */
  private void setupMainFrame() {
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setPreferredSize(new Dimension(1024, 768));
    mainFrame.add(mainPanel);
    mainFrame.setLocationRelativeTo(null);
  }

  /**
   * Creates and adds all main panels to the card layout.
   * Includes welcome panel, menu panel, and game panel.
   */
  private void createPanels() {
    // Welcome Panel
    mainPanel.add(createWelcomePanel(), WELCOME_CARD);

    // Menu Panel
    mainPanel.add(createMenuPanel(), MENU_CARD);

    // Game Panel
    mainPanel.add(createGamePanel(), GAME_CARD);
  }

  /**
   * Creates the welcome panel with title, author info, and start button.
   *
   * @return the configured welcome panel
   */
  private JPanel createWelcomePanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(new Color(245, 245, 245));
    panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

    // Title
    final JLabel titleLabel = createStyledLabel("Kill Doctor Lucky", 48, Font.BOLD);

    // Author
    final JLabel authorLabel = createStyledLabel("Created by Li,Zhanhao", 24, Font.PLAIN);

    // Course Info
    final JLabel courseLabel = createStyledLabel("CS5010 Project", 20, Font.PLAIN);

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

  /**
   * Creates the menu panel with all game menu options.
   *
   * @return the configured menu panel
   */
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

  /**
   * Creates the main game panel with map and control sections.
   *
   * @return the configured game panel
   */
  private JPanel createGamePanel() {
    JPanel panel = new JPanel(new BorderLayout());

    panel.setFocusable(true);
    panel.addKeyListener(this);

    JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    mainSplitPane.setResizeWeight(0.7);

    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.setBorder(BorderFactory.createTitledBorder("Game Map"));
    mapPanel = new MapPanel(controller.getTown().getPlaces(), 58);
    JScrollPane mapScrollPane = new JScrollPane(mapPanel);
    leftPanel.add(mapScrollPane, BorderLayout.CENTER);
    mainSplitPane.setLeftComponent(leftPanel);

    JPanel rightPanel = new JPanel(new BorderLayout());

    JPanel actionPanel = createActionPanel();
    rightPanel.add(actionPanel, BorderLayout.NORTH);

    JPanel infoPanel = createInfoPanel();
    rightPanel.add(infoPanel, BorderLayout.CENTER);

    mainSplitPane.setRightComponent(rightPanel);

    panel.add(mainSplitPane, BorderLayout.CENTER);
    return panel;
  }

  /**
   * Creates the action panel showing available keyboard commands.
   *
   * @return the configured action panel
   */
  private JPanel createActionPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createTitledBorder("Actions"));

    JLabel instructionLabel = new JLabel("<html><body>"
        + "Actions available:<br/>"
        + "Press 'M' to Move Player<br/>"
        + "Press 'L' to Look Around<br/>"
        + "Press 'P' to Pick Up Item<br/>"
        + "Press 'E' to Move Pet<br/>"
        + "Press 'A' to Attack Target"
        + "</body></html>");

    instructionLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
    instructionLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    panel.add(instructionLabel);

    return panel;
  }

  /**
   * Creates the information panel showing current game state.
   *
   * @return the configured information panel
   */
  private JPanel createInfoPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createTitledBorder("Player Information"));

    turnLabel = new JLabel("Turn: ");
    turnLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    playerNameLabel = new JLabel("Player: ");
    playerNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    itemsLabel = new JLabel("Items: ");
    itemsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    panel.add(turnLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
    panel.add(playerNameLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
    panel.add(itemsLabel);

    return panel;
  }

  /**
   * Updates the player information display with current game state.
   *
   * @param info data transfer object containing current player information
   */
  @Override
  public void updatePlayerInfo(PlayerInfoDTO info) {
    SwingUtilities.invokeLater(() -> {
      turnLabel.setText("Turn: " + info.getCurrentTurn());
      playerNameLabel.setText("Player: " + info.getPlayerName());

      String itemsText = info.getItems() == null
          ? "None" : String.join(", ", info.getItems());
      itemsLabel.setText("Items: " + itemsText);

      // update target and player locations on map
      String targetPlace = controller.getTown().getTarget().getCurrentPlace().getName();
      System.out.println(
          "Target place: " + targetPlace + "Player place: " + info.getCurrentPlace());
      mapPanel.updateLocations(targetPlace, info.getCurrentPlace());
    });
  }

  /**
   * Creates a styled JLabel with specified properties.
   *
   * @param text  text to display in the label
   * @param size  font size for the label
   * @param style font style for the label
   * @return the configured JLabel
   */
  private JLabel createStyledLabel(String text, int size, int style) {
    JLabel label = new JLabel(text);
    label.setFont(new Font("Arial", style, size));
    label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    return label;
  }

  /**
   * Creates a menu button with specified text and command.
   *
   * @param text    text to display on the button
   * @param command command to execute when clicked
   * @return the configured JButton
   */
  private JButton createMenuButton(String text, String command) {
    JButton button = new JButton(text);
    button.setFont(new Font("Arial", Font.PLAIN, 18));
    button.setAlignmentX(JButton.CENTER_ALIGNMENT);
    button.setMaximumSize(new Dimension(300, 40));
    button.setActionCommand(command);
    button.addActionListener(e -> {
      try {
        handleCommand(command);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    });
    return button;
  }

  /**
   * Handles menu commands and executes appropriate actions.
   *
   * @param command the command to handle
   * @throws IOException if there's an error executing the command
   */
  private void handleCommand(String command) throws IOException {
    switch (command) {
      case "ADD_PLAYER_SCREEN": {
        AddPlayerDialog dialog = new AddPlayerDialog(this);
        dialog.setVisible(true);
        break;
      }
      case "ADD_COMPUTER_PLAYER":
        controller.executeCommand("ADD_COMPUTER");
        break;
      case "SHOW_PLAYER_INFO": {
        PlayerInfoDialog dialog = new PlayerInfoDialog(this);
        dialog.setVisible(true);
        break;
      }
      case "START_TURNS":
        System.out.println("Starting game...");
        if (controller.executeCommand("START_TURNS")) {
          cardLayout.show(mainPanel, GAME_CARD);
          mainFrame.requestFocusInWindow();
        }
        break;
      case "QUIT":
        close();
        break;
      default:
        System.out.println("Executing command: " + command);
        break;
    }
  }

  /**
   * Displays an error message dialog.
   *
   * @param message the error message to display
   */
  private void showError(String message) {
    JOptionPane.showMessageDialog(mainFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Initializes the GUI components and shows the welcome screen.
   *
   * @throws IOException if there's an error during initialization
   */
  @Override
  public void initialize() throws IOException {
    SwingUtilities.invokeLater(() -> {
      mainFrame.pack();
      mainFrame.setVisible(true);
      cardLayout.show(mainPanel, WELCOME_CARD);
    });
  }

  /**
   * Displays a message in the message area.
   *
   * @param message the message to display
   */
  @Override
  public void showMessage(String message) {
    SwingUtilities.invokeLater(() -> {
      messageArea.append(message + "\n");
      messageArea.setCaretPosition(messageArea.getDocument().getLength());
    });
  }

  /**
   * Shows a GUI message dialog with specified title and message.
   *
   * @param title      the dialog title
   * @param message    the message to display
   * @param buttonText the text for the dialog button
   */
  @Override
  public void showGuiMessage(String title, String message, String buttonText) {
    SwingUtilities.invokeLater(
        () -> MessageDialog.showMessage(mainFrame, title, message, buttonText, null));
  }

  /**
   * Shows a GUI message dialog with callback on close.
   *
   * @param title      the dialog title
   * @param message    the message to display
   * @param buttonText the text for the dialog button
   * @param onClose    callback to execute when dialog closes
   */

  @Override
  public void showGuiMessage(String title, String message, String buttonText, Runnable onClose) {
    SwingUtilities.invokeLater(
        () -> MessageDialog.showMessage(mainFrame, title, message, buttonText, onClose));
  }

  /**
   * Shows a GUI number input dialog with validation.
   *
   * @param title      the dialog title
   * @param message    the message to display
   * @param buttonText the text for the dialog button
   * @param minValue   minimum allowed value
   * @param maxValue   maximum allowed value
   * @return CompletableFuture containing the entered number
   */
  @Override
  public CompletableFuture<Integer> showGuiNumberMessage(String title, String message,
                                                         String buttonText,
                                                         int minValue, int maxValue) {
    CompletableFuture<Integer> future = new CompletableFuture<>();

    SwingUtilities.invokeLater(
        () -> MessageDialog.showNumberInputAsync(mainFrame, title, message, buttonText, minValue,
                maxValue)
            .thenAccept(result -> {
              if (result.hasNumber()) {
                future.complete(result.getNumber());
              } else {
                future.completeExceptionally(new IllegalStateException("No number input provided"));
              }
            }));

    return future;
  }

  /**
   * Gets string input from user via dialog.
   *
   * @return the user's input string
   */
  @Override
  public String getStringInput() {
    return JOptionPane.showInputDialog(mainFrame, "Enter value:");
  }

  /**
   * Gets numeric input from user via dialog.
   *
   * @return the user's numeric input
   */
  @Override
  public int getNumberInput() {
    try {
      String input = JOptionPane.showInputDialog(mainFrame, "Enter number:");
      return Integer.parseInt(input);
    } catch (NumberFormatException e) {
      showError("Invalid number format. Please try again.");
      return getNumberInput();
    }
  }

  /**
   * Closes the GUI window and disposes of resources.
   */
  @Override
  public void close() {
    SwingUtilities.invokeLater(mainFrame::dispose);
  }

  /**
   * Gets the main application window.
   *
   * @return the main JFrame
   */
  public JFrame getMainFrame() {
    return mainFrame;
  }

  /**
   * Gets the game controller.
   *
   * @return the Controller instance
   */
  public Controller getController() {
    return controller;
  }

  /**
   * Resets the game view to the menu screen.
   */
  @Override
  public void resetGame() {
    cardLayout.show(mainPanel, MENU_CARD);
  }

  /**
   * Handles keyboard input for game actions.
   * Supports the following keys:
   * - 'L': Look around
   * - 'M': Move player
   * - 'P': Pick up item
   * - 'E': Move pet
   * - 'A': Attack target
   *
   * @param e the key event
   */
  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyChar() == 'L' || e.getKeyChar() == 'l') {
      try {
        controller.executeCommand("LOOK");
      } catch (IOException ex) {
        showError("Error executing look command: " + ex.getMessage());
      }
    } else if (e.getKeyChar() == 'M' || e.getKeyChar() == 'm') {

      // Get current player's place
      int currentPlayerIndex = controller.getTown().getCurrentPlayerIndex();
      Player currentPlayer = controller.getTown().getPlayers().get(currentPlayerIndex);
      Place currentPlace = controller.getTown().getPlaceByNumber(
          currentPlayer.getPlayerCurrentPlaceNumber());

      // Show move options and handle clicks
      mapPanel.setClickListener((clickedPlace, isValidMove) -> {
        if (isValidMove) {
          System.out.println("Success");
          try {
            // Execute move command
            int placeNumber = Integer.parseInt(clickedPlace.getPlaceNumber());
            String placeName = clickedPlace.getName();
            controller.executeCommand("MOVE " + "," + placeName + "," + placeNumber);
          } catch (IOException ex) {
            showError("Error executing move: " + ex.getMessage());
          }
        } else {
          System.out.println("Wrong");
          showGuiMessage("Invalid Move", "You cannot move to this place.", "OK");
        }
      });

      mapPanel.showMoveOptions(currentPlace);


    } else if (e.getKeyChar() == 'P' || e.getKeyChar() == 'p') {
      try {
        controller.executeCommand("PICK");
      } catch (IOException ex) {
        showError("Error executing pick command: " + ex.getMessage());
      }
    } else if (e.getKeyChar() == 'E' || e.getKeyChar() == 'e') {
      try {
        controller.executeCommand("PETMOVE");
      } catch (IOException ex) {
        showError("Error executing pet move command: " + ex.getMessage());
      }
    } else if (e.getKeyChar() == 'A' || e.getKeyChar() == 'a') {
      try {
        controller.executeCommand("ATTACK");
      } catch (IOException ex) {
        showError("Error executing attack command: " + ex.getMessage());
      }
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {
    // 不需要实现
  }

  @Override
  public void keyReleased(KeyEvent e) {
    // 不需要实现
  }

}