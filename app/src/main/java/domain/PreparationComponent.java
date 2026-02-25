package domain;
import java.time.Instant;
public class PreparationComponent {
    private long id;
    private long preparationId;
    private long batchId;
    private double quantity;
    private ComponentUnit unit;
    private Instant createdAt;

    public PreparationComponent(long preparationId, long batchId, double quantity, ComponentUnit unit) {
        this.preparationId = preparationId;
        this.batchId = batchId;
        this.quantity = quantity;
        this.unit = unit;
        this.createdAt = Instant.now();
    }

    // getters ))
    public long getId() {
        return id;
    }
    public long getPreparationId() {
        return preparationId;
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
    public void setPreparationId(long preporationId) {
        this.preparationId = preporationId;
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