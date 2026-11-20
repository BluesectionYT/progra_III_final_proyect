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

public class RegisterPanel extends JPanel {
    private MainFrame frame;
    private IController controller;
    private JTextField userField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JLabel errorLabel;

    public RegisterPanel(MainFrame frame, IController controller) {
        setLayout(null);
        this.frame = frame;
        this.controller = controller;
        setBackground(ViewUtils.BACKGROUND);
        initComponents();
    }

    private void initComponents() {
        JLabel title = new JLabel("Crear cuenta", SwingConstants.CENTER);
        title.setFont(ViewUtils.TITLE_FONT);
        title.setBounds(0, 230, 900, 40);
        add(title);

        add(ViewUtils.createLabel("Usuario", 230, 310, 140));
        userField = ViewUtils.createTextField(380, 310, 250);
        add(userField);

        add(ViewUtils.createLabel("Contraseña", 230, 350, 140));
        passwordField = ViewUtils.createPasswordField(380, 350, 250);
        add(passwordField);

        add(ViewUtils.createLabel("Confirmar contraseña", 230, 390, 150));
        confirmField = ViewUtils.createPasswordField(380, 390, 250);
        add(confirmField);

        JButton backButton = ViewUtils.createButton("Volver", 380, 440, 120, 28);
        backButton.addActionListener(e -> frame.showLogin());
        add(backButton);

        JButton createButton = ViewUtils.createPrimaryButton("Crear cuenta", 510, 440, 120, 30);
        createButton.addActionListener(e -> createAccount());
        add(createButton);

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
            showError("Ingresa usuario y contraseña");
        } else if (!password.equals(confirm)) {
            showError("Las contraseñas no coinciden");
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
