package co.edu.uptc.view.panels;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import co.edu.uptc.enums.ProjectRole;
import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.utills.ViewUtils;
import co.edu.uptc.view.frames.MainFrame;

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
    private Map<String, JPanel> storedPanels;
    private SummaryPanel summaryPanel;
    private RequestsPanel requestsPanel;
    private ApprovalPanel approvalPanel;
    private HistoryPanel historyPanel;
    private TeamPanel teamPanel;
    private CirclePanel circlePanel;

    public WorkspacePanel(MainFrame frame, IController controller, String user, String projectName, ProjectRole role) {
        super(frame,controller, user + " · " + role.getLabel() + " de «" + projectName + "»");
        this.frame = frame;
        this.controller = controller;
        this.projectName = projectName;
        this.role = role;
        this.storedPanels = new HashMap<>();
        initComponents();
    }

    private void initComponents() {
        this.summaryPanel = new SummaryPanel();
        addTab(TAB_SUMMARY, summaryPanel);
        this.requestsPanel = new RequestsPanel(controller);
        addTab(TAB_REQUESTS, requestsPanel);
        this.approvalPanel = new ApprovalPanel(controller);
        addTab(TAB_APPROVAL, approvalPanel);
        this.historyPanel = new HistoryPanel(controller);
        addTab(TAB_HISTORY, historyPanel);
        this.teamPanel = new TeamPanel(frame, controller);
        addTab(TAB_TEAM, teamPanel);
        this.circlePanel = new CirclePanel(controller, false);
        addTab(TAB_CIRCLE, circlePanel);
        applyRole();
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
        switch (title) {
            case TAB_SUMMARY:
                controller.getSummary();
                break;
            case TAB_REQUESTS:
                controller.getMyRequests();
                break;
            case TAB_APPROVAL:
                controller.getPending();
                break;
            case TAB_TEAM:
                controller.getTeam();
                break;
            case TAB_CIRCLE:
                controller.getCircle();
                break;
        }
    }

    private void addLeaveButton() {
        JButton leaveButton = ViewUtils.createButton("Salir del proyecto");
        leaveButton.addActionListener(e -> leaveProject());
        addHeaderButton(leaveButton);
    }

    private void leaveProject() {
        int option = JOptionPane.showConfirmDialog(this, "¿Seguro que quieres salir de «" + projectName + "»?",
                "Salir del proyecto", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            controller.leaveProject();
        }
    }
}
