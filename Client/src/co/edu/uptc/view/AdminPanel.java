package co.edu.uptc.view;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class AdminPanel extends JPanel{
    private MainFrame frame;
    private String user;
    private String projectName;

    public AdminPanel(MainFrame frame, String user, String projectName){
        this.frame = frame;
        this.user = user;
        this.projectName = projectName;
        setLayout(null);
        setBackground(new Color(235, 239, 245));
        initComponents();
    }

    private void initComponents(){
        JLabel userLabel = new JLabel(user + " · Administrador de «" + projectName + "»");
        userLabel.setBounds(20, 10, 500, 28);
        add(userLabel);

        JButton logoutButton = new JButton("Cerrar sesión");
        logoutButton.setBounds(745, 10, 120, 28);
        logoutButton.addActionListener(e -> frame.showLogin());
        add(logoutButton);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBounds(20, 50, 845, 780);
        tabs.add("Resumen", new SummaryPanel());
        tabs.add("Solicitudes", new RequestsPanel());
        tabs.add("Aprobación", new ApprovalPanel());
        tabs.addTab("Historial", new HistoryPanel());
        tabs.addTab("Equipo", new TeamPanel());
        add(tabs);
    }
}
