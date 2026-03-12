package manager;

public class BatchService {
    private static final int MAX_BATCH_ID = 100;
    private static final int MIN_BATCH_ID = 1;

    public boolean exists(Long id) {
        return id != null && id >= MIN_BATCH_ID && id <= MAX_BATCH_ID;
    }
}
//  не является частью нашей предметной области однако связан с компонентом приготовления, сделали временное решение
// такое, существует всего 100 партий
