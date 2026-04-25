package storage;

import domain.entity.*;
import java.util.Map;

public interface Storage {
    // Пользователи
    Map<String, User> loadAllUsers();
    void saveUser(User user);

    // Растворы
    Map<Long, Solution> loadAllSolutions();
    void saveSolution(Solution solution);
    void deleteSolution(long id);

    // Приготовления
    Map<Long, Preparation> loadAllPreparations();
    void savePreparation(Preparation preparation);
    void deletePreparation(long id);

    // Компоненты
    Map<Long, PreparationComponent> loadAllComponents();
    void saveComponent(PreparationComponent component);
    void deleteComponent(long id);
}
