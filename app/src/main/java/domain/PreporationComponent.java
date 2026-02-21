package domain;
import java.time.Instant;
public class PreporationComponent {
    private long id;
    private long preporationId;
    private long batchId;
    private double quantity;
    private ComponentUnit unit;
    private Instant createdAt;

    public PreporationComponent(long preporationId, long batchId, double quantity, ComponentUnit unit) {
        this.preporationId = preporationId;
        this.batchId = batchId;
        this.quantity = quantity;
        this.unit = unit;

    }

    // getters ))
    public long getId() {
        return id;
    }
    public long getPreporationId() {
        return preporationId;
    }
    public long getBatchId() {
        return batchId;
    }
    public double getQuantity() {
        return quantity;
    }
    public ComponentUnit getUnit() {
        return unit;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    //setters
    public void setId(long id) {
        this.id = id;
    }
    public void setPreporationId(long preporationId) {
        this.preporationId = preporationId;
    }
    public void setBatchId(long batchId) {
        this.batchId = batchId;
    }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    public void setUnit(ComponentUnit unit) {
        this.unit = unit;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}