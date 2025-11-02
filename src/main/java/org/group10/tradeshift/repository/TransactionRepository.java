//this is repository file for transaction table


package org.group10.tradeshift.repository;

import org.group10.tradeshift.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// repository/TransactionRepository.java (expanded)
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByTimestampDesc(Long userId);

    // Net quantity per symbol (for holdings)
    @Query("SELECT t.symbol, SUM(CASE WHEN t.type = 'BUY' THEN t.quantity ELSE -t.quantity END) as netQty " +
            "FROM Transaction t WHERE t.user.id = :userId GROUP BY t.symbol HAVING netQty > 0")
    List<Object[]> findNetHoldingsByUserId(@Param("userId") Long userId);

    // Total cost basis per symbol (sum buys only)
    @Query("SELECT t.symbol, SUM(t.totalCost) as totalCost " +
            "FROM Transaction t WHERE t.user.id = :userId AND t.type = 'BUY' GROUP BY t.symbol")
    Map<String, Double> findCostBasisByUserId(@Param("userId") Long userId);
}