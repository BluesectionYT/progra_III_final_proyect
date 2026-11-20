package co.edu.uptc.view;

import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import co.edu.uptc.interfaces.IController;

public class LoginPanel extends JPanel {
    private IController controller;
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton createAccountButton;
    private JButton loginButton;
    private JLabel errorLabel;

    public LoginPanel(IController controller) {
        super(new GridBagLayout());
        this.controller = controller;
        setBackground(ViewUtils.BACKGROUND);
        initComponents();
    }

    private void initComponents() {
        add(createForm(), Cell.at(0, 0).center().build());
    }

    private JPanel createForm() {
        JPanel form = ViewUtils.createPanel(ViewUtils.BACKGROUND);

        JLabel title = new JLabel("Presupuesto de Proyecto", SwingConstants.CENTER);
        title.setFont(ViewUtils.TITLE_FONT);
        form.add(title, Cell.at(0, 0).span(2).center().insets(0, 5, 25, 5).build());

        userField = ViewUtils.createTextField();
        ViewUtils.addFormRow(form, 1, "Usuario", userField);

        passwordField = ViewUtils.createPasswordField();
        ViewUtils.addFormRow(form, 2, "Contraseña", passwordField);

        createAccountButton = ViewUtils.createButton("Crear cuenta");
        createAccountButton.addActionListener(e -> createAccount());
        loginButton = ViewUtils.createPrimaryButton("Ingresar");
        loginButton.addActionListener(e -> login());
        form.add(ViewUtils.createButtonBar(createAccountButton, loginButton), Cell.at(3, 0).span(2).growX().build());

        errorLabel = new JLabel("●  Usuario o contraseña incorrectos");
        errorLabel.setForeground(ViewUtils.ERROR);
        errorLabel.setVisible(false);
        form.add(errorLabel, Cell.at(4, 0).span(2).build());

        return form;
    }

    private void login() {
        errorLabel.setVisible(false);
        if (hasCredentials()) {
            controller.login(getUser(), getPassword());
        }
    }

    private void createAccount() {
        errorLabel.setVisible(false);
        if (hasCredentials()) {
            controller.register(getUser(), getPassword());
        }
    }

    private boolean hasCredentials() {
        if (userField.getText().isBlank() || passwordField.getPassword().length == 0) {
            showError("Ingresa usuario y contraseña");
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
        showError("Usuario o contraseña incorrectos");
    }

    public void showError(String message) {
        errorLabel.setText("●  " + message);
        errorLabel.setVisible(true);
    }
}
