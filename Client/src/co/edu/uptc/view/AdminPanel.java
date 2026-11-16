package co.edu.uptc.view;

public class AdminPanel extends SessionPanel {

    public AdminPanel(MainFrame frame, String user, String projectName) {
        super(frame, user + " · Administrador de «" + projectName + "»");
        initComponents();
    }

    private void initComponents() {
        addTab("Resumen", new SummaryPanel());
        addTab("Solicitudes", new RequestsPanel());
        addTab("Aprobación", new ApprovalPanel());
        addTab("Historial", new HistoryPanel());
        addTab("Equipo", new TeamPanel());
    }
}
