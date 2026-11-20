package co.edu.uptc.view.panels;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.components.ViewUtils;
import co.edu.uptc.view.frames.MainFrame;

import javax.swing.*;

public class RegisterPanel extends JPanel {
    private MainFrame frame;
    private IController controller;
    private JLabel title;
    private JTextField userField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JButton backButton;
    private JButton createButton;
    private JLabel errorLabel;

    public RegisterPanel(MainFrame frame, IController controller) {
        this.frame = frame;
        this.controller = controller;
        this.setLayout(null);
        this.setBackground(ViewUtils.BACKGROUND);
        initComponents();
    }

    private void initComponents() {
        title();
        userField();
        passwordField();
        confirmField();
        backButton();
        createButton();
        errorLabel();
    }

    private void title() {
        title = new JLabel("Crear cuenta", SwingConstants.CENTER);
        title.setFont(ViewUtils.TITLE_FONT);
        title.setBounds(0, 230, 900, 40);
        add(title);
    }

    private void userField() {
        add(ViewUtils.createLabel("Usuario", 230, 310, 140));
        userField = ViewUtils.createTextField(380, 310, 250);
        add(userField);
    }

    private void passwordField() {
        add(ViewUtils.createLabel("Contraseña", 230, 350, 140));
        passwordField = ViewUtils.createPasswordField(380, 350, 250);
        add(passwordField);
    }

    private void confirmField() {
        add(ViewUtils.createLabel("Confirmar contraseña", 230, 390, 150));
        confirmField = ViewUtils.createPasswordField(380, 390, 250);
        add(confirmField);
    }

    private void backButton() {
        backButton = ViewUtils.createButton("Volver", 380, 440, 120, 28);
        backButton.addActionListener(e -> frame.showLogin());
        add(backButton);
    }

    private void createButton() {
        createButton = ViewUtils.createPrimaryButton("Crear cuenta", 510, 440, 120, 30);
        createButton.addActionListener(e -> createAccount());
        add(createButton);
    }

    private void errorLabel() {
        errorLabel = new JLabel();
        errorLabel.setForeground(ViewUtils.ERROR);
        errorLabel.setBounds(380, 480, 400, 28);
        errorLabel.setVisible(false);
        add(errorLabel);
    }

    private void createAccount() {
        errorLabel.setVisible(false);
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        if (userField.getText().isBlank() || password.isEmpty()) {
            showError(ErrorMessages.CREDENTIALS_REQUIRED.getText());
        } else if (!password.equals(confirm)) {
            showError(ErrorMessages.PASSWORDS_DIFFER.getText());
        } else {
            controller.register(userField.getText(), password);
            frame.showLogin();
        }
    }

    public void showError(String message) {
        errorLabel.setText("●  " + message);
        errorLabel.setVisible(true);
    }
}
