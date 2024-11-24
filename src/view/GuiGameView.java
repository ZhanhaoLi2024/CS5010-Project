package view;

import controller.Controller;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import model.item.Item;
import model.place.Place;
import model.player.Player;
import model.target.Target;

public class GuiGameView implements GameView {
  private final Controller controller;
  private JFrame mainFrame;
  private JPanel mainPanel;
  private CardLayout cardLayout;
  private JTextArea messageArea;
  private JPanel welcomePanel;
  private JPanel mainMenuPanel;
  private JPanel gamePanel;
  private JPanel mapPanel;
  private JPanel infoPanel;
  private String[] playerInfo;

  public GuiGameView(Controller gameController) {
    this.controller = gameController;
    SwingUtilities.invokeLater(this::createAndShowGUI);
  }

  private void createAndShowGUI() {
    // Create main frame
    mainFrame = new JFrame("Kill Doctor Lucky");
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setPreferredSize(new Dimension(800, 600));

    // Create main panel with CardLayout
    mainPanel = new JPanel();
    cardLayout = new CardLayout();
    mainPanel.setLayout(cardLayout);

    // Create all panels
    createWelcomePanel();
    createMainMenuPanel();
    createGamePanel();

    // Add panels to main panel
    mainPanel.add(welcomePanel, "WELCOME");
    mainPanel.add(mainMenuPanel, "MENU");
    mainPanel.add(gamePanel, "GAME");

    // Add main panel to frame
    mainFrame.add(mainPanel);
    mainFrame.pack();
    mainFrame.setLocationRelativeTo(null);
  }

  private void createWelcomePanel() {
    welcomePanel = new JPanel();
    welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
    welcomePanel.setBackground(new Color(245, 245, 245));
    welcomePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

    // Game Title
    JLabel titleLabel = new JLabel("Kill Doctor Lucky");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
    titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

    // Author Information
    JLabel authorLabel = new JLabel("Created by Zhanhao Li");
    authorLabel.setFont(new Font("Arial", Font.PLAIN, 24));
    authorLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

    // Course Information
    JLabel courseLabel = new JLabel("CS 5010 Project");
    courseLabel.setFont(new Font("Arial", Font.PLAIN, 20));
    courseLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

    // Additional Description
    JLabel descLabel = new JLabel("Object-Oriented Design");
    descLabel.setFont(new Font("Arial", Font.ITALIC, 18));
    descLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

    // Start Button
    JButton startButton = new JButton("Start Game");
    startButton.setFont(new Font("Arial", Font.BOLD, 24));
    startButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
    startButton.setMaximumSize(new Dimension(200, 50));
    startButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

    // Add components with spacing
    welcomePanel.add(Box.createVerticalGlue());
    welcomePanel.add(titleLabel);
    welcomePanel.add(Box.createRigidArea(new Dimension(0, 30)));
    welcomePanel.add(authorLabel);
    welcomePanel.add(Box.createRigidArea(new Dimension(0, 10)));
    welcomePanel.add(courseLabel);
    welcomePanel.add(Box.createRigidArea(new Dimension(0, 10)));
    welcomePanel.add(descLabel);
    welcomePanel.add(Box.createRigidArea(new Dimension(0, 50)));
    welcomePanel.add(startButton);
    welcomePanel.add(Box.createVerticalGlue());
  }

  private void createMainMenuPanel() {
    mainMenuPanel = new JPanel();
    mainMenuPanel.setLayout(new BoxLayout(mainMenuPanel, BoxLayout.Y_AXIS));
    mainMenuPanel.setBackground(new Color(245, 245, 245));
    mainMenuPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

    // Title
    JLabel menuTitle = new JLabel("Main Menu");
    menuTitle.setFont(new Font("Arial", Font.BOLD, 36));
    menuTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);

    // Create menu buttons
    String[] menuOptions = {
        "Add Human Player",
        "Add Computer Player",
        "Start Game",
    };

    mainMenuPanel.add(menuTitle);
    mainMenuPanel.add(Box.createRigidArea(new Dimension(0, 30)));

    for (int i = 0; i < menuOptions.length; i++) {
      JButton menuButton = new JButton(menuOptions[i]);
      menuButton.setFont(new Font("Arial", Font.PLAIN, 18));
      menuButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
      menuButton.setMaximumSize(new Dimension(300, 40));
      final int choice = i + 1;
      menuButton.addActionListener(e -> handleMenuChoice(choice));
      mainMenuPanel.add(menuButton);
      mainMenuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    // Exit button
    JButton exitButton = new JButton("Exit Game");
    exitButton.setFont(new Font("Arial", Font.PLAIN, 18));
    exitButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
    exitButton.setMaximumSize(new Dimension(300, 40));
    exitButton.addActionListener(e -> System.exit(0));

    mainMenuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    mainMenuPanel.add(exitButton);
  }

  private void showAddPlayerDialog() {
    // 创建对话框
    JDialog dialog = new JDialog(mainFrame, "Add Human Player", true);
    dialog.setLayout(new BorderLayout());
    dialog.setSize(400, 300);
    dialog.setLocationRelativeTo(mainFrame);

    // 创建表单面板
    JPanel formPanel = new JPanel();
    formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // 玩家名称输入
    JPanel namePanel = new JPanel(new BorderLayout());
    namePanel.add(new JLabel("Player Name:"), BorderLayout.NORTH);
    JTextField nameField = new JTextField(20);
    namePanel.add(nameField, BorderLayout.CENTER);
    formPanel.add(namePanel);
    formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

    // 起始位置输入
    JPanel placePanel = new JPanel(new BorderLayout());
    placePanel.add(new JLabel("Starting Place (1-20):"), BorderLayout.NORTH);
    JSpinner placeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    placePanel.add(placeSpinner, BorderLayout.CENTER);
    formPanel.add(placePanel);
    formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

    // 携带限制输入
    JPanel limitPanel = new JPanel(new BorderLayout());
    limitPanel.add(new JLabel("Carry Limit (1-10):"), BorderLayout.NORTH);
    JSpinner limitSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
    limitPanel.add(limitSpinner, BorderLayout.CENTER);
    formPanel.add(limitPanel);

    // 创建按钮面板
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelButton = new JButton("Cancel");
    JButton addButton = new JButton("Add Player");

    // 添加按钮事件
    cancelButton.addActionListener(e -> dialog.dispose());
    addButton.addActionListener(e -> {
      String name = nameField.getText().trim();
      int place = (Integer) placeSpinner.getValue();
      int limit = (Integer) limitSpinner.getValue();

      if (name.isEmpty()) {
        JOptionPane.showMessageDialog(dialog,
            "Player name cannot be empty!",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      // 保存玩家信息并关闭对话框
      playerInfo = new String[] {name, String.valueOf(place), String.valueOf(limit)};
      dialog.dispose();
    });

    buttonPanel.add(cancelButton);
    buttonPanel.add(addButton);

    // 将所有组件添加到对话框
    dialog.add(formPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);

    // 显示对话框
    dialog.setVisible(true);
  }

  private void createGamePanel() {
    gamePanel = new JPanel(new BorderLayout());

    // Create map panel
    mapPanel = new JPanel();
    mapPanel.setBorder(BorderFactory.createTitledBorder("Game Map"));
    gamePanel.add(mapPanel, BorderLayout.CENTER);

    // Create info panel
    infoPanel = new JPanel(new BorderLayout());
    infoPanel.setBorder(BorderFactory.createTitledBorder("Game Information"));

    // Create message area
    messageArea = new JTextArea(10, 40);
    messageArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(messageArea);
    infoPanel.add(scrollPane, BorderLayout.CENTER);

    // Add back to menu button
    JButton backButton = new JButton("Back to Menu");
    backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
    infoPanel.add(backButton, BorderLayout.SOUTH);

    gamePanel.add(infoPanel, BorderLayout.SOUTH);
  }

  private void handleMenuChoice(int choice) {
    switch (choice) {
      case 1: // Add Human Player
        showAddPlayerDialog();
        if (playerInfo != null) {
          String name = playerInfo[0];
          int place = Integer.parseInt(playerInfo[1]);
          int limit = Integer.parseInt(playerInfo[2]);
          // 这里通过Command添加玩家
          try {
            controller.handleAddHumanPlayer(name, place, limit);
          } catch (IOException ex) {
            showMessage("Error adding player: " + ex.getMessage());
          }
          playerInfo = null; // 清除已使用的数据
        }
        break;
      case 6: // Start Game
        cardLayout.show(mainPanel, "GAME");
        break;
      default:
        notifyMenuChoice(choice);
    }
  }

  private void notifyMenuChoice(int choice) {
    // This method will be used by the controller to handle menu choices
    // Implementation will be added when integrating with the controller
  }

  @Override
  public void initialize() {
    SwingUtilities.invokeLater(() -> {
      mainFrame.setVisible(true);
      cardLayout.show(mainPanel, "WELCOME");
    });
  }

  @Override
  public void showWelcomeScreen() {
    SwingUtilities.invokeLater(() -> cardLayout.show(mainPanel, "WELCOME"));
  }

  @Override
  public void updateMap(List<Place> places, List<Player> players, Target target, Image mapImage) {
    SwingUtilities.invokeLater(() -> {
      if (mapImage != null) {
        // Update map display logic here
      }
    });
  }

  @Override
  public void updatePlayerInfo(Player player, Place currentPlace) {
    SwingUtilities.invokeLater(() -> {
      String info = String.format("Current Player: %s\nLocation: %s\nItems: %d/%d\n",
          player.getName(),
          currentPlace.getName(),
          player.getCurrentCarriedItems().size(),
          player.getCarryLimit());
      messageArea.append(info);
    });
  }

  @Override
  public void showMessage(String message) {
    SwingUtilities.invokeLater(() -> {
      messageArea.append(message + "\n");
      messageArea.setCaretPosition(messageArea.getDocument().getLength());
    });
  }

  @Override
  public int getMoveInput(List<Place> neighbors) {
    String[] options = new String[neighbors.size()];
    for (int i = 0; i < neighbors.size(); i++) {
      options[i] = neighbors.get(i).getName();
    }

    int choice = JOptionPane.showOptionDialog(mainFrame,
        "Choose a place to move to:",
        "Move Player",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[0]);

    return choice + 1;
  }

  @Override
  public int getPickupInput(List<Item> items) {
    if (items.isEmpty()) {
      JOptionPane.showMessageDialog(mainFrame, "No items available to pick up.");
      return -1;
    }

    String[] options = new String[items.size()];
    for (int i = 0; i < items.size(); i++) {
      Item item = items.get(i);
      options[i] = String.format("%s (Damage: %d)", item.getName(), item.getDamage());
    }

    int choice = JOptionPane.showOptionDialog(mainFrame,
        "Choose an item to pick up:",
        "Pick Up Item",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[0]);

    return choice + 1;
  }

  @Override
  public int getAttackInput(List<Item> carriedItems) {
    String[] options = new String[carriedItems.size() + 1];
    options[0] = "Poke in the eye (1 damage)";

    for (int i = 0; i < carriedItems.size(); i++) {
      Item item = carriedItems.get(i);
      options[i + 1] = String.format("Use %s (Damage: %d)",
          item.getName(), item.getDamage());
    }

    int choice = JOptionPane.showOptionDialog(mainFrame,
        "Choose your attack:",
        "Attack Target",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[0]);

    return choice;
  }

  @Override
  public int displayMainMenu() {
    cardLayout.show(mainPanel, "MENU");
    // Wait for user input through button clicks
    // The actual menu choice will be handled through action listeners
    return -1; // Placeholder return
  }

  @Override
  public String getUserInput() {
    return JOptionPane.showInputDialog(mainFrame, "Enter your input:");
  }

  @Override
  public int getNumberInput() {
    String input = JOptionPane.showInputDialog(mainFrame, "Enter a number:");
    try {
      return Integer.parseInt(input);
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(mainFrame, "Invalid input. Please enter a number.");
      return getNumberInput();
    }
  }

  @Override
  public void close() {
    SwingUtilities.invokeLater(() -> {
      mainFrame.dispose();
    });
  }
}