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
 * GUI implementation of the GameView interface.
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

    mainFrame.addKeyListener(this);
    mainFrame.setFocusable(true);

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

    panel.setFocusable(true);
    panel.addKeyListener(this);

    // 创建主分割面板
    JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    mainSplitPane.setResizeWeight(0.7); // 设置左侧占70%宽度

    // 左侧地图面板
    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.setBorder(BorderFactory.createTitledBorder("Game Map"));
//    MapPanel mapPanel = new MapPanel(controller.getTown().getPlaces(), 58);
//    JScrollPane mapScrollPane = new JScrollPane(mapPanel);
//    leftPanel.add(mapScrollPane, BorderLayout.CENTER);
    mapPanel = new MapPanel(controller.getTown().getPlaces(), 58);
    JScrollPane mapScrollPane = new JScrollPane(mapPanel);
    leftPanel.add(mapScrollPane, BorderLayout.CENTER);
    mainSplitPane.setLeftComponent(leftPanel);

    // 右侧面板（包含操作区和信息区）
    JPanel rightPanel = new JPanel(new BorderLayout());

    // 右上操作区
    JPanel actionPanel = createActionPanel();
    rightPanel.add(actionPanel, BorderLayout.NORTH);
    // 右上操作区占50%高度

    // 右下信息区
    JPanel infoPanel = createInfoPanel();
    rightPanel.add(infoPanel, BorderLayout.CENTER);

    mainSplitPane.setRightComponent(rightPanel);

    panel.add(mainSplitPane, BorderLayout.CENTER);
    return panel;
  }

  private JPanel createActionPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createTitledBorder("Actions"));

    String[] actions = {
        "Move Player(M)",
        "Look Around(L)",
        "Pick Up Item(P)",
        "Move Pet(E)",
        "Attack Target(A)"
    };
    String[] commands = {
        "MOVE",
        "LOOK",
        "PICK",
        "PETMOVE",
        "ATTACK"
    };

    for (int i = 0; i < actions.length; i++) {
      JButton button = new JButton(actions[i]);
      button.setAlignmentX(Component.CENTER_ALIGNMENT);
      button.setMaximumSize(new Dimension(200, 30));
      button.setActionCommand(commands[i]);
      final int index = i;
      button.addActionListener(e -> handleCommand(commands[index]));
      panel.add(Box.createRigidArea(new Dimension(0, 5)));
      panel.add(button);
    }

    return panel;
  }

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
        case "MOVE":
//        controller.executeCommand("MOVE");
          break;
        case "LOOK":
          System.out.println("Looking around...");
          controller.executeCommand("LOOK");
          break;
        case "PICK":
//        controller.executeCommand("PICK");
          break;
        case "ATTACK":
//        controller.executeCommand("ATTACK");
          break;
        case "PETMOVE":
//        controller.executeCommand("PETMOVE");
          break;
        default:
          System.out.println("Executing command: " + command);
          break;
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
  public void showMessage(String message) throws IOException {
    SwingUtilities.invokeLater(() -> {
      messageArea.append(message + "\n");
      messageArea.setCaretPosition(messageArea.getDocument().getLength());
    });
  }

  @Override
  public void showGuiMessage(String title, String message, String buttonText) {
    SwingUtilities.invokeLater(() -> {
      MessageDialog.showMessage(mainFrame, title, message, buttonText, null);
    });
  }

  @Override
  public CompletableFuture<Integer> showGuiNumberMessage(String title, String message,
                                                         String buttonText,
                                                         int minValue, int maxValue) {
    CompletableFuture<Integer> future = new CompletableFuture<>();

    SwingUtilities.invokeLater(() -> {
      MessageDialog.showNumberInputAsync(mainFrame, title, message, buttonText, minValue, maxValue)
          .thenAccept(result -> {
            if (result.hasNumber()) {
              future.complete(result.getNumber());
            } else {
              future.completeExceptionally(new IllegalStateException("No number input provided"));
            }
          });
    });

    return future;
  }

  @Override
  public void showGuiMessage(String title, String message, String buttonText, Runnable onClose) {
    SwingUtilities.invokeLater(() -> {
      MessageDialog.showMessage(mainFrame, title, message, buttonText, onClose);
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
  public void close() {
    SwingUtilities.invokeLater(mainFrame::dispose);
  }

  public JFrame getMainFrame() {
    return mainFrame;
  }

  public Controller getController() {
    return controller;
  }

  @Override
  public void resetGame() {
    cardLayout.show(mainPanel, MENU_CARD);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyChar() == 'L' || e.getKeyChar() == 'l') {
      try {
        controller.executeCommand("LOOK");
      } catch (IOException ex) {
        showError("Error executing look command: " + ex.getMessage());
      }
    } else if (e.getKeyChar() == 'M' || e.getKeyChar() == 'm') {
//      try {
//        controller.executeCommand("MOVE");
//      } catch (IOException ex) {
//        showError("Error executing move command: " + ex.getMessage());
//      }
      try {
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

      } catch (Exception ex) {
        showError("Error showing move options: " + ex.getMessage());
      }
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