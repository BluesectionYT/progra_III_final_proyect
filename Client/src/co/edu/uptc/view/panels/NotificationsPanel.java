package co.edu.uptc.view.panels;

import co.edu.uptc.view.components.ViewUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationsPanel extends JPanel {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private DefaultTableModel notificationsModel;
    private JButton clearButton;

    public NotificationsPanel() {
        this.setLayout(null);
        this.setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        notificationsTable();
        clearButton();
    }

    private void notificationsTable() {
        notificationsModel = ViewUtils.createTableModel("Fecha", "Mensaje");
        JPanel tablePanel = ViewUtils.createTitledPanel("Notificaciones", 15, 15, 815, 660);
        tablePanel.add(ViewUtils.createTableScroll(notificationsModel, 10, 25, 795, 620, 140, 700));
        add(tablePanel);
    }

    private void clearButton() {
        clearButton = ViewUtils.createButton("Limpiar", 730, 690, 100, 28);
        clearButton.addActionListener(e -> notificationsModel.setRowCount(0));
        add(clearButton);
    }

    public void addNotification(String message) {
        notificationsModel.insertRow(0, new Object[]{LocalDateTime.now().format(TIME_FORMAT), message});
    }
}
