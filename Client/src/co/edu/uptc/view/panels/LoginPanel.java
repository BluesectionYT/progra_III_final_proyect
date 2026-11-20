package co.edu.uptc.view.panels;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.components.ViewUtils;
import co.edu.uptc.view.frames.MainFrame;

import javax.swing.*;

public class LoginPanel extends JPanel {
    private IController controller;
    private MainFrame frame;
    private JLabel title;
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton createAccountButton;
    private JButton loginButton;
    private JLabel errorLabel;

    public LoginPanel(MainFrame frame, IController controller) {
        this.controller = controller;
        this.frame = frame;
        this.setLayout(null);
        this.setBackground(ViewUtils.BACKGROUND);
        initComponents();
    }

    private void initComponents() {
        title();
        userField();
        passwordField();
        createAccountButton();
        loginButton();
        errorLabel();
    }

    private void title() {
        title = new JLabel("Presupuesto de Proyecto", SwingConstants.CENTER);
        title.setFont(ViewUtils.TITLE_FONT);
        title.setBounds(0, 250, 900, 40);
        add(title);
    }

    private void userField() {
        add(ViewUtils.createLabel("Usuario", 280, 330, 80));
        userField = ViewUtils.createTextField(370, 330, 250);
        add(userField);
    }

    private void passwordField() {
        add(ViewUtils.createLabel("Contraseña", 280, 370, 80));
        passwordField = ViewUtils.createPasswordField(370, 370, 250);
        add(passwordField);
    }

    private void createAccountButton() {
        createAccountButton = ViewUtils.createButton("Crear cuenta", 370, 420, 120, 28);
        createAccountButton.addActionListener(e -> frame.showRegister());
        add(createAccountButton);
    }

    private void loginButton() {
        loginButton = ViewUtils.createPrimaryButton("Ingresar", 500, 420, 120, 30);
        loginButton.addActionListener(e -> login());
        add(loginButton);
    }

    private void errorLabel() {
        errorLabel = new JLabel("●  " + ErrorMessages.INVALID_CREDENTIALS.getText());
        errorLabel.setForeground(ViewUtils.ERROR);
        errorLabel.setBounds(370, 460, 400, 28);
        errorLabel.setVisible(false);
        add(errorLabel);
    }

    private void login() {
        errorLabel.setVisible(false);
        if (hasCredentials()) {
            controller.login(getUser(), getPassword());
        }
    }

    private boolean hasCredentials() {
        if (userField.getText().isBlank() || passwordField.getPassword().length == 0) {
            showError(ErrorMessages.CREDENTIALS_REQUIRED.getText());
            return false;
        }
        return true;
    }

    public String getUser() {
        return userField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void showError() {
        showError(ErrorMessages.INVALID_CREDENTIALS.getText());
    }

    public void showError(String message) {
        errorLabel.setText("●  " + message);
        errorLabel.setVisible(true);
    }
}
