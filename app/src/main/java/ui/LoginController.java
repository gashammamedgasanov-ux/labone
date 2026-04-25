package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import manager.UserManager;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserManager userManager;  // ← будет передан из MainApp
    private MainApp mainApp;

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @FXML
    public void initialize() {
        // Ничего не загружаем - UserManager уже загрузил из БД
        System.out.println("LoginController initialized");
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
            errorLabel.setText("Регистрация успешна! Теперь войдите.");
            loginField.clear();
            passwordField.clear();
        } else {
            errorLabel.setText("Логин уже занят");
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
