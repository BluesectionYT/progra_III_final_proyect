package co.edu.uptc.view.panels;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.components.ViewUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.util.List;

public class ApprovalPanel extends JPanel {
    private static final String NO_ERROR = " ";
    private static final String BALANCE_TEXT = "Solicitudes pendientes en orden de llegada (FIFO). Saldo disponible: ";

    private IController controller;
    private JLabel infoLabel;
    private DefaultTableModel pendingModel;
    private JTable pendingTable;
    private JTextField reasonField;
    private JLabel reasonErrorLabel;
    private JButton rejectButton;
    private JButton approveButton;

    public ApprovalPanel(IController controller) {
        this.controller = controller;
        this.setLayout(null);
        this.setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        infoLabel();
        pendingTable();
        reasonField();
        rejectButton();
        approveButton();
    }

    private void infoLabel() {
        infoLabel = new JLabel(BALANCE_TEXT);
        infoLabel.setBounds(15, 15, 800, 25);
        add(infoLabel);
    }

    private void pendingTable() {
        pendingModel = ViewUtils.createTableModel("#", "Fecha", "Integrante", "Concepto", "Categoría", "Monto");
        pendingTable = ViewUtils.createTable(pendingModel);
        add(ViewUtils.createScroll(pendingTable, 15, 50, 815, 560));
    }

    private void reasonField() {
        add(ViewUtils.createLabel("Motivo", 15, 620, 60));
        reasonField = ViewUtils.createTextField(80, 620, 500);
        add(reasonField);
        reasonErrorLabel = new JLabel(NO_ERROR);
        reasonErrorLabel.setForeground(ViewUtils.ERROR);
        reasonErrorLabel.setBounds(80, 650, 500, 22);
        add(reasonErrorLabel);
    }

    private void rejectButton() {
        rejectButton = ViewUtils.createButton("Rechazar", 605, 690, 105, 30);
        rejectButton.addActionListener(e -> decide(false));
        add(rejectButton);
    }

    private void approveButton() {
        approveButton = ViewUtils.createPrimaryButton("Aprobar", 720, 690, 110, 30);
        approveButton.addActionListener(e -> decide(true));
        add(approveButton);
    }

    private void decide(boolean approve) {
        int row = pendingTable.getSelectedRow();
        if (row < 0) {
            ViewUtils.showMessage(this, ErrorMessages.SELECT_REQUEST.getText());
            return;
        }
        if (validateReason()) {
            sendDecision(selectedId(row), reasonField.getText().trim(), approve);
        }
    }

    private int selectedId(int row) {
        return Integer.parseInt(pendingModel.getValueAt(pendingTable.convertRowIndexToModel(row), 0).toString());
    }

    private void sendDecision(int id, String reason, boolean approve) {
        if (approve) {
            controller.approveRequest(id, reason);
        } else {
            controller.rejectRequest(id, reason);
        }
    }

    private boolean validateReason() {
        boolean valid = !reasonField.getText().isBlank();
        reasonErrorLabel.setText(valid ? NO_ERROR : ErrorMessages.REASON_REQUIRED.getText());
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
