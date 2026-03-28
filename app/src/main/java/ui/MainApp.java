package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import manager.*;
import storage.FileStorage;
import storage.LabData;
import validation.FileValidator;

import java.io.IOException;
import java.util.List;

public class MainApp extends Application {

    private SolutionManager solutionManager;
    private PreparationManager preparationManager;
    private PreparationComponentManager componentManager;

    @Override
    public void start(Stage primaryStage) throws IOException {
        solutionManager = new SolutionManager();
        preparationManager = new PreparationManager();
        BatchService batchService = new BatchService();
        componentManager = new PreparationComponentManager(preparationManager, batchService);

        Parameters params = getParameters();
        if (!params.getRaw().isEmpty()) {
            String filePath = params.getRaw().get(0);
            loadData(filePath);
        }

        // ПРАВИЛЬНАЯ ЗАГРУЗКА FXML
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ui/main-view.fxml"));

        // Отладка
        System.out.println("Пробуем загрузить: " + getClass().getResource("/ui/main-view.fxml"));

        BorderPane root = loader.load();

        MainController controller = loader.getController();
        controller.setManagers(solutionManager, preparationManager, componentManager);
        controller.setMainApp(this);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Система учета растворов");
        primaryStage.setScene(scene);
        primaryStage.show();

        controller.refreshSolutions();
    }

    void loadData(String filePath) {
        try {
            FileStorage fileStorage = new FileStorage();
            FileValidator fileValidator = new FileValidator();

            LabData loadedData = fileStorage.load(filePath);
            List<String> errors = fileValidator.validate(loadedData);

            if (!errors.isEmpty()) {
                System.err.println("Ошибки в файле:");
                errors.forEach(System.err::println);
                return;
            }

            if (loadedData.getSolutions() != null) {
                solutionManager.setAll(loadedData.getSolutions());
                solutionManager.updateNextId();
            }
            if (loadedData.getPreparations() != null) {
                preparationManager.setAll(loadedData.getPreparations());
                preparationManager.updateNextId();
            }
            if (loadedData.getComponents() != null) {
                componentManager.setAll(loadedData.getComponents());
                componentManager.updateNextId();
            }

            System.out.println("Данные загружены из: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка загрузки: " + e.getMessage());
        }
    }

    public void saveData(String filePath) {
        try {
            FileStorage fileStorage = new FileStorage();
            LabData data = new LabData(
                    solutionManager.getSolutions(),
                    preparationManager.getAllPreparationsMap(),
                    componentManager.getAllComponentsMap()
            );
            fileStorage.save(data, filePath);
            System.out.println("Данные сохранены в: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
