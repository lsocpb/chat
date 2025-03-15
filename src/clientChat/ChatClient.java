package clientChat;

import modelChat.Message;
import rmiChat.ChatService;
import rmiChat.ClientCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ChatClient extends JFrame implements ClientCallback {
    private JTextField messageField;
    private JTextArea chatArea;
    private JButton sendButton;
    private JScrollPane scrollPane;
    private JTextField usernameField;
    private JButton connectButton;
    private JTextField serverField;
    private JPanel connectionPanel;
    private JPanel chatPanel;
    private JList<String> userList;

    private ChatService chatService;
    private String username;
    private boolean connected = false;

    public ChatClient() {
        setTitle("RMI Chat Client");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        connectionPanel = new JPanel(new FlowLayout());
        connectionPanel.add(new JLabel("Serwer:"));
        serverField = new JTextField("localhost", 10);
        connectionPanel.add(serverField);
        connectionPanel.add(new JLabel("Nazwa użytkownika:"));
        usernameField = new JTextField(10);
        connectionPanel.add(usernameField);
        connectButton = new JButton("Połącz");
        connectionPanel.add(connectButton);

        JPanel mainPanel = new JPanel(new BorderLayout());

        chatPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        scrollPane = new JScrollPane(chatArea);
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        userList = new JList<>();
        userList.setPreferredSize(new Dimension(150, 0));
        JScrollPane userScrollPane = new JScrollPane(userList);
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.add(new JLabel("Użytkownicy online:"), BorderLayout.NORTH);
        userPanel.add(userScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Wyślij");
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.add(chatPanel, BorderLayout.CENTER);
        mainPanel.add(userPanel, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(connectionPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        messageField.setEnabled(false);
        sendButton.setEnabled(false);
        userList.setEnabled(false);

        connectButton.addActionListener(this::connect);

        sendButton.addActionListener(this::sendMessage);

        messageField.addActionListener(this::sendMessage);

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
                Registry registry = LocateRegistry.getRegistry(server, 1099);

                chatService = (ChatService) registry.lookup("ChatService");

                ClientCallback callback = (ClientCallback) UnicastRemoteObject.exportObject(this, 0);
                chatService.registerClient(username, callback);

                connected = true;
                connectButton.setText("Rozłącz");
                messageField.setEnabled(true);
                sendButton.setEnabled(true);
                usernameField.setEnabled(false);
                serverField.setEnabled(false);
                userList.setEnabled(true);

                chatArea.append("Połączono z serwerem " + server + "\n");

            } catch (Exception ex) {
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
                chatService.unregisterClient(username);
                UnicastRemoteObject.unexportObject(this, true);

                // Aktualizacja UI
                connected = false;
                connectButton.setText("Połącz");
                messageField.setEnabled(false);
                sendButton.setEnabled(false);
                usernameField.setEnabled(true);
                serverField.setEnabled(true);
                userList.setEnabled(false);

                chatArea.append("Rozłączono z serwerem\n");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(ActionEvent e) {
        String content = messageField.getText().trim();
        if (!content.isEmpty() && connected) {
            try {
                Message message = new Message(username, content);
                chatService.broadcastMessage(message);
                messageField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Błąd wysyłania: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void receiveMessage(Message message) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message.toString() + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    @Override
    public void updateUserList(String[] users) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            userList.setListData(users);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClient::new);
    }
}