package org.group10.tradeshift.controller;

import org.group10.tradeshift.model.Transaction;
import org.group10.tradeshift.service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// OrderRequest DTO: class with userId, symbol, quantity
class OrderRequest {
    private Long userId;
    private String symbol;
    private Double quantity;
    // getters/setters
}

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
    @Autowired private PortfolioService portfolioService;

    @PostMapping("/buy")
    public ResponseEntity<Transaction> buyAsset(@RequestBody OrderRequest order, Authentication auth) {
        // Get userId from JWT/auth
        Long userId = getUserIdFromAuth(auth);
        // Fetch current price (simulate or from market service)
        Double price = 150.0;  // Placeholder
        Transaction tx = tradingService.executeBuyOrder(userId, order.getSymbol(), order.getQuantity(), price);
        return ResponseEntity.ok(tx);
    }

    private Long getUserIdFromAuth(Authentication auth) {
        // Impl: Extract from JWT
        return 1L;  // Placeholder
    }

    @GetMapping
    public ResponseEntity<Portfolio> getCurrentPortfolio(Authentication auth) {
        Long userId = getUserIdFromAuth(auth);  // From JWT
        Portfolio portfolio = portfolioService.getPortfolio(userId);
        return ResponseEntity.ok(portfolio);
    }
}
