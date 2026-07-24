package br.com.signe.service;

import java.util.List;

public class InventoryManagement {
    private final ProductRepository productRepository;

    public InventoryManagement(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public void addProduct(Product product) {
        productRepository.save(product);
    }

    public boolean productEntry(String productName, int quantity) {
        Product randomProduct = productRepository.searchProductByName(productName);

        if (randomProduct == null) {
            return false;
        } else {
            randomProduct.setInventoryQuantity(randomProduct.getInventoryQuantity() + quantity);
            productRepository.update(randomProduct);
            return true;
        }
    }

    public boolean productExit(String productName, int quantity) {
        Product randomProduct = productRepository.searchProductByName(productName);

        if (randomProduct == null) {
            return false;
        }
        if (randomProduct.getInventoryQuantity() < quantity) {
            return false;
        }

        randomProduct.setInventoryQuantity(randomProduct.getInventoryQuantity() - quantity);
        productRepository.update(randomProduct);
        return true;
    }

    public boolean changeProductName(String currentlyName, String newName) {
        Product randomProduct = productRepository.searchProductByName(currentlyName);

        if (randomProduct == null) {
            return false;
        } else {
            randomProduct.setName(newName);
            productRepository.update(randomProduct);
            return true;
        }
    }

    public boolean checkAvailability(String productName, int quantity) {
        Product randomProduct = productRepository.searchProductByName(productName);

        if (randomProduct == null) {
            return false;
        } else {
            return randomProduct.getInventoryQuantity() >= quantity;
        }
    }

    public List<Product> showAllProducts(){
        return productRepository.showAllProducts();
    }

    public List<Product> lowInventoryProductsList(int limit) {
        return productRepository.lowInventoryProductsList(limit);
    }
}
