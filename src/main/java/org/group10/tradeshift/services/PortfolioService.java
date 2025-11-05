package org.group10.tradeshift.services;
//add the package of the reprensented class
//here i assumed you added portfolio n transaction in m1 package
import org.group10.tradeshift.entities.*;
import org.group10.tradeshift.repository.TransactionRepository;
import org.group10.tradeshift.repository.UserRepository;
import org.group10.tradeshift.websocket.*;  // For prices
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioService {
    @Autowired private TransactionRepository transactionRepo;
    @Autowired private UserRepository userRepo;
    private final FinnhubWebSocketClient finnhubClient;

    public PortfolioService(
            @Lazy
            FinnhubWebSocketClient finnhubClient,
            TransactionRepository transactionRepo

    ) {
        this.finnhubClient = finnhubClient;
        this.transactionRepo = transactionRepo;

    }  // Assume we have a price cache/map

    // Cache live prices: symbol -> price (updated via WS)
    private final Map<String, Double> livePrices = new HashMap<>();

    // Update price from WS (call this in Finnhub handler)
    public void updateLivePrice(String symbol, Double price) {
        livePrices.put(symbol, price);
        System.out.println("Updated price: " + symbol + " = $" + price);
    }

    @Transactional(readOnly = true)
    public Portfolio getPortfolio(Long userId) {
        User user = userRepo.findById(Math.toIntExact(userId)).orElseThrow(() -> new RuntimeException("User not found"));
//        List<Object[]> netHoldings = transactionRepo.findNetHoldingsByUserId(userId);
        Map<String, Double> costBasis = transactionRepo.findCostBasisByUserId(userId).stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],                    // symbol
                        row -> ((Number) row[1]).doubleValue()     // total cost
                ));
        Map<String, Double> netHoldings = transactionRepo.findNetHoldingsByUserId(userId).stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],      // symbol
                        row -> ((Number) row[1]).doubleValue()  // net quantity
                ));

        Portfolio portfolio = new Portfolio();
        portfolio.setUser(user);
        double totalValue = 0.0;
        double unrealizedPl = 0.0;
        Map<String, Double> allocations = new HashMap<>();  // For diversification

        for (Map.Entry<String, Double> entry : netHoldings.entrySet()) {
            String symbol = entry.getKey();
            double netQty = entry.getValue();

            double currentPrice = livePrices.getOrDefault(symbol, fetchCurrentPrice(symbol));
            double currentValue = netQty * currentPrice;
            double basis = costBasis.getOrDefault(symbol, 0.0);
            double pl = currentValue - basis;

            totalValue += currentValue;
            unrealizedPl += pl;

            double allocationPct = totalValue > 0 ? (currentValue / totalValue) * 100 : 0.0;
            allocations.put(symbol, allocationPct);
        }

        double totalBuys = costBasis.values().stream().mapToDouble(Double::doubleValue).sum();
        double realizedPl = user.getCashBalance() - 10000.0 + totalValue - totalBuys;  // Adjust initial

        portfolio.setTotalValue(totalValue);
        portfolio.setUnrealizedPl(unrealizedPl);
        portfolio.setRealizedPl(realizedPl);  // Add field to Portfolio
        portfolio.setNumAssets(netHoldings.size());
        userRepo.save(user);
        return portfolio;
    }

    private Double fetchCurrentPrice(String symbol) {

        finnhubClient.subscribe(symbol);
        return livePrices.getOrDefault(symbol, 0.0);
    }


    public void recalculateOnPriceChange(String symbol, Double newPrice) {
        updateLivePrice(symbol, newPrice);

    }
    public double calculateDiversificationScore(Map<String, Double> allocations) {
        return allocations.values().stream()
                .map(pct -> Math.pow(pct / 100, 2))
                .mapToDouble(Double::doubleValue)
                .sum();
    }
    public void updateStockPrice(String symbol, double price) {
        livePrices.put(symbol, price);

    }
    @Transactional
    public Transaction executeBuyOrder(Long userId, String symbol, Double quantity, Double price) {
        User user = userRepo.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        double totalCost = quantity * price;
        if (user.getCashBalance() < totalCost) {
            throw new RuntimeException("Insufficient funds");
        }

        Transaction tx = new Transaction(symbol, quantity, price, "BUY", user);

        user.setCashBalance(user.getCashBalance() - totalCost);
        userRepo.save(user);

        return transactionRepo.save(tx);
    }
}
