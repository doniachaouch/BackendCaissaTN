package edu.polytech.caissatn.inventory.repository;


import edu.polytech.caissatn.inventory.entity.Barcode;
import edu.polytech.caissatn.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BarcodeRepository extends JpaRepository<Barcode, UUID> {

    List<Barcode> findAllByProductId(UUID productId);

    Barcode findByCode(String code);

    List<Barcode> findByProduct(Product savedProduct);
    List<Barcode> findByCodeContaining(String code);
}
