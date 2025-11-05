package org.group10.tradeshift.controllers;

import lombok.Data;

@Data
class OrderRequest {
    private Long userId;
    private String symbol;
    private Double quantity;
    private String type; // "BUY" or "SELL"
    // getters/setters


    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
