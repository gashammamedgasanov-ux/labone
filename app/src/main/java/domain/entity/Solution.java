package domain.entity;

import domain.enums.SolutionConcentrationUnit;

public final class Solution extends BaseEntity{
    // Поля
    private String name;
    private double concentration;
    private SolutionConcentrationUnit concentrationUnit;
    private String solvent;
    private String ownerUsername;

    //ну вот здесь конструктор
    public Solution(String name,
                    double concentration,
                    SolutionConcentrationUnit concentrationUnit,
                    String solvent,
                    String ownerUsername) {
        super();
        this.name = name;
        this.concentration = concentration;
        this.concentrationUnit = concentrationUnit;
        this.solvent = solvent;
        this.ownerUsername = ownerUsername;
    }

    // это геттеры

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


    //А это сеттеры мяу


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
}
