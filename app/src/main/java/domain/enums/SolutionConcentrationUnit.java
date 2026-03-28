package domain.enums;

public enum SolutionConcentrationUnit {
    PERCENT("%"),
    MOL_PER_L("моль/литр"),
    G_PER_L("грамм/литр");

    private final String newName;
    SolutionConcentrationUnit(String newName) {
        this.newName = newName;
    }

    @Override
    public String toString() {
        return newName;
    }
}