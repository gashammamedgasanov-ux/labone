package ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import manager.*;
import storage.DatabaseStorage;
import storage.FileStorage;
import storage.LabData;
import validation.FileValidator;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainApp extends Application {

    private SolutionManager solutionManager;
    private PreparationManager preparationManager;
    private PreparationComponentManager componentManager;
    private UserManager userManager;
    private Stage primaryStage;
    private String lastUsedFilePath = null;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        DatabaseStorage storage = new DatabaseStorage();
        solutionManager = new SolutionManager(storage);
        preparationManager = new PreparationManager(storage);
        userManager = new UserManager(storage);
        BatchService batchService = new BatchService();
        componentManager = new PreparationComponentManager(preparationManager, batchService);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ui/login-view.fxml"));
        Parent root = loader.load();

        LoginController loginController = loader.getController();
        loginController.setMainApp(this);
        loginController.setUserManager(userManager);

        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.setTitle("Авторизация");
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Выход из программы");
            alert.setHeaderText("Сохранить изменения?");
            alert.setContentText("У вас есть несохранённые изменения. Сохранить перед выходом?");

            ButtonType buttonSave = new ButtonType("Сохранить и выйти");
            ButtonType buttonExit = new ButtonType("Выйти без сохранения");
            ButtonType buttonCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonSave, buttonExit, buttonCancel);

            alert.showAndWait().ifPresent(response -> {
                if (response == buttonSave) {
                quickSave();
                Platform.exit();
                } else if (response == buttonExit) {
                    Platform.exit();
                }
            });
        });
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
            lastUsedFilePath = filePath;
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

    public void quickSave() {
        if (lastUsedFilePath == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл для сохранения");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JSON files", "*.json")
            );
            fileChooser.setInitialFileName("data.json");

            File file = fileChooser.showSaveDialog(primaryStage);
            if (file == null) {
                return;
            }
            lastUsedFilePath = file.getAbsolutePath();
        }
        saveData(lastUsedFilePath);
        System.out.println("Сохранено в: " + lastUsedFilePath);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
