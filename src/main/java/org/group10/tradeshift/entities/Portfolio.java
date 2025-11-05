package org.group10.tradeshift.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
public class Portfolio {
    @Id @GeneratedValue
    private Long id;

    @OneToOne
    private User user;

    private Double totalValue = 0.0;  // Cached; update via service
    private Double unrealizedPl = 0.0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getUnrealizedPl() {
        return unrealizedPl;
    }

    public void setUnrealizedPl(Double unrealizedPl) {
        this.unrealizedPl = unrealizedPl;
    }

    public Double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }
    // Add these fields
    private double realizedPl = 0.0;
    private int numAssets = 0;



    public double getRealizedPl() { return realizedPl; }
    public void setRealizedPl(double realizedPl) { this.realizedPl = realizedPl; }

    public int getNumAssets() { return numAssets; }
    public void setNumAssets(int numAssets) { this.numAssets = numAssets; }


}
