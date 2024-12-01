package view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.concurrent.CompletableFuture;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * A customizable message dialog that supports both simple messages and number input.
 * Provides both synchronous and asynchronous interactions with configurable callbacks.
 */
public class MessageDialog extends JDialog {
  private final String buttonText;
  private final Runnable onClose;
  private final DialogMode mode;
  private final CompletableFuture<DialogResult> resultFuture;
  private JSpinner numberSpinner;

  /**
   * Creates a new MessageDialog with specified parameters.
   *
   * @param parent        parent window of the dialog
   * @param title         title of the dialog window
   * @param message       message to display in the dialog
   * @param mesButtonText text for the action button
   * @param mesMode       mode of operation (MESSAGE_ONLY or MESSAGE_WITH_NUMBER)
   * @param mesOnClose    callback to execute when dialog closes (can be null)
   */
  public MessageDialog(JFrame parent, String title, String message,
                       String mesButtonText, DialogMode mesMode, Runnable mesOnClose) {
    super(parent, title, true);
    this.buttonText = mesButtonText;
    this.onClose = mesOnClose;
    this.mode = mesMode;
    this.resultFuture = new CompletableFuture<>();

    initializeDialog(message);
  }

  /**
   * Creates and shows a dialog for number input.
   *
   * @param parent     parent window of the dialog
   * @param title      title of the dialog window
   * @param message    message to display
   * @param buttonText text for the action button
   * @param minValue   minimum allowed input value
   * @param maxValue   maximum allowed input value
   * @return CompletableFuture containing the user's input
   */
  public static CompletableFuture<DialogResult> showNumberInputAsync(
      JFrame parent, String title, String message, String buttonText,
      int minValue, int maxValue) {
    MessageDialog dialog = new MessageDialog(
        parent, title, message, buttonText, DialogMode.MESSAGE_WITH_NUMBER, null);
    dialog.initNumberSpinner(minValue, maxValue);
    dialog.setVisible(true);
    return dialog.resultFuture;
  }

  /**
   * Creates and shows a simple message dialog.
   *
   * @param parent     parent window of the dialog
   * @param title      title of the dialog window
   * @param message    message to display
   * @param buttonText text for the action button
   * @param onClose    callback to execute when dialog closes
   */
  public static void showMessage(
      JFrame parent, String title, String message, String buttonText, Runnable onClose) {
    SwingUtilities.invokeLater(() -> {
      MessageDialog dialog = new MessageDialog(
          parent, title, message, buttonText, DialogMode.MESSAGE_ONLY, onClose);
      dialog.setVisible(true);
    });
  }

  /**
   * Initializes the dialog components and layout.
   *
   * @param message the message to display in the dialog
   */
  private void initializeDialog(String message) {
    setLayout(new BorderLayout(10, 10));
    setResizable(true);

    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    contentPanel.setBackground(Color.WHITE);

    // Add message panel
    contentPanel.add(createMessagePanel(message));

    // Add number input if needed
    if (mode == DialogMode.MESSAGE_WITH_NUMBER) {
      contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
      contentPanel.add(createNumberInputPanel());
    }

    JScrollPane scrollPane = new JScrollPane(contentPanel);
    scrollPane.setBorder(null);

    add(scrollPane, BorderLayout.CENTER);
    add(createButtonPanel(), BorderLayout.SOUTH);

    setupDialogSize();
  }

  /**
   * Creates the panel containing the message text.
   *
   * @param message the message to display
   * @return JPanel containing the formatted message
   */
  private JPanel createMessagePanel(String message) {
    final JPanel panel = new JPanel(new BorderLayout());

    StringBuilder htmlMessage = new StringBuilder("<html>");
    for (String line : message.split("\n")) {
      htmlMessage.append("<p style='margin: 0; text-align: left;'>")
          .append(line)
          .append("</p>");
    }
    htmlMessage.append("</html>");

    JLabel messageLabel = new JLabel(htmlMessage.toString());
    messageLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
    messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

    panel.add(messageLabel, BorderLayout.CENTER);
    return panel;
  }

  /**
   * Creates the panel containing the number input spinner.
   *
   * @return JPanel containing the number input controls
   */
  private JPanel createNumberInputPanel() {
    final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

    numberSpinner = new JSpinner();
    numberSpinner.setPreferredSize(new Dimension(80, 25));

    JLabel label = new JLabel("Enter number: ");
    label.setFont(new Font("Dialog", Font.PLAIN, 14));

    panel.add(label);
    panel.add(numberSpinner);

    return panel;
  }

  /**
   * Initializes the number spinner with specified range.
   *
   * @param minValue minimum allowed value
   * @param maxValue maximum allowed value
   */
  private void initNumberSpinner(int minValue, int maxValue) {
    numberSpinner.setModel(new SpinnerNumberModel(
        minValue, minValue, maxValue, 1));
  }

  /**
   * Creates the panel containing the dialog's action button.
   *
   * @return JPanel containing the button
   */
  private JPanel createButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

    JButton button = new JButton(buttonText);
    button.setPreferredSize(new Dimension(80, 30));
    button.addActionListener(e -> {
      DialogResult result;
      if (mode == DialogMode.MESSAGE_WITH_NUMBER) {
        result = new DialogResult((Integer) numberSpinner.getValue());
      } else {
        result = new DialogResult();
      }

      // Complete the future with the result
      resultFuture.complete(result);

      // Execute callback if provided
      if (onClose != null) {
        SwingUtilities.invokeLater(onClose);
      }

      dispose();
    });

    panel.add(button);
    return panel;
  }

  /**
   * Configures the dialog size and position based on screen dimensions.
   * Ensures dialog fits within screen bounds and is properly centered.
   */
  private void setupDialogSize() {
    pack();

    setMinimumSize(new Dimension(400, mode == DialogMode.MESSAGE_WITH_NUMBER ? 250 : 200));
    setMaximumSize(new Dimension(800, 600));

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int maxWidth = (int) (screenSize.width * 0.8);
    int maxHeight = (int) (screenSize.height * 0.8);

    if (getWidth() > maxWidth || getHeight() > maxHeight) {
      setSize(Math.min(getWidth(), maxWidth), Math.min(getHeight(), maxHeight));
    }

    setLocationRelativeTo(getParent());
  }

  /**
   * Represents the different modes of operation for the dialog.
   */
  public enum DialogMode {
    MESSAGE_ONLY,
    MESSAGE_WITH_NUMBER
  }

  /**
   * Result class representing the outcome of dialog interaction.
   * Contains methods to access and validate the input value.
   */
  public static class DialogResult {
    private final Integer number;

    /**
     * Creates a new DialogResult with no number value.
     */
    public DialogResult() {
      this.number = null;
    }

    /**
     * Creates a new DialogResult with the specified number value.
     *
     * @param diaNumber the number input value
     */
    private DialogResult(int diaNumber) {
      this.number = diaNumber;
    }

    /**
     * Checks if this result contains a number value.
     *
     * @return true if a number value is present, false otherwise
     */
    public boolean hasNumber() {
      return number != null;
    }

    /**
     * Gets the number input from the dialog.
     *
     * @return the number input
     * @throws IllegalStateException if no number is available
     */
    public int getNumber() {
      if (!hasNumber()) {
        throw new IllegalStateException("No number available in result");
      }
      return number != null ? number : 0;
    }
  }
}