package br.com.signe.service.controller;

import br.com.signe.service.domain.Product;
import br.com.signe.service.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllActiveProducts());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest request) {
        Product newProduct = new Product(
                request.name(),
                request.price(),
                request.unitMeasure(),
                request.brand(),
                request.inventoryQuantity()
        );
        Product savedProduct = productService.addProduct(newProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @PutMapping("/{id}/details")
    public ResponseEntity<Product> updateProductDetails(
            @PathVariable UUID id,
            @RequestBody UpdateProductDetailsRequest request) {
        Product updatedProduct = productService.updateProductDetails(id, request.name(), request.unitMeasure(), request.brand());
        return ResponseEntity.ok(updatedProduct);
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<Product> updateProductPrice(
            @PathVariable UUID id,
            @RequestBody UpdatePriceRequest request) {
        Product updatedProduct = productService.updateProductPrice(id, request.newPrice());
        return ResponseEntity.ok(updatedProduct);
    }

    @PutMapping("/{id}/stock/add")
    public ResponseEntity<Product> addStock(
            @PathVariable UUID id,
            @RequestBody StockRequest request) {
        Product updatedProduct = productService.addStock(id, request.quantity());
        return ResponseEntity.ok(updatedProduct);
    }

    @PutMapping("/{id}/stock/remove")
    public ResponseEntity<Product> removeStock(
            @PathVariable UUID id,
            @RequestBody StockRequest request) {
        Product updatedProduct = productService.removeStock(id, request.quantity());
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequests(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    record CreateProductRequest(String name, double price, String unitMeasure, String brand, int inventoryQuantity) {}
    record UpdateProductDetailsRequest(String name, String unitMeasure, String brand) {}
    record UpdatePriceRequest(double newPrice) {}
    record StockRequest(int quantity) {}
}


