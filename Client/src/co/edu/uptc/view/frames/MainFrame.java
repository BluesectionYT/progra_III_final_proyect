package co.edu.uptc.view.frames;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.*;

import co.edu.uptc.enums.ProjectRole;
import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.panels.*;
import co.edu.uptc.view.utills.ViewUtils;

public class MainFrame extends JFrame {
    private IController controller;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;

    public MainFrame(IController controller) {
        this.controller = controller;
        setTitle("Gestión de Presupuesto");
        setSize(900, 900);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.sendExit();
                controller.close(null);
            }
        });
        setLocationRelativeTo(null);
        showLogin();
        setVisible(true);
    }

    public void showLogin() {
        loginPanel = new LoginPanel(this ,controller);
        showPanel(loginPanel);
    }

    public void showProject() {
        showPanel(new ProjectPanel(this, controller, getUser()));
        controller.getCircle();
        controller.getInvitations();
    }

    public void showAdmin(String user, String projectName) {
        showWorkspace(user, projectName, ProjectRole.ADMIN);
    }

    public void showMember(String user, String projectName) {
        showWorkspace(user, projectName, ProjectRole.MEMBER);
    }

    public void showWorkspace(String user, String projectName, ProjectRole role) {
        showPanel(new WorkspacePanel(this, controller, user, projectName, role));
        controller.getProjectInfo();
        controller.getSummary();
        controller.getHistory("Todas", "Todos", "", "", "Fecha de registro");
        controller.getCircle();
        if (role == ProjectRole.MEMBER) {
            controller.getMyRequests();
        }else{
            controller.getPending();
            controller.getTeam();
        }
    }

    private void showPanel(JPanel panel) {
        setContentPane(panel);
        revalidate();
        repaint();
    }

    public void addNotification(String message) {
        if (getContentPane() instanceof SessionPanel) {
            ((SessionPanel) getContentPane()).addNotification(message);
        }
    }

    public void setCircle(List<String> members) {
        if (getContentPane() instanceof SessionPanel) {
            CirclePanel panel = ((SessionPanel) getContentPane()).findTab(CirclePanel.class);
            if (panel != null) {
                panel.setCircle(members);
            }
        }
    }

    public void setInvitations(List<String> invitations) {
        if (getContentPane() instanceof SessionPanel) {
            CirclePanel panel = ((SessionPanel) getContentPane()).findTab(CirclePanel.class);
            if (panel != null) {
                panel.setInvitations(invitations);
            }
        }
    }

    private <T> T findPanel(Class<T> type) {
        if (getContentPane() instanceof SessionPanel) {
            return ((SessionPanel) getContentPane()).findTab(type);
        }
        return null;
    }

    public void setProjectInfo(List<String> categories, String balance) {
        RequestsPanel requests = findPanel(RequestsPanel.class);
        if (requests != null) {
            requests.setCategories(categories);
        }
        HistoryPanel history = findPanel(HistoryPanel.class);
        if (history != null) {
            history.setCategories(categories);
        }
        ApprovalPanel approval = findPanel(ApprovalPanel.class);
        if (approval != null) {
            approval.setBalance(balance);
        }
    }

    public void setSummary(String balance, String spent, List<Object[]> categoryRows, List<Object[]> memberRows) {
        SummaryPanel summary = findPanel(SummaryPanel.class);
        if (summary != null) {
            summary.setBalance(balance);
            summary.setSpent(spent);
            summary.setCategories(categoryRows);
            summary.setMembers(memberRows);
        }
    }

    public void setHistory(List<Object[]> rows) {
        HistoryPanel history = findPanel(HistoryPanel.class);
        if (history != null) {
            history.setHistory(rows);
        }
    }

    public void setMyRequests(List<Object[]> rows) {
        RequestsPanel requests = findPanel(RequestsPanel.class);
        if (requests != null) {
            requests.setRequests(rows);
        }
    }

    public void requestCreated(List<Object[]> rows) {
        RequestsPanel requests = findPanel(RequestsPanel.class);
        if (requests != null) {
            requests.clearForm();
            requests.setRequests(rows);
        }
    }

    public void setPending(String balance, List<Object[]> rows) {
        ApprovalPanel approval = findPanel(ApprovalPanel.class);
        if (approval != null) {
            approval.setBalance(balance);
            approval.setPending(rows);
        }
    }

    public void requestResolved(String balance, List<Object[]> rows) {
        ApprovalPanel approval = findPanel(ApprovalPanel.class);
        if (approval != null) {
            approval.setBalance(balance);
            approval.setPending(rows);
            approval.clearReason();
        }
    }

    public void setTeam(List<String> members, List<String> candidates) {
        TeamPanel team = findPanel(TeamPanel.class);
        if (team != null) {
            team.setMembers(members);
            team.setInviteCandidates(candidates);
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

    public void showError(String message) {
        if (getContentPane() instanceof RegisterPanel) {
            registerPanel.showError(message);
        } else {
            loginPanel.showError(message);
        }
    }
    public void showGeneralError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showRegister() {
        registerPanel = new RegisterPanel(this, controller);
        showPanel(registerPanel);
    }

    public void accountCreated() {
        showLogin();
        ViewUtils.showMessage(this, "Cuenta creada. Ya puedes ingresar.");
    }
}
