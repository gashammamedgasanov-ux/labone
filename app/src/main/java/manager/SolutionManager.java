package manager;

import domain.entity.Solution;
import domain.enums.SolutionConcentrationUnit;
import validation.SolutionValidation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.time.Instant;

public class SolutionManager {
    private final Map<Long, Solution> solutions = new HashMap<>();
    private long nextId = 1;

    //метод для команды sol_add
    public Solution addSolution(String name, double concentration, SolutionConcentrationUnit concentrationUnit, String solvent, String ownerUsername) throws IllegalArgumentException  {
        Solution solution = new Solution(name, concentration, concentrationUnit, solvent, ownerUsername);
        SolutionValidation.validate(solution);
        solution.setId(nextId++);
        solution.setCreatedAt(Instant.now());
        solution.setUpdatedAt(Instant.now());
        solutions.put(solution.getId(), solution);
        return solution;
    }

    //метолд для команды sol_show
    public Solution getSolution(Long id) {
        if (solutions.get(id) == null) {
            throw new IllegalArgumentException("Раствор с id=" + id + " не найден");
        }
        return solutions.get(id);
    }

    // метод для команды sol_list
    public Map<Long, Solution> getAll() {
        return Collections.unmodifiableMap(solutions);
    }

    public void updateSolution(Long id, String newName, Double newConcentration, SolutionConcentrationUnit newUnit, String newSolvent) throws IllegalAccessException {
        Solution existing = getSolution(id);
        if (existing == null) {
            throw new IllegalArgumentException("Раствор с таким id =" + id + " не сущeствует");
        }
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
    }

    public void removeSolution(Long id) {
        if (!solutions.containsKey(id)) {
            throw new IllegalArgumentException("Раствор с id=" + id + " не найден");
        }
        solutions.remove(id);
    }
}
