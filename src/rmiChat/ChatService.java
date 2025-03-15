package rmiChat;

import modelChat.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatService extends Remote {
    void registerClient(String username, ClientCallback callback) throws RemoteException;
    void unregisterClient(String username) throws RemoteException;
    void broadcastMessage(Message message) throws RemoteException;
    String[] getActiveUsers() throws RemoteException;
}