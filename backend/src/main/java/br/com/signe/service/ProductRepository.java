package br.com.signe.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ProductRepository {
    private final EntityManager entityManager;

    public ProductRepository(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    public void save(Product product) {
        entityManager.getTransaction().begin();
        entityManager.persist(product);
        entityManager.getTransaction().commit();
    }

    public void update(Product product) {
        entityManager.getTransaction().begin();
        entityManager.merge(product);
        entityManager.getTransaction().commit();
    }

    public Product searchProductByName(String name) {
        TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p WHERE LOWER(p.name) = LOWER(:name)", Product.class);
        query.setParameter("name", name);
        List<Product> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Product> showAllProducts(){
        return entityManager.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }

    public List<Product> lowInventoryProductsList(int limit) {
        TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p WHERE p.inventoryQuantity <= :limit", Product.class);
        query.setParameter("limit", limit);
        return query.getResultList();
    }
}
