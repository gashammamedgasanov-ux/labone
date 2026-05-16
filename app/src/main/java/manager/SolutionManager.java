package manager;

import domain.entity.Solution;
import domain.enums.SolutionConcentrationUnit;
import validation.SolutionValidation;
import storage.Storage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.time.Instant;

public class SolutionManager {
    private final Map<Long, Solution> solutions = new HashMap<>();
    private long nextId = 1;
    private  Storage storage;

    public SolutionManager(){
    }

    public SolutionManager(Storage storage) {
        this.storage = storage;
        loadFromStorage();
    }

    public Storage getStorage() {
        return storage;
    }

    private void loadFromStorage() {
        if (storage != null) {
            solutions.clear();
            solutions.putAll(storage.loadAllSolutions());
            updateNextId();
            System.out.println("Загружено растворов из БД: " + solutions.size());
        }
    }




    //метод для команды sol_add
    public Solution addSolution(String name, double concentration, SolutionConcentrationUnit concentrationUnit, String solvent, String ownerUsername) throws IllegalArgumentException  {
        Solution solution = new Solution(name, concentration, concentrationUnit, solvent, ownerUsername);
        SolutionValidation.validate(solution);
        solution.setId(nextId++);
        solution.setCreatedAt(Instant.now());
        solution.setUpdatedAt(Instant.now());
        solutions.put(solution.getId(), solution);
        if (storage != null) {
            storage.saveSolution(solution);
        }
        return solution;
    }

    //метолд для команды sol_show
    public Solution getSolution(Long id) {
        if (storage != null) {
            Map<Long, Solution> all = storage.loadAllSolutions();
            Solution solution = all.get(id);
            if (solution == null) {
                throw new IllegalArgumentException("Раствор с id=" + id + " не найден");
            }
            return solution;
        }
        if (solutions.get(id) == null) {
            throw new IllegalArgumentException("Раствор с id=" + id + " не найден");
        }
        return solutions.get(id);
    }

    // метод для команды sol_list
    public Map<Long, Solution> getAll() {
        if (storage != null) {
            // ВСЕГДА СВЕЖИЕ ДАННЫЕ ИЗ БД
            return storage.loadAllSolutions();
        }
        return Collections.unmodifiableMap(solutions);
    }

    public void updateSolution(Long id, String newName, Double newConcentration, SolutionConcentrationUnit newUnit, String newSolvent) throws IllegalAccessException {
        Solution existing = getSolution(id);
        if (existing == null) {
            throw new IllegalArgumentException("Раствор с таким id =" + id + " не сущeствует");
        }
        String currentUser = existing.getOwnerUsername();
        Solution draftSolution = new Solution(
                newName != null ? newName : existing.getName(),
                newConcentration != null ? newConcentration : existing.getConcentration(),
                newUnit != null ? newUnit : existing.getConcentrationUnit(),
                newSolvent != null ? newSolvent : existing.getSolvent(),
                existing.getOwnerUsername());
        SolutionValidation.validate(draftSolution);
        if (newName != null) existing.setName(newName);
        if (newConcentration != null) existing.setConcentration(newConcentration);
        if (newUnit != null) existing.setConcentrationUnit(newUnit);
        if (newSolvent != null) existing.setSolvent(newSolvent);
        existing.setUpdatedAt(Instant.now());
        if (storage != null) {
            storage.saveSolution(existing);
            solutions.put(existing.getId(), existing);
        }
    }

    public void removeSolution(Long id, String currentUser) {
        if (!solutions.containsKey(id)) {
            throw new IllegalArgumentException("Раствор с id=" + id + " не найден");
        }
        Solution s = getSolution(id);
        if (!s.getOwnerUsername().equals(currentUser)) {
            throw new IllegalArgumentException("Нет прав на удаление чужого раствора");
        }
        solutions.remove(id);
        if (storage != null) {
            storage.deleteSolution(id);
        }
    }

    //методы для работы с файлами

    public void clear() {
        solutions.clear();
    }

    public void setAll(Map<Long, Solution> newSolutions) {
        solutions.clear();
        solutions.putAll(newSolutions);
    }

    public void updateNextId() {
        long maxId = 0;
        for (Long id : solutions.keySet()) {
            if (id > maxId) {
                maxId = id;
            }
        }
        nextId = maxId + 1;
    }

    public Map<Long, Solution> getSolutions() {
        return solutions;
    }



}
