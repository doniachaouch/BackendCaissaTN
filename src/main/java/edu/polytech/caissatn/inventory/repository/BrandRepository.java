package edu.polytech.caissatn.inventory.repository;


import edu.polytech.caissatn.inventory.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {

}
