package co.edu.uptc.view;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import co.edu.uptc.controller.Controller;

public class MainFrame extends JFrame {
    private Controller controller;
    private LoginPanel loginPanel;

    public MainFrame(Controller presenter) {
        this.controller = presenter;
        setTitle("Gestión de Presupuesto");
        setSize(900, 900);
        setMinimumSize(new Dimension(800, 700));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        showLogin();
        setVisible(true);
    }

    public void showLogin() {
        loginPanel = new LoginPanel(this::showProject);
        showPanel(loginPanel);
    }

    private void showProject() {
        showPanel(new ProjectPanel(this, getUser()));
    }

    public void showAdmin(String user, String projectName) {
        showPanel(new AdminPanel(this, user, projectName));
    }

    private void showPanel(JPanel panel) {
        setContentPane(panel);
        revalidate();
        repaint();
    }

    public String getUser() {
        return loginPanel.getUser();
    }

    public String getPassword() {
        return loginPanel.getPassword();
    }

    public void showError() {
        loginPanel.showError();
    }
}
