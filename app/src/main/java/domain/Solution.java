package domain;
import java.time.Instant;
public final class Solution {
    // Поля
    private Long id;
    private String name;
    private double concentration;
    private SolutionConcentrationUnit concentrationUnit;
    private String solvent;
    private String ownerUsername;
    private Instant createdAt;
    private Instant updatedAt;

//ну вот здесь конструктор
    public Solution(String name,
                    double concentration,
                    SolutionConcentrationUnit concentrationUnit,
                    String solvent,
                    String ownerUsername)
    {
        this.name = name;
        this.concentration = concentration;
        this.concentrationUnit = concentrationUnit;
        this.solvent = solvent;
        this.ownerUsername = ownerUsername;
    }

// это геттеры
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getConcentration() {
        return concentration;
    }

    public SolutionConcentrationUnit getConcentrationUnit() {
        return concentrationUnit;
    }

    public String getSolvent() {
        return solvent;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    //А это сеттеры мяу
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConcentration(double concentration) {
        this.concentration = concentration;
    }

    public void setConcentrationUnit(SolutionConcentrationUnit concentrationUnit) {
        this.concentrationUnit = concentrationUnit;
    }

    public void setSolvent(String solvent) {
        this.solvent = solvent;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}