package validation;
import domain.Preparation;

public class PreparationValidator {
    public static void validate(Preparation preparation) {
        if (preparation.getFinalQuantity() <= 0) {
            throw new IllegalArgumentException("Итоговое количество раствора должно быть больше нуля");
        }
        if (component.getUnit() == null) {
            throw new IllegalArgumentException("Единицы измерения не могут быть пустыми");
        }
        if (component.getComment() != null && preparation.getComment().length() > 128) {
            throw new IllegalArgumentException("Комментарий слишком длинный (максимум 128 символов)")
        }
        if (preparation.getOwnerUsername() == null || preparation.getOwnerUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Логин владельца не может быть пустым");
        }
    }
}