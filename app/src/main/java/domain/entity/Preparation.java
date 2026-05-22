package domain.entity;

import domain.enums.FinalQuantityUnit;

import java.time.Instant;

public class Preparation extends BaseEntity {
    private long solutionId;
    private double finalQuantity;
    private FinalQuantityUnit finalUnit;
    private String comment;
    private String ownerUsername;
    private Instant preparedAt;

    public Preparation() {
        super();
    }

    public Preparation(long solutionId, double finalQuantity, FinalQuantityUnit finalUnit,
                       String comment, String ownerUsername, Instant preparedAt) {
        super();
        this.solutionId = solutionId;
        this.finalQuantity = finalQuantity;
        this.finalUnit = finalUnit;
        this.comment = comment;
        this.ownerUsername = ownerUsername;
        this.preparedAt = (preparedAt != null) ? preparedAt : Instant.now();
    }

    public Preparation(long id, long solutionId, Instant createdAt, String ownerUsername) {
    }

    public long getSolutionId() {
        return solutionId;
    }

    public void setSolutionId(long solutionId) {
        this.solutionId = solutionId;
    }

    public double getFinalQuantity() {
        return finalQuantity;
    }

    public void setFinalQuantity(double finalQuantity) {
        this.finalQuantity = finalQuantity;
    }

    public FinalQuantityUnit getFinalUnit() {
        return finalUnit;
    }

    public void setFinalUnit(FinalQuantityUnit finalUnit) {
        this.finalUnit = finalUnit;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public Instant getPreparedAt() {
        return preparedAt;
    }

    public void setPreparedAt(Instant preparedAt) {
        this.preparedAt = preparedAt;
    }
}
