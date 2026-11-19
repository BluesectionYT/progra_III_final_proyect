package co.edu.uptc.view;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ApprovalPanel extends JPanel {
    private static final String NO_ERROR = " ";
    private static final String BALANCE_TEXT = "Solicitudes pendientes en orden de llegada (FIFO). Saldo disponible: ";
    private static final String REASON_REQUIRED = "●  El motivo es obligatorio";

    private JLabel infoLabel;
    private DefaultTableModel pendingModel;
    private JTable pendingTable;
    private JTextField reasonField;
    private JLabel reasonErrorLabel;
    private JButton rejectButton;
    private JButton approveButton;

    public ApprovalPanel() {
        super(new GridBagLayout());
        ViewUtils.styleAsTab(this);
        initComponents();
    }

    private void initComponents() {
        infoLabel = new JLabel(BALANCE_TEXT);
        add(infoLabel, Cell.at(0, 0).growX().build());
        pendingModel = ViewUtils.createTableModel("#", "Fecha", "Integrante", "Concepto", "Categoría", "Monto");
        pendingTable = ViewUtils.createTable(pendingModel);
        add(ViewUtils.createScroll(pendingTable), Cell.at(1, 0).grow().build());
        add(createReasonRow(), Cell.at(2, 0).growX().build());
        add(createActionBar(), Cell.at(3, 0).growX().build());
    }

    private JPanel createReasonRow() {
        JPanel row = ViewUtils.createPanel(Color.WHITE);
        reasonField = ViewUtils.createTextField();
        ViewUtils.addFormRow(row, 0, "Motivo", reasonField);
        reasonErrorLabel = new JLabel(NO_ERROR);
        reasonErrorLabel.setForeground(ViewUtils.ERROR);
        row.add(reasonErrorLabel, Cell.at(1, 1).build());
        return row;
    }

    private JPanel createActionBar() {
        rejectButton = ViewUtils.createButton("Rechazar");
        rejectButton.addActionListener(e -> decide(false));
        approveButton = ViewUtils.createPrimaryButton("Aprobar");
        approveButton.addActionListener(e -> decide(true));
        return ViewUtils.createButtonBar(rejectButton, approveButton);
    }

    private void decide(boolean approve) {
        if (pendingTable.getSelectedRow() < 0) {
            ViewUtils.showMessage(this, "Selecciona una solicitud de la lista.");
            return;
        }
        if (validateReason()) {
            // El controller enviará APPROVE_REQUEST o REJECT_REQUEST con el # de la solicitud y el motivo.
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
