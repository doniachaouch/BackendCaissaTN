package edu.polytech.caissatn.sale.repository;


import edu.polytech.caissatn.sale.entity.Register;
import edu.polytech.caissatn.sale.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findAllByRegister(Register register);
    Page<Transaction> findAllByRegister(Register register, Pageable pageable);
}
