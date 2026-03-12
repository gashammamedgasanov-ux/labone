package domain.enums;

public enum SolutionConcentrationUnit {
    PERCENT("%"),
    MOL_PER_L("моль/литр"),
    G_PER_L("грамм/литр");

    private final String newName;
    private SolutionConcentrationUnit(String newName) {
        this.newName = newName;
    }

    }