package manger;

import domain.Solution;
import validation.SolutionValidation;
import java.util.HashMap;
import java.util.Map;
import java.time.Instant;

public class SolutionManager {
    private final Map<Long, Solution> solutions = new HashMap<>();
    private long nextId = 1;

    public Solution addSolution(Solution solution) {
        solution.setId(nextId++);
        solutions.put(solution.getId(), solution);
        return solution;
    }

}
