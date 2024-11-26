package view.panel;

import controller.Controller;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import model.place.Place;

/**
 * Panel that manages the main game display including welcome screen,
 * map display and game controls.
 */
public class GamePanel extends JPanel {
  private static final String WELCOME_CARD = "WELCOME";
  private static final String GAME_CARD = "GAME";

  private final Controller controller;
  private final CardLayout cardLayout;
  private final JPanel contentPanel;
  private final List<Place> places;
  private MapPanel mapPanel;

  /**
   * Constructs a new GamePanel.
   *
   * @param gameController the game controller
   * @param gamePlaces     the list of places in the game
   */
  public GamePanel(Controller gameController, List<Place> gamePlaces) {
    this.controller = gameController;
    this.places = gamePlaces;
    this.cardLayout = new CardLayout();
    this.contentPanel = new JPanel(cardLayout);

    setLayout(new BorderLayout());
    initializeComponents();
  }

  /**
   * Shows the welcome screen.
   */
  public void showWelcomeScreen() {
    cardLayout.show(contentPanel, WELCOME_CARD);
  }

  /**
   * Shows the main game interface.
   */
  public void showGameScreen() {
    cardLayout.show(contentPanel, GAME_CARD);
  }

  private void initializeComponents() {
    // Create welcome panel
    JPanel welcomePanel = createWelcomePanel();

    // Create game panel
    JPanel gamePanel = createGamePanel();

    // Add panels to card layout
    contentPanel.add(welcomePanel, WELCOME_CARD);
    contentPanel.add(gamePanel, GAME_CARD);

    // Add content panel to main panel
    add(contentPanel, BorderLayout.CENTER);
  }

  private JPanel createWelcomePanel() {
    JPanel welcomePanel = new JPanel(new GridBagLayout());
    welcomePanel.setBackground(new Color(240, 240, 240));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new java.awt.Insets(10, 10, 10, 10);

    // Title
    JLabel titleLabel = new JLabel("Kill Doctor Lucky");
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
    titleLabel.setHorizontalAlignment(JLabel.CENTER);

    // Author info
    JLabel authorLabel = new JLabel("Created by: Zhanhao Li");
    authorLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
    authorLabel.setHorizontalAlignment(JLabel.CENTER);

    // Description
    JLabel descLabel = new JLabel("<html><center>Welcome to Kill Doctor Lucky!</center></html>");
    descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
    descLabel.setHorizontalAlignment(JLabel.CENTER);

    // Start game button
    JButton startButton = new JButton("Start Game");
    startButton.setFont(new Font("SansSerif", Font.BOLD, 16));
    startButton.addActionListener(e -> {
      try {
        controller.startGame();
        showGameScreen();
      } catch (IOException ex) {
        // Handle exception
      }
    });

    // Add components
    gbc.gridy = 0;
    welcomePanel.add(titleLabel, gbc);

    gbc.gridy = 1;
    welcomePanel.add(authorLabel, gbc);

    gbc.gridy = 2;
    welcomePanel.add(descLabel, gbc);

    gbc.gridy = 3;
    gbc.insets.top = 30;
    welcomePanel.add(startButton, gbc);

    return welcomePanel;
  }

  private JPanel createGamePanel() {
    JPanel gamePanel = new JPanel(new BorderLayout());
    gamePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

    // Initialize map panel with places
//    mapPanel = new MapPanel(places);
    JPanel mapContainer = new JPanel(new BorderLayout());
    mapContainer.add(mapPanel, BorderLayout.CENTER);

    // Create control panel
    JPanel controlPanel = createControlPanel();

    // Add components
    gamePanel.add(mapContainer, BorderLayout.CENTER);
    gamePanel.add(controlPanel, BorderLayout.SOUTH);

    return gamePanel;
  }

  private JPanel createControlPanel() {
    JPanel controlPanel = new JPanel(new GridBagLayout());
    controlPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new java.awt.Insets(5, 5, 5, 5);

    // Create game control buttons
    JButton[] buttons = createGameControlButtons();

    // Add buttons to panel with equal spacing
    for (int i = 0; i < buttons.length; i++) {
      gbc.gridx = i;
      gbc.weightx = 1.0;
      controlPanel.add(buttons[i], gbc);
    }

    return controlPanel;
  }

  private JButton[] createGameControlButtons() {
    JButton[] buttons = new JButton[4];
    String[] buttonLabels = {"Move", "Look Around", "Pick Up", "Attack"};

    for (int i = 0; i < buttons.length; i++) {
      buttons[i] = new JButton(buttonLabels[i]);
      buttons[i].setFont(new Font("SansSerif", Font.PLAIN, 14));
      configureButton(buttons[i], i);
    }

    return buttons;
  }

  private void configureButton(JButton button, int actionType) {
    button.addActionListener(e -> {
      try {
        switch (actionType) {
          case 0: // Move
            handleMove();
            break;
          case 1: // Look Around
            handleLookAround();
            break;
          case 2: // Pick Up
            handlePickUp();
            break;
          case 3: // Attack
            handleAttack();
            break;
          default:
            break;
        }
      } catch (Exception ex) {
        // Handle exceptions
      }
    });
  }

  // Action handlers (to be implemented based on your game logic)
  private void handleMove() {
    // Implement move action
  }

  private void handleLookAround() {
    // Implement look around action
  }

  private void handlePickUp() {
    // Implement pick up action
  }

  private void handleAttack() {
    // Implement attack action
  }
}