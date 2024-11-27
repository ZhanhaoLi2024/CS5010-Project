package view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * A reusable message dialog component that displays a title and message with a customizable button.
 * Supports callback functionality when the dialog is closed.
 */
public class MessageDialog extends JDialog {
  private final Runnable onClose;
  private final String buttonText;

  /**
   * Creates a new message dialog.
   *
   * @param parent     The parent frame for this dialog
   * @param title      The title of the dialog
   * @param message    The message to display
   * @param buttonText The text to display on the button
   * @param onClose    Callback to execute when dialog is closed (can be null)
   */
  public MessageDialog(JFrame parent, String title, String message,
                       String buttonText, Runnable onClose) {
    super(parent, title, true);
    this.onClose = onClose;
    this.buttonText = buttonText;

    initializeDialog(message);
  }

  /**
   * Static helper method to show a message dialog.
   *
   * @param parent     The parent frame
   * @param title      The dialog title
   * @param message    The message to display
   * @param buttonText The text for the button
   * @param onClose    Callback to execute when dialog is closed (can be null)
   */
  public static void showMessage(JFrame parent, String title, String message,
                                 String buttonText, Runnable onClose) {
    SwingUtilities.invokeLater(() -> {
      MessageDialog dialog = new MessageDialog(parent, title, message,
          buttonText, onClose);
      dialog.setVisible(true);
    });
  }

  private void initializeDialog(String message) {

    setLayout(new BorderLayout(10, 10));
    setResizable(true);

    JScrollPane scrollPane = new JScrollPane(createMessagePanel(message));
    scrollPane.setBorder(null);

    JPanel buttonPanel = createButtonPanel();

    add(scrollPane, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);

    pack();

    setMinimumSize(new Dimension(400, 200));
    setMaximumSize(new Dimension(800, 600));

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int maxWidth = (int) (screenSize.width * 0.8);
    int maxHeight = (int) (screenSize.height * 0.8);

    if (getWidth() > maxWidth || getHeight() > maxHeight) {
      setSize(Math.min(getWidth(), maxWidth),
          Math.min(getHeight(), maxHeight));
    }

    setLocationRelativeTo(getParent());
  }

  private JPanel createMessagePanel(String message) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    panel.setBackground(Color.WHITE);

    String[] lines = message.split("\n");
    StringBuilder htmlMessage = new StringBuilder("<html>");
    for (String line : lines) {
      htmlMessage.append("<p style='margin: 0; text-align: left;'>")
          .append(line)
          .append("</p>");
    }
    htmlMessage.append("</html>");
    JLabel messageLabel = new JLabel(htmlMessage.toString());

    messageLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
    messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    messageLabel.setVerticalAlignment(SwingConstants.CENTER);

    panel.add(messageLabel, BorderLayout.CENTER);

    return panel;
  }

  private JPanel createButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

    JButton button = new JButton(buttonText);
    button.setPreferredSize(new Dimension(80, 30));
    button.addActionListener(e -> {
      dispose();
      if (onClose != null) {
        SwingUtilities.invokeLater(onClose);
      }
    });

    panel.add(button);
    return panel;
  }
}