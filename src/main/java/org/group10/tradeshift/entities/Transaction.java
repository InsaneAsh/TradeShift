
//updating this also at the same place where all the tables are present
@Entity
public class Transaction {

    private Double price;
    private String type;
    private String symbol;
    private Double quantity;

    private User user; //User represents the User entity
//
    // For P/L: Add totalCost for the trade
    private Double totalCost;

    // Constructors/getters/setters...
    public Transaction(String symbol, Double quantity, Double price, String type, User user) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.user = user;
        this.totalCost = quantity * price;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    // ... getters/setters
}
