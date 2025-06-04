package edu.polytech.caissatn.sale.repository;

import edu.polytech.caissatn.sale.entity.Register;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Repository
public interface RegisterRepository extends JpaRepository<Register, UUID> {

    List<Register> findByClosingCashAndEndDate(BigDecimal v, Instant instant);

    Page<Register> findAll(Pageable pageable);
}
