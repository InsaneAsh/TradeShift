//package org.group10.tradeshift.model;
//add this file also in where user, portfolio,transaction tables are there
public class OrderRequest {
    private Long userId;
    private String symbol;
    private Double quantity;

    // Default constructor
    public OrderRequest() {}

    // Getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
}