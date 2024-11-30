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
 * An enhanced message dialog that can display text messages and optionally collect numeric input.
 * Supports both synchronous and asynchronous operation modes.
 */
public class MessageDialog extends JDialog {
  private final String buttonText;
  private final Runnable onClose;
  private final DialogMode mode;
  private final CompletableFuture<DialogResult> resultFuture;
  private JSpinner numberSpinner;

  /**
   * Creates a new message dialog.
   *
   * @param parent        The parent frame
   * @param title         Dialog title
   * @param message       Message to display
   * @param mesButtonText Button text
   * @param mesMode       Dialog mode (message only or with number input)
   * @param mesOnClose    Optional callback when dialog closes (can be null)
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
   * Shows a message dialog with number input and returns a future for the result.
   *
   * @param parent     The parent frame
   * @param title      Dialog title
   * @param message    Message to display
   * @param buttonText Button text
   * @param minValue   Minimum allowed value
   * @param maxValue   Maximum allowed value
   * @return CompletableFuture containing the dialog result
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
   * Shows a message dialog with optional callback.
   *
   * @param parent     The parent frame
   * @param title      Dialog title
   * @param message    Message to display
   * @param buttonText Button text
   * @param onClose    Optional callback when dialog closes
   */
  public static void showMessage(
      JFrame parent, String title, String message, String buttonText, Runnable onClose) {
    SwingUtilities.invokeLater(() -> {
      MessageDialog dialog = new MessageDialog(
          parent, title, message, buttonText, DialogMode.MESSAGE_ONLY, onClose);
      dialog.setVisible(true);
    });
  }

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

  private void initNumberSpinner(int minValue, int maxValue) {
    numberSpinner.setModel(new SpinnerNumberModel(
        minValue, minValue, maxValue, 1));
  }

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
   * Represents the result of the dialog interaction.
   */
  public static class DialogResult {
    private final Integer number;

    public DialogResult() {
      this.number = null;
    }

    private DialogResult(int diaNumber) {
      this.number = diaNumber;
    }

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
      return number;
    }
  }
}