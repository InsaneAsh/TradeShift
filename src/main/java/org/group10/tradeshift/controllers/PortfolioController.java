package org.group10.tradeshift.controllers;

import lombok.RequiredArgsConstructor;
import org.group10.tradeshift.entities.Portfolio;
import org.group10.tradeshift.entities.Transaction;
import org.group10.tradeshift.services.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor  // Lombok
public class PortfolioController {

    private final PortfolioService portfolioService;  // INJECT

    @PostMapping("/buy")
    public ResponseEntity<Transaction> buyAsset(@RequestBody OrderRequest order, Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        Double price = 150.0;  // placeholder
        Transaction tx = portfolioService.executeBuyOrder(userId, order.getSymbol(), (double)order.getQuantity(), price);
        return ResponseEntity.ok(tx);
    }

    private Long getUserIdFromAuth(Authentication auth) {
        return 1L; // TODO: extract from JWT
    }

    @GetMapping
    public ResponseEntity<Portfolio> getCurrentPortfolio(Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        Portfolio portfolio = portfolioService.getPortfolio(userId);
        return ResponseEntity.ok(portfolio);
    }
}
