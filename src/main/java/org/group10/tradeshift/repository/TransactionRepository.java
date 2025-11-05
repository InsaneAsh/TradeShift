package org.group10.tradeshift.repository;

import org.group10.tradeshift.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByTimestampDesc(Long userId);

    // Net quantity per symbol (for holdings)
    @Query("SELECT t.symbol, SUM(CASE WHEN t.type = 'BUY' THEN t.quantity ELSE -t.quantity END) " +
            "FROM Transaction t WHERE t.user.id = :userId " +
            "GROUP BY t.symbol " +
            "HAVING SUM(CASE WHEN t.type = 'BUY' THEN t.quantity ELSE -t.quantity END) > 0")
    List<Object[]> findNetHoldingsByUserId(@Param("userId") Long userId);

    // Total cost basis per symbol (sum buys only)
    @Query("SELECT t.symbol, SUM(t.price * t.quantity) " +
            "FROM Transaction t WHERE t.user.id = :userId AND t.type = 'BUY' " +
            "GROUP BY t.symbol")
    List<Object[]> findCostBasisByUserId(@Param("userId") Long userId);
}