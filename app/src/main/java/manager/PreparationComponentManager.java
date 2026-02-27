package manager;

import domain.entity.PreparationComponent;
import domain.enums.ComponentUnit;
import validation.PreparationComponentValidation;

import java.time.Instant;
import java.util.*;

public class PreparationComponentManager {
    private final Map<Long, PreparationComponent> components = new HashMap<>();
    private long nextId = 1;


    private final PreparationManager preparationManager;
    private final BatchService batchService;

    public PreparationComponentManager(PreparationManager preparationManager,
                                       BatchService batchService) {
        this.preparationManager = preparationManager;
        this.batchService = batchService;
    }

    public PreparationComponent addComponent(long preparationId,
                                             long batchId,
                                             double quantity,
                                             ComponentUnit unit) throws IllegalAccessException {
        // 1. Проверка существования связанных объектов
        if (!preparationManager.exists(preparationId)) {
            throw new IllegalArgumentException("Приготовление с id=" + preparationId + " не существует");
        }
        if (!batchService.exists(batchId)) {
            throw new IllegalArgumentException("Партия реактива с id=" + batchId + " не существует");
        }


        PreparationComponent component = new PreparationComponent(preparationId, batchId, quantity, unit);


        PreparationComponentValidation.validate(component);

        component.setId(nextId++);
        component.setCreatedAt(Instant.now());
        components.put(component.getId(), component);
        return component;
    }

    public PreparationComponent getComponent(Long id) {
        PreparationComponent comp = components.get(id);
        if (comp == null) {
            throw new IllegalArgumentException("Компонент с id=" + id + " не найден");
        }
        return comp;
    }

    public Collection<PreparationComponent> getAllComponents() {
        return Collections.unmodifiableCollection(components.values());
    }

    public List<PreparationComponent> getComponentsForPreparation(Long preparationId) {
        List<PreparationComponent> result = new ArrayList<>();
        for (PreparationComponent c : components.values()) {
            if (c.getPreparationId() == preparationId) {
                result.add(c);
            }
        }
        return result;
    }

    public boolean exists(Long id) {
        return components.containsKey(id);
    }

    public void removeComponent(Long id) {
        if (!components.containsKey(id)) {
            throw new IllegalArgumentException("Компонент с id=" + id + " не найден");
        }
        components.remove(id);
    }
}
