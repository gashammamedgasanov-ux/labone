package manager

import domain.Preparation;
import domain.FinalQuantutyUnit;
import validation.PreparationValidator;
import java.time.Instant;
import java.util.*;

public class PreparationManager {
    private final Map<Long, Preparation> preparations = new HashMap<>();
    private long nextId = 1;
    public Preparation addPreparation(long solutionId,
                                      double finalQuantity,
                                      FinalquantityUnit finalUnit,
                                      String comment,
                                      String ownerUsername,
                                      Instant preparedAt) {
        Preparation preparation = new Preparation(solutionId, finalQuantity, finalUnit, comment, ownerUsername, preparedAt);
        PreparationValidator.validate(preparation);
        preparation.setId(nextId++);
        preparation.setCreatedAt(Instant.now());
        preparation.setUpdatedAt(Instant.now())
        preparations.put(preparation.getId(), preparation);
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

    public  boolean exists(Long id) {
        return preparations.containsKey(id);
    }

    public void removePreparation(Long id) {
        if (!preparations.containsKey(id)) {
            throw new IllegalArgumentException("Приготовление с id=" + id + " не найдено");
        }
        preparations.remove(id);
    }

    public List<Preparation> getPreparationsForSolution(Long solutionId) {
        List<Preparation> result = new ArrayList<>();
        for (preparation p : preparations.values()) {
            if (p.getSolutionId() == solutionId) {
                result.add(p);
            }
        }
        return result;
    }
}