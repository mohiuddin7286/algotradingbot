package com.skilliou.backend.repository;

import com.skilliou.backend.model.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    
    // Get history for one user
    List<Ledger> findByUserId(Long userId);

    // Calculate Total Balance (Sum of all credit changes)
    // If null (no history), return 0.0
    @Query("SELECT COALESCE(SUM(l.creditChange), 0.0) FROM Ledger l WHERE l.user.id = :userId")
    Double getBalance(Long userId);
}