package view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A reusable message dialog component that displays a title and message with an OK button.
 */
public class MessageDialog extends JDialog {

  private String buttonName = "";

  /**
   * Creates a new message dialog.
   *
   * @param parent     The parent frame for this dialog
   * @param title      The title of the dialog
   * @param message    The message to display
   * @param buttonName The name of the button
   */
  public MessageDialog(JFrame parent, String title, String message, String buttonName) {
    super(parent, title, true);
    this.buttonName = buttonName;

    // Configure dialog
    setLayout(new BorderLayout(10, 10));
    setSize(400, 200);
    setLocationRelativeTo(parent);
    setResizable(false);

    // Create message panel
    JPanel messagePanel = new JPanel();
    messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    messagePanel.setBackground(Color.WHITE);

    // Add message label
    JLabel messageLabel = new JLabel(message);
    messageLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
    messagePanel.add(messageLabel);

    // Create button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

    // Create and configure OK button
    JButton okButton = new JButton(buttonName);
    okButton.setPreferredSize(new Dimension(80, 30));
    okButton.addActionListener(e -> dispose());
    buttonPanel.add(okButton);

    // Add panels to dialog
    add(messagePanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Static helper method to show a message dialog.
   *
   * @param parent  The parent frame
   * @param title   The dialog title
   * @param message The message to display
   */
  public static void showMessage(JFrame parent, String title, String message, String buttonName) {
    MessageDialog dialog = new MessageDialog(parent, title, message, buttonName);
    dialog.setVisible(true);
  }
}