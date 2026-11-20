package co.edu.uptc.view.panels;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import co.edu.uptc.view.utills.ViewUtils;

public class NotificationsPanel extends JPanel {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private DefaultTableModel notificationsModel;
    private JButton clearButton;

    public NotificationsPanel() {
        setLayout(null);
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        notificationsModel = ViewUtils.createTableModel("Fecha", "Mensaje");
        JPanel tablePanel = ViewUtils.createTitledPanel("Notificaciones", 15, 15, 815, 660);
        tablePanel.add(ViewUtils.createTableScroll(notificationsModel, 10, 25, 795, 620, 140, 700));
        add(tablePanel);

        clearButton = ViewUtils.createButton("Limpiar", 730, 690, 100, 28);
        clearButton.addActionListener(e -> notificationsModel.setRowCount(0));
        add(clearButton);
    }

    public void addNotification(String message) {
        notificationsModel.insertRow(0, new Object[] { LocalDateTime.now().format(TIME_FORMAT), message });
    }
}
