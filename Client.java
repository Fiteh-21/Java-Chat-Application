import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame {

    private JTextArea chatArea = new JTextArea();
    private JTextField messageField = new JTextField();
    private JButton sendButton = new JButton("Send");

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private String name;

    public Client() {
        super("Chat Client");

        // Setup GUI
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(panel, BorderLayout.SOUTH);

        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        // Prompt for user name
        name = JOptionPane.showInputDialog(this, "Enter your name:", "Name", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.trim().isEmpty()) {
            name = "Anonymous";
        }

        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Thread to listen for messages from server
            new Thread(() -> {
                try {
                    // Server asks for name
                    String serverMsg = in.readLine(); // "Enter your name:"
                    out.println(name);

                    // Welcome message
                    serverMsg = in.readLine();
                    appendMessage(serverMsg);

                    // Read messages from server continuously
                    String msgFromServer;
                    while ((msgFromServer = in.readLine()) != null) {
                        appendMessage(msgFromServer);
                    }
                } catch (IOException e) {
                    appendMessage("Disconnected from server.");
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Cannot connect to server: " + e.getMessage());
            System.exit(0);
        }
    }

    private void sendMessage() {
        String msg = messageField.getText().trim();
        if (!msg.isEmpty()) {
            if (msg.equalsIgnoreCase("/quit")) {
                out.println(msg);
                appendMessage("You left the chat.");
                messageField.setEditable(false);
                sendButton.setEnabled(false);
                try {
                    socket.close();
                } catch (IOException e) {
                    // ignore
                }
                return;
            }
            out.println(msg);
            appendMessage("Me: " + msg);
            messageField.setText("");
        }
    }

    private void appendMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(msg + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Client client = new Client();
            client.setVisible(true);
        });
    }
}