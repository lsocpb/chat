package rmi;

import model.Product;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductServiceImpl extends UnicastRemoteObject implements ProductService {
    private List<Product> products;

    public ProductServiceImpl() throws RemoteException {
        products = new ArrayList<>();
        products.add(new Product("Product 1", 100, 1));
        products.add(new Product("Product 2", 200, 2));
        products.add(new Product("Product 3", 300, 3));
    }

    @Override
    public List<Product> getProducts() throws RemoteException {
        return products;
    }

    @Override
    public Product getProduct(int id) throws RemoteException {
        return products.stream().filter(product -> product.getId() == id).findFirst().orElse(null);
    }

    @Override
    public void addProduct(Product product) throws RemoteException {
        products.add(product);
    }

    @Override
    public void updateProduct(Product product) throws RemoteException {
        products = products.stream().map(p -> p.getId() == product.getId() ? product : p).collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(int id) throws RemoteException {
        products.removeIf(product -> product.getId() == id);
    }

    @Override
    public Product findProductByName(String name) throws RemoteException {
        return products.stream().filter(product -> product.getName().equals(name)).findFirst().orElse(null);
    }
}
