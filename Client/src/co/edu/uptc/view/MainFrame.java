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

    /** Pantalla de un usuario sin proyecto: crear uno o aceptar una invitación. */
    public void showProject() {
        showPanel(new ProjectPanel(this, getUser()));
    }

    public void showAdmin(String user, String projectName) {
        showWorkspace(user, projectName, ProjectRole.ADMIN);
    }

    public void showMember(String user, String projectName) {
        showWorkspace(user, projectName, ProjectRole.MEMBER);
    }

    public void showWorkspace(String user, String projectName, ProjectRole role) {
        showPanel(new WorkspacePanel(this, user, projectName, role));
    }

    private void showPanel(JPanel panel) {
        setContentPane(panel);
        revalidate();
        repaint();
    }

    /** Agrega una notificación a la pantalla actual, si es una pantalla con sesión. */
    public void addNotification(String message) {
        if (getContentPane() instanceof SessionPanel) {
            ((SessionPanel) getContentPane()).addNotification(message);
        }
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
