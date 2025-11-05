package org.group10.tradeshift.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private Double quantity;
    private Double price;
    private String type; // "BUY" or "SELL"

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // CONSTRUCTOR FOR executeBuyOrder()
    public Transaction(String symbol, Double quantity, Double price, String type, User user) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.user = user;
        this.timestamp = new Date(); // auto-set current time
    }
}