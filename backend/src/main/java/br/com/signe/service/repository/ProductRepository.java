package br.com.signe.service.repository;

import br.com.signe.service.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

//a versão antiga não rodava pois o springboot não aceitava

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    List<Product> findByInventoryQuantityLessThanEqualAndActiveTrue(int limit);
}