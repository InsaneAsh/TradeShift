//add this file in where all other tables are there like user table



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
}
