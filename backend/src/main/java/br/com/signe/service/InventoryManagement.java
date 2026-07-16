package br.com.signe.service;

import java.util.ArrayList;
import java.util.List;

public class InventoryManagement {
    private List<Product> products = new ArrayList<>();

    public Product searchProductByName(String name) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public boolean productEntry(String productName, int quantity) {
        Product randomProduct = searchProductByName(productName);

        if (randomProduct == null) {
            return false;
        } else {
            randomProduct.setInventoryQuantity(randomProduct.getInventoryQuantity() + quantity);
            return true;
        }
    }

    public boolean productExit(String productName, int quantity) {
        Product randomProduct = searchProductByName(productName);

        if (randomProduct == null) {
            return false;
        }
        if (randomProduct.getInventoryQuantity() < quantity) {
            return false;
        }

        randomProduct.setInventoryQuantity(randomProduct.getInventoryQuantity() - quantity);
        return true;
    }


    public boolean changeProductName(String currentlyName, String newName) {
        Product randomProduct = searchProductByName(currentlyName);

        if (randomProduct == null) {
            return false;
        } else {
            randomProduct.setName(newName);
            return true;
        }
    }


    public boolean checkAvailability(String productName, int quantity) {
        Product randomProduct = searchProductByName(productName);

        if (randomProduct == null) {
            return false;
        } else {
            return randomProduct.getInventoryQuantity() >= quantity;
        }
    }

    public List<Product> showAllProducts(){
        return products;
    }

    public List<Product> lowInventoryProductsList(int limit){
        List<Product> list = new ArrayList<>();

        for (Product p : products) {
            if (p.getInventoryQuantity() <= limit) {
                list.add(p);
            }
        }

        return list;
    }
}
