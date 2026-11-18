package co.edu.uptc.view;

import javax.swing.JButton;
import javax.swing.JOptionPane;

public class WorkspacePanel extends SessionPanel {
    private static final String TAB_SUMMARY = "Resumen";
    private static final String TAB_REQUESTS = "Solicitudes";
    private static final String TAB_APPROVAL = "Aprobación";
    private static final String TAB_HISTORY = "Historial";
    private static final String TAB_TEAM = "Equipo";
    private static final String TAB_CIRCLE = "Círculo";

    private MainFrame frame;
    private String projectName;
    private ProjectRole role;

    public WorkspacePanel(MainFrame frame, String user, String projectName, ProjectRole role) {
        super(frame, user + " · " + role.getLabel() + " de «" + projectName + "»");
        this.frame = frame;
        this.projectName = projectName;
        this.role = role;
        initComponents();
    }

    private void initComponents() {
        addTab(TAB_SUMMARY, new SummaryPanel());
        addTab(TAB_REQUESTS, new RequestsPanel());
        addTab(TAB_APPROVAL, new ApprovalPanel());
        addTab(TAB_HISTORY, new HistoryPanel());
        addTab(TAB_TEAM, new TeamPanel(frame));
        addTab(TAB_CIRCLE, new CirclePanel(false));
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
            // El controller enviará LEAVE_PROJECT y, si el servidor responde ok, se vuelve a la pantalla sin proyecto.
            frame.showProject();
        }
    }
}