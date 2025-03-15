package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import model.Product;

public interface ProductService extends Remote {
    List<Product> getProducts() throws RemoteException;
    Product findProductByName(String name) throws RemoteException;
    Product getProduct(int id) throws RemoteException;
    void addProduct(Product product) throws RemoteException;
    void updateProduct(Product product) throws RemoteException;
    void deleteProduct(int id) throws RemoteException;
}
