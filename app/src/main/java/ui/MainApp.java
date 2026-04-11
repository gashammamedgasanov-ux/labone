package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
    private UserManager userManager;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        solutionManager = new SolutionManager();
        preparationManager = new PreparationManager();
        BatchService batchService = new BatchService();
        componentManager = new PreparationComponentManager(preparationManager, batchService);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ui/login-view.fxml"));
        Parent root = loader.load();

        LoginController loginController = loader.getController();
        loginController.setMainApp(this);

        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.setTitle("Авторизация");
        primaryStage.show();
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void openMainWindow() {
        try {
            Parameters params = getParameters();
            if (!params.getRaw().isEmpty()) {
                loadData(params.getRaw().get(0));
            }

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ui/main-view.fxml"));
            BorderPane root = loader.load();

            MainController controller = loader.getController();
            controller.setManagers(solutionManager, preparationManager, componentManager);
            controller.setUserManager(userManager);
            controller.setMainApp(this);

            Scene scene = new Scene(root, 1000, 700);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Система учета растворов");

            controller.refreshSolutions();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loadData(String filePath) {
        try {
            FileStorage fileStorage = new FileStorage();
            FileValidator fileValidator = new FileValidator();

            LabData loadedData = fileStorage.load(filePath);
            List<String> errors = fileValidator.validate(loadedData);

            if (!errors.isEmpty()) {
                StringBuilder errorMsg = new StringBuilder("Ошибки в файле:\n");
                for (String error : errors) {
                    errorMsg.append("• ").append(error).append("\n");
                }
                showErrorDialog("Ошибка загрузки", errorMsg.toString());
                return false;
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

            return true;
        } catch (IOException e) {
            showErrorDialog("Ошибка загрузки", e.getMessage());
            return false;
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
        } catch (IOException e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
