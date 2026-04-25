package manager;

import domain.entity.Preparation;
import domain.enums.FinalQuantityUnit;
import validation.PreparationValidator;
import storage.Storage;
import java.time.Instant;
import java.util.*;

public class PreparationManager {
    private final Map<Long, Preparation> preparations = new HashMap<>();
    private long nextId = 1;
    private Storage storage = null;

    public PreparationManager(){
    }

    public PreparationManager(Storage storage) {
        this.storage = storage;
        loadFromStorage();
    }
    private void loadFromStorage() {
        if (storage != null) {
            preparations.clear();
            preparations.putAll(storage.loadAllPreparations());
            updateNextId();
            System.out.println("Загружено приготовлений из БД: " + preparations.size());
        }
    }

    //метод для команды prep_add
    public Preparation addPreparation(long solutionId,
                                      double finalQuantity,
                                      FinalQuantityUnit finalUnit,
                                      String comment,
                                      String ownerUsername,
                                      Instant preparedAt) {
        Preparation preparation = new Preparation(solutionId, finalQuantity, finalUnit, comment, ownerUsername, preparedAt);
        PreparationValidator.validate(preparation);
        preparation.setId(nextId++);
        preparation.setCreatedAt(Instant.now());
        preparation.setUpdatedAt(Instant.now());
        preparations.put(preparation.getId(), preparation);
        if (storage != null) {
            storage.savePreparation(preparation);
        }
        return preparation;
    }

    public Preparation getPreparation(Long id) {
        Preparation prep = preparations.get(id);
        if (prep == null) {
            throw new IllegalArgumentException("Приготовление с id=" + id + " не найдено");
        }
        return prep;
    }

    public Collection<Preparation> getAllPreparations() {
        return Collections.unmodifiableCollection(preparations.values());
    }

    public boolean exists(Long id) {
        return preparations.containsKey(id);
    }

    //метод для команды prep_delete
    public void removePreparation(Long id,String currentUser) {
        if (!preparations.containsKey(id)) {
            throw new IllegalArgumentException("Приготовление с id=" + id + " не найдено");
        }
        Preparation p = getPreparation(id);
        if (!p.getOwnerUsername().equals(currentUser)) {
            throw new IllegalArgumentException("Нет прав на удаление чужого приготовления");
        }
        preparations.remove(id);
        if (storage != null) {
            storage.deletePreparation(id);
        }
    }

    //метолд для команды prep_show
    public List<Preparation> getPreparationsForSolution(Long solutionId) {
        List<Preparation> result = new ArrayList<>();
        for (Preparation p : preparations.values()) {
            if (p.getSolutionId() == solutionId) {
                result.add(p);
            }
        }
        return result;
    }

    //метод для команды prep_update
    public void updatePreparation(Long id,
                                  Double newFinalQuantity,
                                  FinalQuantityUnit newFinalUnit,
                                  String newComment) {
        Preparation existing = getPreparation(id);  // throws if not found
        Preparation draft = new Preparation(
                existing.getSolutionId(),
                newFinalQuantity != null ? newFinalQuantity : existing.getFinalQuantity(),
                newFinalUnit != null ? newFinalUnit : existing.getFinalUnit(),
                newComment != null ? newComment : existing.getComment(),
                existing.getOwnerUsername(),
                existing.getPreparedAt()  // дата приготовления не меняется
        );
        PreparationValidator.validate(draft);
        if (newFinalQuantity != null) existing.setFinalQuantity(newFinalQuantity);
        if (newFinalUnit != null) existing.setFinalUnit(newFinalUnit);
        if (newComment != null) existing.setComment(newComment);
        existing.setUpdatedAt(Instant.now());
        if (storage != null) {
            storage.savePreparation(existing);
        }
    }

    public List<Preparation> getLastPreparationsForSolution(long solutionId, int limit) {
        //получаем все приготовления для раствора
        List<Preparation> result = getPreparationsForSolution(solutionId);

        //ортируем полученный список приготовлений по убыванию даты приготовления
        result.sort((p1, p2) -> {
            if (p1.getPreparedAt() == null && p2.getPreparedAt() == null) return 0;
            if (p1.getPreparedAt() == null) return 1;
            if (p2.getPreparedAt() == null) return -1;
            return p2.getPreparedAt().compareTo(p1.getPreparedAt());
        });

        if (limit > 0 && limit < result.size()) {
            return result.subList(0, limit);
        }
        return result;
    }

    public void clear() {
        preparations.clear();
    }

    public void setAll(Map<Long, Preparation> newPreparations) {
        preparations.clear();
        preparations.putAll(newPreparations);
    }

    public void updateNextId() {
        long maxId = 0;
        for (Long id : preparations.keySet()) {
            if (id > maxId) {
                maxId = id;
            }
        }
        nextId = maxId + 1;
    }

    public Map<Long, Preparation> getAllPreparationsMap() {
        return Collections.unmodifiableMap(preparations);
    }
}



