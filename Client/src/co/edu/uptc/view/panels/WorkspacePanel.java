package co.edu.uptc.view.panels;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.enums.ProjectRole;
import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.components.ViewUtils;
import co.edu.uptc.view.frames.MainFrame;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class WorkspacePanel extends SessionPanel {
    private static final String TAB_SUMMARY = "Resumen";
    private static final String TAB_REQUESTS = "Solicitudes";
    private static final String TAB_APPROVAL = "Aprobación";
    private static final String TAB_HISTORY = "Historial";
    private static final String TAB_TEAM = "Equipo";
    private static final String TAB_CIRCLE = "Círculo";

    private IController controller;
    private MainFrame frame;
    private String projectName;
    private ProjectRole role;
    private Map<String, Runnable> tabActions;
    private SummaryPanel summaryPanel;
    private RequestsPanel requestsPanel;
    private ApprovalPanel approvalPanel;
    private HistoryPanel historyPanel;
    private TeamPanel teamPanel;
    private CirclePanel circlePanel;

    public WorkspacePanel(MainFrame frame, IController controller, String user, String projectName, ProjectRole role) {
        super(frame, controller, user + " · " + role.getLabel() + " de «" + projectName + "»");
        this.frame = frame;
        this.controller = controller;
        this.projectName = projectName;
        this.role = role;
        this.tabActions = new HashMap<>();
        initTabActions();
        initComponents();
    }

    private void initTabActions() {
        tabActions.put(TAB_SUMMARY, controller::getSummary);
        tabActions.put(TAB_REQUESTS, controller::getMyRequests);
        tabActions.put(TAB_APPROVAL, controller::getPending);
        tabActions.put(TAB_TEAM, controller::getTeam);
        tabActions.put(TAB_CIRCLE, controller::getCircle);
    }

    private void initComponents() {
        summaryPanel();
        requestsPanel();
        approvalPanel();
        historyPanel();
        teamPanel();
        circlePanel();
        applyRole();
    }

    private void summaryPanel() {
        summaryPanel = new SummaryPanel();
        addTab(TAB_SUMMARY, summaryPanel);
    }

    private void requestsPanel() {
        requestsPanel = new RequestsPanel(controller);
        addTab(TAB_REQUESTS, requestsPanel);
    }

    private void approvalPanel() {
        approvalPanel = new ApprovalPanel(controller);
        addTab(TAB_APPROVAL, approvalPanel);
    }

    private void historyPanel() {
        historyPanel = new HistoryPanel(controller);
        addTab(TAB_HISTORY, historyPanel);
    }

    private void teamPanel() {
        teamPanel = new TeamPanel(frame, controller);
        addTab(TAB_TEAM, teamPanel);
    }

    private void circlePanel() {
        circlePanel = new CirclePanel(controller, false);
        addTab(TAB_CIRCLE, circlePanel);
    }

    private void applyRole() {
        boolean admin = (role == ProjectRole.ADMIN);
        setTabEnabled(TAB_REQUESTS, !admin);
        setTabEnabled(TAB_APPROVAL, admin);
        setTabEnabled(TAB_TEAM, admin);
        if (!admin) {
            addLeaveButton();
        }
    }

    @Override
    protected void onTabSelected(String title) {
        if (controller == null) {
            return;
        }
        Runnable action = tabActions.get(title);
        if (action != null) {
            action.run();
        }
    }

    private void addLeaveButton() {
        JButton leaveButton = ViewUtils.createButton("Salir del proyecto", 0, 0, 140, 28);
        leaveButton.addActionListener(e -> leaveProject());
        addHeaderButton(leaveButton);
    }

    private void leaveProject() {
        int option = JOptionPane.showConfirmDialog(this, ErrorMessages.CONFIRM_LEAVE.format(projectName),
                "Salir del proyecto", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            controller.leaveProject();
        }
    }
}
