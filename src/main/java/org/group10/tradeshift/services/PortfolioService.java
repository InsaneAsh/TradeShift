//package org.group10.tradeshift.service;
//add the package of the reprensented class
//here i assumed you added portfolio n transaction in m1 package

import org.group10.tradeshift.m1.Portfolio;
import org.group10.tradeshift.m1.Transaction;
import org.group10.tradeshift.m1.User;
import org.group10.tradeshift.repository.TransactionRepository;
import org.group10.tradeshift.repository.UserRepository;
import org.group10.tradeshift.websocket.FinnhubWebSocketClient;  // For prices
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioService {
    @Autowired private TransactionRepository transactionRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private FinnhubWebSocketClient finnhubClient;  // Assume we have a price cache/map

    // Cache live prices: symbol -> price (updated via WS)
    private final Map<String, Double> livePrices = new HashMap<>();

    // Update price from WS (call this in Finnhub handler)
    public void updateLivePrice(String symbol, Double price) {
        livePrices.put(symbol, price);
    }

    @Transactional(readOnly = true)
    public Portfolio getPortfolio(Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        List<Object[]> netHoldings = transactionRepo.findNetHoldingsByUserId(userId);
        Map<String, Double> costBasis = transactionRepo.findCostBasisByUserId(userId);

        Portfolio portfolio = new Portfolio();
        portfolio.setUser(user);
        double totalValue = 0.0;
        double unrealizedPl = 0.0;
        Map<String, Double> allocations = new HashMap<>();  // For diversification

        for (Object[] holding : netHoldings) {
            String symbol = (String) holding[0];
            Double netQty = (Double) holding[1];
            Double currentPrice = livePrices.getOrDefault(symbol, fetchCurrentPrice(symbol));  // Fallback to API
            Double currentValue = netQty * currentPrice;
            Double basis = costBasis.getOrDefault(symbol, 0.0);
            Double pl = currentValue - basis;  // Unrealized

            totalValue += currentValue;
            unrealizedPl += pl;

            double allocationPct = (currentValue / (totalValue > 0 ? totalValue : 1)) * 100;
            allocations.put(symbol, allocationPct);
        }

        // Realized P/L: Sum (sell totalCost - buy cost for matched sells) – simplify as total sells proceeds - total buys
        double totalBuys = costBasis.values().stream().mapToDouble(Double::doubleValue).sum();
        // For realized, you'd match FIFO/LIFO; here, approx as user.cashBalance + totalValue - initialBalance
        double realizedPl = user.getCashBalance() - 10000.0 + totalValue - totalBuys;  // Adjust initial

        portfolio.setTotalValue(totalValue);
        portfolio.setUnrealizedPl(unrealizedPl);
        portfolio.setRealizedPl(realizedPl);  // Add field to Portfolio

        // Diversification: e.g., # assets, top 3 allocations
        portfolio.setNumAssets(netHoldings.size());
        portfolio.setTopAllocations(allocations.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList()));

        // Update cache in DB if needed
        userRepo.save(user);  // If updating Portfolio entity
        return portfolio;
    }

    private Double fetchCurrentPrice(String symbol) {
        // Call Finnhub REST: /quote?symbol=AAPL&token=...
        // Use WebClient as in MarketDataService
        finnhubClient.subscribe(symbol);  // Ensure subscribed
        return livePrices.getOrDefault(symbol, 0.0);  // Placeholder
    }

    // On price update (call from WS handler)
    public void recalculateOnPriceChange(String symbol, Double newPrice) {
        updateLivePrice(symbol, newPrice);
        // For subscribed users: Broadcast update (user-specific topics?)
        // template.convertAndSend("/user/" + userId + "/portfolio/update", getPortfolio(userId));
    }

    // Diversification metric: e.g., Herfindahl Index (sum sq %alloc / 10000) for concentration (lower = more diversified)
    public double calculateDiversificationScore(Map<String, Double> allocations) {
        return allocations.values().stream()
                .map(pct -> Math.pow(pct / 100, 2))
                .mapToDouble(Double::doubleValue)
                .sum();
        // Score 0-1: Closer to 0 = better diversification
    }
}
