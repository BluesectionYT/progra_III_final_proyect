package co.edu.uptc.view.panels;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.frames.MainFrame;
import co.edu.uptc.view.utills.ViewUtils;

public class LoginPanel extends JPanel {
    private IController controller;
    private MainFrame frame;
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton createAccountButton;
    private JButton loginButton;
    private JLabel errorLabel;

    public LoginPanel(MainFrame frame, IController controller) {
        setLayout(null);
        this.controller = controller;
        this.frame = frame;
        setBackground(ViewUtils.BACKGROUND);
        initComponents();
    }

    private void initComponents() {
        JLabel title = new JLabel("Presupuesto de Proyecto", SwingConstants.CENTER);
        title.setFont(ViewUtils.TITLE_FONT);
        title.setBounds(0, 250, 900, 40);
        add(title);

        add(ViewUtils.createLabel("Usuario", 280, 330, 80));
        userField = ViewUtils.createTextField(370, 330, 250);
        add(userField);

        add(ViewUtils.createLabel("Contraseña", 280, 370, 80));
        passwordField = ViewUtils.createPasswordField(370, 370, 250);
        add(passwordField);

        createAccountButton = ViewUtils.createButton("Crear cuenta", 370, 420, 120, 28);
        createAccountButton.addActionListener(e -> frame.showRegister());
        add(createAccountButton);

        loginButton = ViewUtils.createPrimaryButton("Ingresar", 500, 420, 120, 30);
        loginButton.addActionListener(e -> login());
        add(loginButton);

        errorLabel = new JLabel("●  Usuario o contraseña incorrectos");
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
