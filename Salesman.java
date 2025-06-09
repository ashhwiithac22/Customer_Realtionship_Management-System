public class Salesman {
    private int id;
    private String name;
    private String orderId;

    public Salesman(int id, String name, String orderId) {
        this.id = id;
        this.name = name;
        this.orderId = orderId;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getOrderId() { return orderId; }

    public void setName(String name) { this.name = name; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    @Override
    public String toString() {
        return name + " (ID: " + id + ")";
    }
}