package co.edu.uptc.view;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import co.edu.uptc.interfaces.IController;

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
        addTab(TAB_SUMMARY, new SummaryPanel());
        addTab(TAB_REQUESTS, new RequestsPanel(controller));
        addTab(TAB_APPROVAL, new ApprovalPanel(controller));
        addTab(TAB_HISTORY, new HistoryPanel(controller));
        addTab(TAB_TEAM, new TeamPanel(frame, controller));
        addTab(TAB_CIRCLE, new CirclePanel(controller, false));
        applyRole();
    }

    private void applyRole() {
        boolean admin = role == ProjectRole.ADMIN;
        setTabEnabled(TAB_REQUESTS, !admin);
        setTabEnabled(TAB_APPROVAL, admin);
        setTabEnabled(TAB_TEAM, admin);
        if (!admin) {
            addLeaveButton();
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
            frame.showProject();
        }
    }

    public void addAndStorePanel(String panelName, JPanel panel){
        storedPanels.put(panelName, panel);
        this.add(panel, panelName);
    }

    public JPanel getStoredPanel(String panelName){
        return storedPanels.get(panelName);
    }
}
