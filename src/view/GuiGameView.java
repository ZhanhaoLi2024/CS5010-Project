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
    MapPanel mapPanel = new MapPanel(controller.getTown().getPlaces(), 58);
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

//    for (String action : actions) {
//      JButton button = new JButton(action);
//      button.setAlignmentX(Component.CENTER_ALIGNMENT);
//      button.setMaximumSize(new Dimension(200, 30));
//      panel.add(Box.createRigidArea(new Dimension(0, 5)));
//      panel.add(button);
//    }

    return panel;
  }

  private JPanel createInfoPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createTitledBorder("Player Information"));

    // 回合信息
    turnLabel = new JLabel("Turn: ");
    turnLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // 玩家信息
    playerNameLabel = new JLabel("Player: ");
    playerNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // 物品信息
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
    });
  }

//  private JPanel createControlPanel() {
//    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
//    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//
//    String[] buttonLabels = {"Move (M)", "Look (L)", "Pick (P)", "Attack (A)", "Pet Move (T)"};
//    String[] commands = {"MOVE", "LOOK", "PICK", "ATTACK", "PETMOVE"};
//
//    for (int i = 0; i < buttonLabels.length; i++) {
//      panel.add(createActionButton(buttonLabels[i], commands[i]));
//    }
//
//    return panel;
//  }

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
        System.out.println("Starting game...");
//        if (controller.executeCommand("START_TURNS")) {
//          cardLayout.show(mainPanel, GAME_CARD);
//        }
        if (controller.executeCommand("START_TURNS")) {
          cardLayout.show(mainPanel, GAME_CARD);
          mainFrame.requestFocusInWindow();
        }
      } else if (command.equals("QUIT")) {
        close();
      } else if (command.equals("MOVE")) {
//        controller.executeCommand("MOVE");
      } else if (command.equals("LOOK")) {
        System.out.println("Looking around...");
        controller.executeCommand("LOOK");
      } else if (command.equals("PICK")) {
//        controller.executeCommand("PICK");
      } else if (command.equals("ATTACK")) {
//        controller.executeCommand("ATTACK");
      } else if (command.equals("PETMOVE")) {
//        controller.executeCommand("PETMOVE");
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
  public void close() throws IOException {
    SwingUtilities.invokeLater(() -> mainFrame.dispose());
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

//  private void showError(String message) {
//    JOptionPane.showMessageDialog(mainFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
//    mainFrame.requestFocusInWindow(); // 对话框关闭后重新请求焦点
//  }

}