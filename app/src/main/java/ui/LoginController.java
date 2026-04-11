package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import manager.UserManager;
import storage.LabData;
import storage.FileStorage;
import java.io.IOException;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserManager userManager = new UserManager();
    private MainApp mainApp;
    private FileStorage fileStorage = new FileStorage();
    private static final String USERS_FILE = "users.json";

    @FXML
    public void initialize() {
        loadUsers();
    }

    @FXML
    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        if (userManager.login(login, password)) {
            mainApp.setUserManager(userManager);
            mainApp.openMainWindow();
        } else {
            errorLabel.setText("Неверный логин или пароль");
        }
    }

    @FXML
    private void handleRegister() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        if (userManager.register(login, password)) {
            saveUsers();
            errorLabel.setText("Регистрация успешна! Теперь войдите.");
            loginField.clear();
            passwordField.clear();
        } else {
            errorLabel.setText("Логин уже занят");
        }
    }

    private void loadUsers() {
        try {
            LabData data = fileStorage.load(USERS_FILE);
            if (data.getUsers() != null) {
                userManager.setAll(data.getUsers());
                System.out.println("Загружено пользователей: " + data.getUsers().size());
            }
        } catch (IOException e) {
            System.out.println("Файл users.json не найден");
        }
    }

    private void saveUsers() {
        try {
            LabData data = new LabData();
            data.setUsers(userManager.getAllUsers());
            fileStorage.save(data, USERS_FILE);
            System.out.println("Пользователи сохранены");
        } catch (IOException e) {
            errorLabel.setText("Ошибка сохранения");
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
