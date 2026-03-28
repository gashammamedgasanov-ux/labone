package domain.entity;

import domain.enums.ComponentUnit;

public class PreparationComponent extends BaseEntity {

    private long preparationId;
    private long batchId;
    private double quantity;
    private ComponentUnit unit;

    public PreparationComponent() {
        super();
    }

    public PreparationComponent(long preparationId, long batchId, double quantity, ComponentUnit unit) {
        super();
        this.preparationId = preparationId;
        this.batchId = batchId;
        this.quantity = quantity;
        this.unit = unit;
    }

    // getters ))

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

    //setters

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
}
