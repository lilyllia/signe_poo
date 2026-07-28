package br.com.signe.service.service;

import br.com.signe.service.domain.Product;
import br.com.signe.service.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // create
    @Transactional
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    //read
    public List<Product> getAllActiveProducts() {
        return productRepository.findAll().stream()
                .filter(Product::isActive)
                .toList();
    }

    //update
    @Transactional
    public Product addStock(UUID id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        product.addStock(quantity);
        return productRepository.save(product);
    }

    // métodos essenciais pro usuário
    @Transactional
    public Product updateProductDetails(UUID id, String name, String unitMeasure, String brand) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        product.updateDetails(name, unitMeasure, brand);
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProductPrice(UUID id, double newPrice) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        product.updatePrice(newPrice);
        return productRepository.save(product);
    }

    // delete (mais ou menos)
    @Transactional
    public Product removeStock(UUID productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        product.removeStock(quantity);

        return productRepository.save(product);
    }

    // soft delete
    @Transactional
    public void deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        product.deactivate();
        productRepository.save(product);
    }
}