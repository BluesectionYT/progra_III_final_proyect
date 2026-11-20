package co.edu.uptc.view.panels;

import java.awt.Color;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.utills.ViewUtils;

public class ApprovalPanel extends JPanel {
    private static final String NO_ERROR = " ";
    private static final String BALANCE_TEXT = "Solicitudes pendientes en orden de llegada (FIFO). Saldo disponible: ";
    private static final String REASON_REQUIRED = "●  El motivo es obligatorio";

    private IController controller;
    private JLabel infoLabel;
    private DefaultTableModel pendingModel;
    private JTable pendingTable;
    private JTextField reasonField;
    private JLabel reasonErrorLabel;
    private JButton rejectButton;
    private JButton approveButton;

    public ApprovalPanel(IController controller) {
        setLayout(null);
        this.controller = controller;
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        infoLabel = new JLabel(BALANCE_TEXT);
        infoLabel.setBounds(15, 15, 800, 25);
        add(infoLabel);

        pendingModel = ViewUtils.createTableModel("#", "Fecha", "Integrante", "Concepto", "Categoría", "Monto");
        pendingTable = ViewUtils.createTable(pendingModel);
        add(ViewUtils.createScroll(pendingTable, 15, 50, 815, 560));

        add(ViewUtils.createLabel("Motivo", 15, 620, 60));
        reasonField = ViewUtils.createTextField(80, 620, 500);
        add(reasonField);
        reasonErrorLabel = new JLabel(NO_ERROR);
        reasonErrorLabel.setForeground(ViewUtils.ERROR);
        reasonErrorLabel.setBounds(80, 650, 500, 22);
        add(reasonErrorLabel);

        rejectButton = ViewUtils.createButton("Rechazar", 605, 690, 105, 30);
        rejectButton.addActionListener(e -> decide(false));
        add(rejectButton);

        approveButton = ViewUtils.createPrimaryButton("Aprobar", 720, 690, 110, 30);
        approveButton.addActionListener(e -> decide(true));
        add(approveButton);
    }

    private void decide(boolean approve) {
        int row = pendingTable.getSelectedRow();
        if (row < 0) {
            ViewUtils.showMessage(this, "Selecciona una solicitud de la lista.");
            return;
        }
        if (validateReason()) {
            int id = Integer.parseInt(pendingModel.getValueAt(pendingTable.convertRowIndexToModel(row), 0).toString());
            String reason = reasonField.getText().trim();
            if (approve) {
                controller.approveRequest(id, reason);
            } else {
                controller.rejectRequest(id, reason);
            }
        }
    }

    private boolean validateReason() {
        boolean valid = !reasonField.getText().isBlank();
        reasonErrorLabel.setText(valid ? NO_ERROR : REASON_REQUIRED);
        if (!valid) {
            reasonField.requestFocusInWindow();
        }
        return valid;
    }

    public void setBalance(String balance) {
        infoLabel.setText(BALANCE_TEXT + balance);
    }

    public void setPending(List<Object[]> rows) {
        ViewUtils.setRows(pendingModel, rows);
    }

    public void clearReason() {
        reasonField.setText("");
        reasonErrorLabel.setText(NO_ERROR);
    }
}
