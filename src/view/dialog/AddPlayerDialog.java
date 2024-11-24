package view.dialog;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 * Dialog for adding a new human player to the game.
 * Collects player name, starting position, and carry limit.
 */
public class AddPlayerDialog extends JDialog {
  private final JTextField nameField;
  private final JSpinner positionSpinner;
  private final JSpinner carryLimitSpinner;
  private final Consumer<PlayerInfo> onSubmit;

  /**
   * Creates a new AddPlayerDialog.
   *
   * @param parent         The parent frame
   * @param maxPosition    Maximum valid starting position
   * @param submitCallback Callback to handle the submitted player information
   */
  public AddPlayerDialog(JFrame parent, int maxPosition, Consumer<PlayerInfo> submitCallback) {
    super(parent, "Add Human Player", true);
    this.onSubmit = submitCallback;

    // Create main panel with padding
    JPanel mainPanel = new JPanel(new GridBagLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    // Name field
    gbc.gridy = 0;
    gbc.gridx = 0;
    mainPanel.add(new JLabel("Player Name:"), gbc);

    gbc.gridx = 1;
    nameField = new JTextField(20);
    mainPanel.add(nameField, gbc);

    // Position spinner
    gbc.gridy = 1;
    gbc.gridx = 0;
    mainPanel.add(new JLabel("Starting Position:"), gbc);

    gbc.gridx = 1;
    SpinnerNumberModel positionModel = new SpinnerNumberModel(1, 1, maxPosition, 1);
    positionSpinner = new JSpinner(positionModel);
    mainPanel.add(positionSpinner, gbc);

    // Carry limit spinner
    gbc.gridy = 2;
    gbc.gridx = 0;
    mainPanel.add(new JLabel("Carry Limit:"), gbc);

    gbc.gridx = 1;
    SpinnerNumberModel limitModel = new SpinnerNumberModel(1, 1, 10, 1);
    carryLimitSpinner = new JSpinner(limitModel);
    mainPanel.add(carryLimitSpinner, gbc);

    // Buttons panel
    gbc.gridy = 3;
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

    JButton submitButton = new JButton("Add Player");
    submitButton.addActionListener(e -> handleSubmit());

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dispose());

    buttonPanel.add(submitButton);
    buttonPanel.add(cancelButton);
    mainPanel.add(buttonPanel, gbc);

    // Add to dialog
    add(mainPanel);
    pack();
    setLocationRelativeTo(parent);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
  }

  private void handleSubmit() {
    String name = nameField.getText().trim();
    if (name.isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "Player name cannot be empty",
          "Validation Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    int position = (Integer) positionSpinner.getValue();
    int carryLimit = (Integer) carryLimitSpinner.getValue();

    onSubmit.accept(new PlayerInfo(name, position, carryLimit));
    dispose();
  }

  /**
   * Represents the collected player information.
   */
  public static class PlayerInfo {
    private final String name;
    private final int startPosition;
    private final int carryLimit;

    public PlayerInfo(String name, int startPosition, int carryLimit) {
      this.name = name;
      this.startPosition = startPosition;
      this.carryLimit = carryLimit;
    }

    public String getName() {
      return name;
    }

    public int getStartPosition() {
      return startPosition;
    }

    public int getCarryLimit() {
      return carryLimit;
    }
  }
}