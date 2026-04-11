package ui;

import domain.entity.Preparation;
import domain.entity.PreparationComponent;
import domain.entity.Solution;
import domain.enums.SolutionConcentrationUnit;
import domain.enums.FinalQuantityUnit;
import domain.enums.ComponentUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.io.File;
import manager.*;

import java.time.Instant;
import java.util.List;

public class MainController {

    // таблица растворов
    @FXML private TableView<Solution> solutionsTable;
    @FXML private TableColumn<Solution, Long> colId;
    @FXML private TableColumn<Solution, String> colName;
    @FXML private TableColumn<Solution, Double> colConcentration;
    @FXML private TableColumn<Solution, String> colUnit;
    @FXML private TableColumn<Solution, String> colSolvent;
    @FXML private TableColumn<Solution, String> colOwner;

    // инфа про раствор
    @FXML private TextField txtName;
    @FXML private TextField txtConcentration;
    @FXML private ComboBox<SolutionConcentrationUnit> cmbUnit;
    @FXML private TextField txtSolvent;
    @FXML private TextField txtOwner;
    @FXML private Label lblCreatedAt;
    @FXML private Label lblUpdatedAt;

    //Приготовления
    @FXML private TableView<Preparation> preparationsTable;
    @FXML private TableColumn<Preparation, Long> colPrepId;
    @FXML private TableColumn<Preparation, Double> colPrepQty;
    @FXML private TableColumn<Preparation, String> colPrepUnit;
    @FXML private TableColumn<Preparation, String> colPrepDate;
    @FXML private TableColumn<Preparation, String> colPrepComment;
    @FXML private TextField txtPrepQty;
    @FXML private ComboBox<FinalQuantityUnit> cmbPrepUnit;
    @FXML private TextField txtPrepComment;
    @FXML private TextField txtEditPrepQty;
    @FXML private ComboBox<FinalQuantityUnit> cmbEditPrepUnit;
    @FXML private TextField txtEditPrepComment;

    // компоненты
    @FXML private TableView<PreparationComponent> componentsTable;
    @FXML private TableColumn<PreparationComponent, Long> colCompId;
    @FXML private TableColumn<PreparationComponent, Long> colCompBatchId;
    @FXML private TableColumn<PreparationComponent, Double> colCompQty;
    @FXML private TableColumn<PreparationComponent, String> colCompUnit;
    @FXML private TextField txtCompBatchId;
    @FXML private TextField txtCompQty;
    @FXML private ComboBox<ComponentUnit> cmbCompUnit;
    @FXML private TextField txtEditCompBatchId;
    @FXML private TextField txtEditCompQty;
    @FXML private ComboBox<ComponentUnit> cmbEditCompUnit;


    private SolutionManager solutionManager;
    private PreparationManager preparationManager;
    private PreparationComponentManager componentManager;
    private MainApp mainApp;
    private UserManager userManager;

    // данные для таблиц
    private ObservableList<Solution> solutions = FXCollections.observableArrayList();
    private ObservableList<Preparation> preparations = FXCollections.observableArrayList();
    private ObservableList<PreparationComponent> components = FXCollections.observableArrayList();

    private Solution currentSolution = null;
    private Preparation currentPreparation = null;

    @FXML
    public void initialize() {
        // Настройка таблицы растворов
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colConcentration.setCellValueFactory(new PropertyValueFactory<>("concentration"));
        colUnit.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getConcentrationUnit().toString()
                ));
        colSolvent.setCellValueFactory(new PropertyValueFactory<>("solvent"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("ownerUsername"));

        // Настройка ComboBox для единиц концентрации
        cmbUnit.getItems().setAll(SolutionConcentrationUnit.values());

        // Настройка таблицы приготовлений
        colPrepId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPrepQty.setCellValueFactory(new PropertyValueFactory<>("finalQuantity"));
        colPrepUnit.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getFinalUnit().toString()
                ));
        colPrepDate.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getPreparedAt() != null ?
                                cellData.getValue().getPreparedAt().toString() : "-"
                ));
        colPrepComment.setCellValueFactory(new PropertyValueFactory<>("comment"));

        // Настройка ComboBox для единиц приготовления
        cmbPrepUnit.getItems().setAll(FinalQuantityUnit.values());

        // Настройка таблицы компонентов
        colCompId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCompBatchId.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        colCompQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCompUnit.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getUnit().toString()
                ));

        // Настройка ComboBox для единиц компонентов
        cmbCompUnit.getItems().setAll(ComponentUnit.values());

        // Привязка данных к таблицам
        solutionsTable.setItems(solutions);
        preparationsTable.setItems(preparations);
        componentsTable.setItems(components);

        // Слушать выбора раствора
        solutionsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    if (selected != null) {
                        currentSolution = selected;
                        showSolutionDetails(selected);
                        loadPreparations(selected.getId());
                    } else {
                        clearDetails();
                        preparations.clear();
                        components.clear();
                        currentSolution = null;
                    }
                }
        );

        // Слушатель выбора приготовления
        preparationsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    if (selected != null) {
                        currentPreparation = selected;
                        loadComponents(selected.getId());
                    } else {
                        components.clear();
                        currentPreparation = null;
                    }
                }
        );
    }

    public void setManagers(SolutionManager sm, PreparationManager pm, PreparationComponentManager cm) {
        this.solutionManager = sm;
        this.preparationManager = pm;
        this.componentManager = cm;
    }
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setMainApp(MainApp app) {
        this.mainApp = app;
    }

    public void refreshSolutions() {
        solutions.clear();
        solutions.addAll(solutionManager.getAll().values());
        solutionsTable.refresh();
    }

    private void showSolutionDetails(Solution s) {
        txtName.setText(s.getName());
        txtConcentration.setText(String.valueOf(s.getConcentration()));
        cmbUnit.setValue(s.getConcentrationUnit());
        txtSolvent.setText(s.getSolvent());
        txtOwner.setText(s.getOwnerUsername());
        lblCreatedAt.setText(s.getCreatedAt() != null ? s.getCreatedAt().toString() : "-");
        lblUpdatedAt.setText(s.getUpdatedAt() != null ? s.getUpdatedAt().toString() : "-");
    }

    private void clearDetails() {
        txtName.clear();
        txtConcentration.clear();
        cmbUnit.setValue(null);
        txtSolvent.clear();
        txtOwner.clear();
        lblCreatedAt.setText("-");
        lblUpdatedAt.setText("-");
        txtPrepQty.clear();
        cmbPrepUnit.setValue(null);
        txtPrepComment.clear();
        txtCompBatchId.clear();
        txtCompQty.clear();
        cmbCompUnit.setValue(null);
    }

    private void loadPreparations(long solutionId) {
        preparations.clear();
        List<Preparation> preps = preparationManager.getPreparationsForSolution(solutionId);
        preparations.addAll(preps);
        preparationsTable.refresh();
        components.clear();
    }

    private void loadComponents(long preparationId) {
        components.clear();
        List<PreparationComponent> comps = componentManager.getComponentsForPreparation(preparationId);
        components.addAll(comps);
        componentsTable.refresh();
    }

    // команды для растворов

    @FXML
    private void handleAddSolution() {
        try {
            String name = txtName.getText().trim();
            if (name.isEmpty()) { showAlert("Ошибка", "Название не может быть пустым"); return; }
            double conc = Double.parseDouble(txtConcentration.getText().trim());
            SolutionConcentrationUnit unit = cmbUnit.getValue();
            if (unit == null) { showAlert("Ошибка", "Выберите единицы"); return; }
            String solvent = txtSolvent.getText().trim();
            String owner = txtOwner.getText().trim();
            if (owner.isEmpty()) { owner = "SYSTEM"; }

            owner = userManager.getCurrentUser().getLogin();
            solutionManager.addSolution(name, conc, unit, solvent, owner);
            refreshSolutions();
            showAlert("Успех", "Раствор добавлен");
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Концентрация должна быть числом");
        } catch (IllegalArgumentException e) {
            showAlert("Ошибка", e.getMessage());
        }
    }

    @FXML
    private void handleUpdateSolution() {
        if (currentSolution == null) {
            showAlert("Ошибка", "Выберите раствор для редактирования");
            return;
        }
        String currentUser = userManager.getCurrentUser().getLogin();
        if (!currentSolution.getOwnerUsername().equals(currentUser)) {
            showAlert("Ошибка", "Нельзя редактировать чужой раствор");
            return;
        }
        try {
            String name = txtName.getText().trim();
            if (name.isEmpty()) { showAlert("Ошибка", "Название не может быть пустым"); return; }
            double conc = Double.parseDouble(txtConcentration.getText().trim());
            SolutionConcentrationUnit unit = cmbUnit.getValue();
            String solvent = txtSolvent.getText().trim();
            String owner = txtOwner.getText().trim();

            solutionManager.updateSolution(currentSolution.getId(), name, conc, unit, solvent);
            refreshSolutions();
            showAlert("Успех", "Раствор обновлен");
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Концентрация должна быть числом");
        } catch (IllegalArgumentException e) {
            showAlert("Ошибка", e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleDeleteSolution() {
        if (currentSolution == null) {
            showAlert("Ошибка", "Выберите раствор для удаления");
            return;
        }
        String currentUser = userManager.getCurrentUser().getLogin();
        if (!currentSolution.getOwnerUsername().equals(currentUser)) {
            showAlert("Ошибка", "Нельзя удалять чужой раствор");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setContentText("Удалить раствор \"" + currentSolution.getName() + "\"?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            solutionManager.removeSolution(currentSolution.getId(),currentUser);
            refreshSolutions();
            clearDetails();
            preparations.clear();
            components.clear();
            currentSolution = null;
            showAlert("Успех", "Раствор удален");
        }
    }

    // команды для приготовлений

    @FXML
    private void handleAddPreparation() {
        if (currentSolution == null) {
            showAlert("Ошибка", "Сначала выберите раствор");
            return;
        }
        try {
            double qty = Double.parseDouble(txtPrepQty.getText().trim());
            FinalQuantityUnit unit = cmbPrepUnit.getValue();
            if (unit == null) { showAlert("Ошибка", "Выберите единицы"); return; }
            String comment = txtPrepComment.getText().trim();

            String owner = userManager.getCurrentUser().getLogin();
            preparationManager.addPreparation(currentSolution.getId(), qty, unit, comment, owner, Instant.now());
            loadPreparations(currentSolution.getId());
            showAlert("Успех", "Приготовление добавлено");
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Количество должно быть числом");
        } catch (IllegalArgumentException e) {
            showAlert("Ошибка", e.getMessage());
        }
    }

    @FXML
    private void handleDeletePreparation() {
        Preparation selected = preparationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите приготовление для удаления");
            return;
        }
        String currentUser = userManager.getCurrentUser().getLogin();
        if (!selected.getOwnerUsername().equals(currentUser)) {
            showAlert("Ошибка", "Нельзя удалять чужое приготовление");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setContentText("Удалить приготовление #" + selected.getId() + "?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            preparationManager.removePreparation(selected.getId(), currentUser);
            loadPreparations(currentSolution.getId());
            components.clear();
            showAlert("Успех", "Приготовление удалено");
        }
    }
    @FXML
    private void handleEditPreparation() {
        Preparation selected = preparationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите приготовление для редактирования");
            return;
        }
        String currentUser = userManager.getCurrentUser().getLogin();
        if (!selected.getOwnerUsername().equals(currentUser)) {
            showAlert("Ошибка", "Нельзя редактировать чужое приготовление");
            return;
        }

        try {
            Double newQty = null;
            FinalQuantityUnit newUnit = null;
            String newComment = null;

            if (!txtEditPrepQty.getText().trim().isEmpty()) {
                newQty = Double.parseDouble(txtEditPrepQty.getText().trim());
            }
            if (cmbEditPrepUnit.getValue() != null) {
                newUnit = cmbEditPrepUnit.getValue();
            }
            if (!txtEditPrepComment.getText().trim().isEmpty()) {
                newComment = txtEditPrepComment.getText().trim();
            }

            preparationManager.updatePreparation(
                    selected.getId(), newQty, newUnit, newComment
            );

            // Обновляем таблицу
            loadPreparations(currentSolution.getId());

            // Очищаем поля редактирования
            txtEditPrepQty.clear();
            cmbEditPrepUnit.setValue(null);
            txtEditPrepComment.clear();

            showAlert("Успех", "Приготовление обновлено");

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Количество должно быть числом");
        } catch (IllegalArgumentException e) {
            showAlert("Ошибка", e.getMessage());
        }
    }

    // команды для компонентов
    @FXML
    private void handleAddComponent() {
        if (currentPreparation == null) {
            showAlert("Ошибка", "Сначала выберите приготовление");
            return;
        }
        try {
            long batchId = Long.parseLong(txtCompBatchId.getText().trim());
            if (batchId < 1 || batchId > 100) {
                showAlert("Ошибка", "ID партии должен быть от 1 до 100");
                return;
            }
            double qty = Double.parseDouble(txtCompQty.getText().trim());
            ComponentUnit unit = cmbCompUnit.getValue();
            if (unit == null) { showAlert("Ошибка", "Выберите единицы"); return; }

            componentManager.addComponent(currentPreparation.getId(), batchId, qty, unit);
            loadComponents(currentPreparation.getId());
            showAlert("Успех", "Компонент добавлен");
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "ID партии и количество должны быть числами");
        } catch (IllegalAccessException e) {
            showAlert("Ошибка", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteComponent() {
        PreparationComponent selected = componentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите компонент для удаления");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setContentText("Удалить компонент #" + selected.getId() + "?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            componentManager.removeComponent(selected.getId());
            if (currentPreparation != null) {
                loadComponents(currentPreparation.getId());
            }
            showAlert("Успех", "Компонент удален");
        }
    }

    @FXML
    private void handleEditComponent() {
        PreparationComponent selected = componentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите компонент для редактирования");
            return;
        }
        try {
            Long newBatchId = null;
            Double newQty = null;
            ComponentUnit newUnit = null;

            if (!txtEditCompBatchId.getText().trim().isEmpty()) {
                newBatchId = Long.parseLong(txtEditCompBatchId.getText().trim());
                if (newBatchId < 1 || newBatchId > 100) {
                    showAlert("Ошибка", "ID партии должен быть от 1 до 100");
                    return;
                }
            }
            if (!txtEditCompQty.getText().trim().isEmpty()) {
                newQty = Double.parseDouble(txtEditCompQty.getText().trim());
            }
            if (cmbEditCompUnit.getValue() != null) {
                newUnit = cmbEditCompUnit.getValue();
            }
            componentManager.removeComponent(selected.getId());
            componentManager.addComponent(
                    currentPreparation.getId(),
                    newBatchId != null ? newBatchId : selected.getBatchId(),
                    newQty != null ? newQty : selected.getQuantity(),
                    newUnit != null ? newUnit : selected.getUnit()
            );

            loadComponents(currentPreparation.getId());

            // Очищаем поля
            txtEditCompBatchId.clear();
            txtEditCompQty.clear();
            cmbEditCompUnit.setValue(null);

            showAlert("Успех", "Компонент обновлен");
        }catch (NumberFormatException e) {
            showAlert("Ошибка", "ID партии и количество должны быть числами");
        } catch (IllegalArgumentException | IllegalAccessException e) {
            showAlert("Ошибка", e.getMessage());
        }
    }


    //общие команды:
    @FXML
    private void handleRefresh() {
        refreshSolutions();
        clearDetails();
        preparations.clear();
        components.clear();
        currentSolution = null;
        currentPreparation = null;
        showAlert("Инфо", "Данные обновлены");
    }

    @FXML
    private void handleSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить данные");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files", "*.json")
        );
        fileChooser.setInitialFileName("data.json");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            String filePath = file.getAbsolutePath();
            mainApp.saveData(filePath);
            showAlert("Успех", "Данные сохранены в: " + filePath);
        }
    }

    @FXML
    private void handleLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить данные");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files", "*.json")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String filePath = file.getAbsolutePath();
            boolean success = mainApp.loadData(filePath);
            if (success) {
                refreshSolutions();
                showAlert("Успех", "Данные загружены из: " + filePath);
        }
    }
    }


    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}