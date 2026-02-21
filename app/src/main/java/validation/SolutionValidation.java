package validation;
import domain.Solution;


public class SolutionValidation {
    public static void validate(Solution solution) throws IllegalArgumentException {
        if (solution.getName()== null) {
            throw new IllegalArgumentException("Название раствора не может быть пустым");
        }
        if (solution.getName().length() > 128) {
            throw new IllegalArgumentException("Название раствора сликом длинное(должно быть не более 128 символов)");
        }
        if (solution.getConcentration()<0){
            throw new IllegalArgumentException("Концентрация не должна быть отрицательной");
        }
        if (solution.getSolvent().length()>64){
            throw new IllegalArgumentException("Название растворителя слишком длинное(должно быть не более 64 символов)");
        }
    }
}
