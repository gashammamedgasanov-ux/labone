package manager;

public class BatchService {
    public boolean exists(Long id) {
        return id != null && id > 0 && id <= 100;
    }
}
//  не является частью нашей предметной области однако связан с компонентом приготовления, сделали временное решение
// такое, существует всего 100 партий
