package co.edu.uptc.view;

import java.awt.GridBagLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

public class NotificationsPanel extends JPanel {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private DefaultTableModel notificationsModel;
    private JButton clearButton;

    public NotificationsPanel() {
        super(new GridBagLayout());
        ViewUtils.styleAsTab(this);
        initComponents();
    }

    private void initComponents() {
        notificationsModel = ViewUtils.createTableModel("Fecha", "Mensaje");
        add(ViewUtils.createTablePanel("Notificaciones", notificationsModel, 140, 700), Cell.at(0, 0).grow().build());

        clearButton = ViewUtils.createButton("Limpiar");
        clearButton.addActionListener(e -> notificationsModel.setRowCount(0));
        add(ViewUtils.createButtonBar(clearButton), Cell.at(1, 0).growX().build());
    }

    public void addNotification(String message) {
        notificationsModel.insertRow(0, new Object[] { LocalDateTime.now().format(TIME_FORMAT), message });
    }
}
