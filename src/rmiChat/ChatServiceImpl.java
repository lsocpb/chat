package rmiChat;

import modelChat.Message;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ChatServiceImpl extends UnicastRemoteObject implements ChatService {

    private Map<String, ClientCallback> clients = new HashMap<>();

    public ChatServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public synchronized void registerClient(String username, ClientCallback callback) throws RemoteException {
        String originalUsername = username;
        int counter = 1;

        while (clients.containsKey(username)) {
            username = originalUsername + "_" + counter;
            counter++;
        }

        clients.put(username, callback);
        System.out.println("Zarejestrowano klienta: " + username);

        Message serverMessage = new Message("Server", "Użytkownik " + username + " dołączył do czatu");
        broadcastMessage(serverMessage);

        updateAllUserLists();
    }

    @Override
    public synchronized void unregisterClient(String username) throws RemoteException {
        clients.remove(username);
        System.out.println("Wyrejestrowano klienta: " + username);

        Message serverMessage = new Message("Server", "Użytkownik " + username + " opuścił czat");
        broadcastMessage(serverMessage);

        updateAllUserLists();
    }

    @Override
    public void broadcastMessage(Message message) throws RemoteException {
        System.out.println("Wysyłanie wiadomości od " + message.getSender() + ": " + message.getContent());

        for (ClientCallback client : clients.values()) {
            try {
                client.receiveMessage(message);
            } catch (RemoteException e) {
                System.out.println("Błąd przy wysyłaniu wiadomości do klienta: " + e.getMessage());
            }
        }
    }

    @Override
    public String[] getActiveUsers() throws RemoteException {
        return clients.keySet().toArray(new String[0]);
    }

    private void updateAllUserLists() throws RemoteException {
        String[] users = getActiveUsers();

        for (ClientCallback client : clients.values()) {
            try {
                client.updateUserList(users);
            } catch (RemoteException e) {
                System.out.println("Błąd przy aktualizacji listy użytkowników: " + e.getMessage());
            }
        }
    }
}