package validation;

import domain.entity.Preparation;
import domain.entity.PreparationComponent;
import domain.entity.Solution;
import storage.LabData;
import java.util.*;

public class FileValidator {
    public List<String> validate(LabData data) {
        List<String> errors = new ArrayList<>();

        if (data.getSolutions() == null) {
            errors.add("Коллекция растворов отсутствует в файле");
            return errors;
        }

        if (data.getPreparations() == null) {
            errors.add("Коллекция приготовлений  отсутствует в файле");
            return errors;
        }

        if (data.getComponents() == null) {
            errors.add("Коллекция компонентов  отсутствует в файле");
            return errors;
        }


        for (Map.Entry<Long, Solution> entry : data.getSolutions().entrySet()) {
            Long keyId = entry.getKey();
            Solution solution = entry.getValue();

            // Проверка: id в ключе должен совпадать с id в объекте
            if (solution.getId() != keyId) {
                errors.add(String.format(
                        "Раствор: несоответствие id (ключ=%d, поле id=%d)",
                        keyId, solution.getId()
                ));
            }
            // Проверка полей раствора
            try {
                SolutionValidation.validate(solution);
            } catch (IllegalArgumentException e) {
                errors.add(String.format(
                        "Раствор id=%d: %s", keyId, e.getMessage()
                ));
            }
        }


        for (Map.Entry<Long, Preparation> entry : data.getPreparations().entrySet()) {
            Long keyId = entry.getKey();
            Preparation prep = entry.getValue();

            // Проверка: id в ключе должен совпадать с id в объекте
            if (prep.getId() != keyId) {
                errors.add(String.format(
                        "Приготовление: несоответствие id (ключ=%d, поле id=%d)",
                        keyId, prep.getId()
                ));
            }

            // Проверка полей приготовления
            try {
                PreparationValidator.validate(prep);
            } catch (IllegalArgumentException e) {
                errors.add(String.format(
                        "Приготовление id=%d: %s", keyId, e.getMessage()
                ));
            }

            // Проверка ссылочной целостности: существует ли раствор?
            if (!data.getSolutions().containsKey(prep.getSolutionId())) {
                errors.add(String.format(
                        "Приготовление id=%d: ссылается на несуществующий раствор solutionId=%d",
                        keyId, prep.getSolutionId()
                ));
            }
        }

        for (Map.Entry<Long, PreparationComponent> entry : data.getComponents().entrySet()) {
            Long keyId = entry.getKey();
            PreparationComponent comp = entry.getValue();

            // Проверка: id в ключе должен совпадать с id в объекте
            if (comp.getId() != keyId) {
                errors.add(String.format(
                        "Компонент: несоответствие id (ключ=%d, поле id=%d)",
                        keyId, comp.getId()
                ));
            }

            // Проверка полей компонента через существующий валидатор
            try {
                PreparationComponentValidation.validate(comp);
            } catch (IllegalArgumentException e) {
                errors.add(String.format(
                        "Компонент id=%d: %s", keyId, e.getMessage()
                ));
            }

            // Проверка ссылочной целостности: существует ли приготовление?
            if (!data.getPreparations().containsKey(comp.getPreparationId())) {
                errors.add(String.format(
                        "Компонент id=%d: ссылается на несуществующее приготовление preparationId=%d",
                        keyId, comp.getPreparationId()
                ));
            }
        }

        return errors;
    }
}

