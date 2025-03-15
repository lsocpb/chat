package clientChat;

import modelChat.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ChatClient extends JFrame {
    private JTextField messageField;
    private JTextArea chatArea;
    private JButton sendButton;
    private JScrollPane scrollPane;
    private JTextField usernameField;
    private JButton connectButton;
    private JTextField serverField;
    private JPanel connectionPanel;
    private JPanel chatPanel;

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String username;
    private boolean connected = false;

    public ChatClient() {
        setTitle("Chat Client");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel połączenia
        connectionPanel = new JPanel(new FlowLayout());
        connectionPanel.add(new JLabel("Serwer:"));
        serverField = new JTextField("localhost", 10);
        connectionPanel.add(serverField);
        connectionPanel.add(new JLabel("Nazwa użytkownika:"));
        usernameField = new JTextField(10);
        connectionPanel.add(usernameField);
        connectButton = new JButton("Połącz");
        connectionPanel.add(connectButton);

        // Panel czatu
        chatPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        scrollPane = new JScrollPane(chatArea);
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Wyślij");
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        add(connectionPanel, BorderLayout.NORTH);
        add(chatPanel, BorderLayout.CENTER);

        // Początkowo panel czatu jest nieaktywny
        messageField.setEnabled(false);
        sendButton.setEnabled(false);

        // Listener dla przycisku połączenia
        connectButton.addActionListener(this::connect);

        // Listener dla przycisku wysyłania
        sendButton.addActionListener(this::sendMessage);

        // Listener dla pola tekstowego (Enter)
        messageField.addActionListener(this::sendMessage);

        // Listener dla zamknięcia okna
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
            }
        });

        setVisible(true);
    }

    private void connect(ActionEvent e) {
        if (!connected) {
            username = usernameField.getText().trim();
            String server = serverField.getText().trim();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Podaj nazwę użytkownika");
                return;
            }

            try {
                socket = new Socket(server, 12345);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());

                // Wysłanie pierwszej wiadomości z nazwą użytkownika
                Message message = new Message(username, "dołączył do czatu");
                outputStream.writeObject(message);

                // Uruchomienie wątku do odbierania komunikatów
                new Thread(this::receiveMessages).start();

                // Aktualizacja UI
                connected = true;
                connectButton.setText("Rozłącz");
                messageField.setEnabled(true);
                sendButton.setEnabled(true);
                usernameField.setEnabled(false);
                serverField.setEnabled(false);

                chatArea.append("Połączono z serwerem " + server + "\n");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Błąd połączenia: " + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            disconnect();
        }
    }

    private void disconnect() {
        if (connected) {
            try {
                // Wysłanie wiadomości o opuszczeniu czatu
                Message message = new Message(username, "opuścił czat");
                outputStream.writeObject(message);

                // Zamknięcie połączenia
                socket.close();

                // Aktualizacja UI
                connected = false;
                connectButton.setText("Połącz");
                messageField.setEnabled(false);
                sendButton.setEnabled(false);
                usernameField.setEnabled(true);
                serverField.setEnabled(true);

                chatArea.append("Rozłączono z serwerem\n");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(ActionEvent e) {
        String content = messageField.getText().trim();
        if (!content.isEmpty() && connected) {
            try {
                Message message = new Message(username, content);
                outputStream.writeObject(message);
                messageField.setText("");
                chatArea.append("Ty: " + content + "\n");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Błąd wysyłania: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void receiveMessages() {
        try {
            while (connected) {
                Message message = (Message) inputStream.readObject();
                SwingUtilities.invokeLater(() -> {
                    chatArea.append(message.toString() + "\n");
                    // Automatyczne przewijanie na dół
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());
                });
            }
        } catch (IOException | ClassNotFoundException e) {
            if (connected) {
                SwingUtilities.invokeLater(() -> {
                    chatArea.append("Utracono połączenie z serwerem\n");
                    connected = false;
                    connectButton.setText("Połącz");
                    messageField.setEnabled(false);
                    sendButton.setEnabled(false);
                    usernameField.setEnabled(true);
                    serverField.setEnabled(true);
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClient::new);
    }
}