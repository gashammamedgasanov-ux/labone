package validation;
import domain.Solution;
import org.gradle.internal.impldep.org.jsoup.helper.ValidationException;

public class SolutionValidation {
    public static void validate(Solution solution) {
        if (solution.getName()== null) {
            throw new ValidationException("Name is required");
        }
    }
}
