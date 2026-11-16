package co.edu.uptc.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import co.edu.uptc.controller.Controller;

public class MainFrame extends JFrame{
    private Controller controller;
    private JTextField userField;
    private JTextField passwordField;
    private JButton createAccountButton;
    private JButton loginButton;
    private JLabel errorLabel;


    public MainFrame(Controller presenter){
        this.controller = presenter;
        setTitle("Gestión de Presupuesto");
        setSize(900, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }

    private void initComponents(){
        getContentPane().setBackground(new Color(235, 239, 245));
        setLayout(null);

        JLabel title = new JLabel("Presupuesto de Proyecto", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.PLAIN, 28));
        title.setBounds(0, 250, 900, 40);
        add(title);

        JLabel userLabel = new JLabel("Usuario");
        userLabel.setBounds(280, 330, 80, 28);
        add(userLabel);

        userField = new JTextField();
        userField.setBounds(370, 330, 250, 28);
        add(userField);

        JLabel passwordLabel = new JLabel("Contraseña");
        passwordLabel.setBounds(280, 370, 80, 28);
        add(passwordLabel);

        passwordField = new JTextField();
        passwordField.setBounds(370, 370, 250, 28);
        add(passwordField);

        createAccountButton = new JButton("Crear cuenta");
        createAccountButton.setBounds(370, 420, 120, 28);
        add(createAccountButton);

        loginButton = ViewUtils.createPrimaryButton("Ingresar", 500, 420, 120, 30);
        loginButton.addActionListener(e -> showProject());
        add(loginButton);

        errorLabel = new JLabel("●  Usuario o contraseña incorrectos");
        errorLabel.setForeground(new Color(170, 0, 30));
        errorLabel.setBounds(370, 460, 250, 28);
        errorLabel.setVisible(false);
        add(errorLabel);
    }

    private void showProject(){
        setContentPane(new ProjectPanel(this, getUser()));
        revalidate();
        repaint();
    }

    public void showAdmin(String user, String projectName) {
        setContentPane(new AdminPanel(this, user, projectName));
        revalidate();
        repaint();
    }

    public void showLogin(){
        setContentPane(new JPanel());
        initComponents();
        revalidate();
        repaint();
    }

    public String getUser(){
        return userField.getText();
    }

    public String getPassword(){
        return passwordField.getText();
    }

    public void showError(){
        errorLabel.setVisible(true);
    }
}
