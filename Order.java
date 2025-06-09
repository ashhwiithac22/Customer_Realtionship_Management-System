public class Order {
    private int id;
    private String customerId;
    private String salesmanId;

    public Order(int id, String customerId, String salesmanId) {
        this.id = id;
        this.customerId = customerId;
        this.salesmanId = salesmanId;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getSalesmanId() { return salesmanId; }

    @Override
    public String toString() {
        return "Order #" + id;
    }
}