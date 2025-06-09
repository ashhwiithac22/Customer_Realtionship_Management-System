public class Customer {
    private int id;
    private String name;
    private String transactionId;
    private String orderId;
    private String salesmanId;

    public Customer(int id, String name, String transactionId, String orderId, String salesmanId) {
        this.id = id;
        this.name = name;
        this.transactionId = transactionId;
        this.orderId = orderId;
        this.salesmanId = salesmanId;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getTransactionId() { return transactionId; }
    public String getOrderId() { return orderId; }
    public String getSalesmanId() { return salesmanId; }

    @Override
    public String toString() {
        return name + " (ID: " + id + ")";
    }
}