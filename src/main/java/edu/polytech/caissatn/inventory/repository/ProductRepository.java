package edu.polytech.caissatn.inventory.repository;



import edu.polytech.caissatn.inventory.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    // Filtrer par nom de produit, id de catégorie, et id de marque
    Page<Product> findByNameContainingAndCategories_IdEqualsAndBrand_IdEquals(String name, UUID categoryId, UUID brandId, Pageable pageable);

    // Filtrer par nom de produit et id de catégorie
    Page<Product> findByNameContainingAndCategories_IdEquals(String name, UUID categoryId, Pageable pageable);

    // Filtrer par nom de produit et id de marque
    Page<Product> findByNameContainingAndBrand_IdEquals(String name, UUID brandId, Pageable pageable);

    // Rechercher par nom de produit ou code-barres
    // @Query("SELECT p FROM Product p LEFT JOIN p.barcodes b WHERE p.name LIKE %:searchQuery% OR b.code = :searchQuery")
    @Query("SELECT p FROM Product p LEFT JOIN p.barcodes b WHERE p.name LIKE %:searchQuery% OR b.code LIKE %:searchQuery%")
    Page<Product> findByNameOrBarcode(@Param("searchQuery") String searchQuery, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.id IN :ids")
    Page<Product> findByIds(@Param("ids") List<UUID> ids, Pageable pageable);
    @Query("SELECT p FROM Product p JOIN p.barcodes b WHERE b.code = :barcode")
    Optional<Product> findByBarcode(@Param("barcode") String barcode);
}
