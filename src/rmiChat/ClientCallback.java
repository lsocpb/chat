package rmiChat;

import modelChat.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {
    void receiveMessage(Message message) throws RemoteException;
    void updateUserList(String[] users) throws RemoteException;
}