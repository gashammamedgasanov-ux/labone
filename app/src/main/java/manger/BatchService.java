package manger;

public class BatchService{
        public boolean exists(Long id) {
            return id != null && id > 0 && id <= 100;
        }
}
