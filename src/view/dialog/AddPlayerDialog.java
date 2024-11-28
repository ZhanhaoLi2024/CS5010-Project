package view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import view.GuiGameView;
import view.panel.MapPanel;

/**
 * Dialog for adding a new human player to the game.
 */
public class AddPlayerDialog extends JDialog {
  private final GuiGameView parentView;
  private JTextField nameField;
  private JSpinner placeSpinner;
  private JSpinner limitSpinner;

  public AddPlayerDialog(GuiGameView parent) {
    super(parent.getMainFrame(), "Add Human Player", true);
    this.parentView = parent;
    initializeDialog();
  }

  private void initializeDialog() {
    setLayout(new BorderLayout(10, 10));
    setSize(900, 600);
    setLocationRelativeTo(getParent());

    // Left panel (Map)
    JPanel leftPanel = createMapPanel();

    // Form panel
    JPanel formPanel = createFormPanel();

    // Button panel
    final JPanel buttonPanel = createButtonPanel();

    // Split pane
    JSplitPane splitPane = new JSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        leftPanel,
        formPanel
    );
    splitPane.setDividerLocation(450);
    splitPane.setResizeWeight(0.5);

    add(splitPane, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  private JPanel createMapPanel() {
    JPanel panel = new JPanel(new BorderLayout(5, 5));
    panel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(),
        "Game Map"
    ));
    MapPanel mapPanel = new MapPanel(parentView.getController().getTown().getPlaces(), 40);
    mapPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    panel.add(mapPanel, BorderLayout.CENTER);
    return panel;
  }

  private JPanel createFormPanel() {
    JPanel formPanel = new JPanel();
    formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    final Dimension inputSize = new Dimension(200, 25);
    final Font labelFont = new Font("Dialog", Font.PLAIN, 12);

    // Initialize input fields
    nameField = new JTextField();
    placeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    limitSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));

    // Player Name input
    JPanel namePanel = createInputPanel("Player Name:", nameField, inputSize, labelFont);

    // Starting Place input
    JPanel placePanel =
        createInputPanel("Starting Place (1-20):", placeSpinner, inputSize, labelFont);

    // Carry Limit input
    JPanel limitPanel = createInputPanel("Carry Limit (1-10):", limitSpinner, inputSize, labelFont);

    formPanel.add(namePanel);
    formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    formPanel.add(placePanel);
    formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    formPanel.add(limitPanel);
    formPanel.add(Box.createVerticalGlue());

    return formPanel;
  }

  private JPanel createInputPanel(String labelText, JComponent input,
                                  Dimension inputSize, Font labelFont) {
    JPanel panel = new JPanel(new BorderLayout(10, 5));
    panel.setMaximumSize(new Dimension(350, 50));

    JLabel label = new JLabel(labelText);
    label.setFont(labelFont);

    input.setPreferredSize(inputSize);
    if (input instanceof JTextField) {
      ((JTextField) input).setHorizontalAlignment(JTextField.RIGHT);
    } else if (input instanceof JSpinner) {
      JComponent editor = ((JSpinner) input).getEditor();
      ((JSpinner.DefaultEditor) editor).getTextField().setHorizontalAlignment(JTextField.RIGHT);
    }

    panel.add(label, BorderLayout.WEST);
    panel.add(input, BorderLayout.EAST);
    return panel;
  }

  private JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelButton = new JButton("Cancel");
    JButton addButton = new JButton("Add Player");

    cancelButton.addActionListener(e -> dispose());
    addButton.addActionListener(e -> handleAddPlayer());

    buttonPanel.add(cancelButton);
    buttonPanel.add(addButton);
    return buttonPanel;
  }

  private void handleAddPlayer() {
    String name = nameField.getText().trim();
    int place = (Integer) placeSpinner.getValue();
    int limit = (Integer) limitSpinner.getValue();

    if (name.isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "Player name cannot be empty!",
          "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    try {
      parentView.getController().executeCommand("ADD_PLAYER " + name + " " + place + " " + limit);
      dispose();
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this,
          "Error adding player: " + ex.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
}