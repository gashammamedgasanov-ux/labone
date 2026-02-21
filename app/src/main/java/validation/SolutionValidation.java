package validation;
import domain.Solution;


public class SolutionValidation {
    public static void validate(Solution solution) throws IllegalAccessException {
        if (solution.getName()== null) {
            throw new IllegalAccessException("Название раствора не может быть пустым");
        }
        if (solution.getName().length() > 128) {
            throw new IllegalAccessException("Название раствора сликом длинное(должно быть не более 128 символов)");
        }
        if (solution.getConcentration()<0){
            throw new IllegalAccessException("Концентрация не должна быть отрицательной");
        }
        if (solution.getSolvent().length()>64){
            throw new IllegalAccessException("Название растворителя слишком длинное(должно быть не более 64 символов)");
        }
    }
}
