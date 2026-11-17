package co.edu.uptc.view;

import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ApprovalPanel extends JPanel {
    private static final String NO_ERROR = " ";
    private static final String REASON_REQUIRED = "●  El motivo es obligatorio";

    private JLabel infoLabel;
    private DefaultTableModel pendingModel;
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
        infoLabel = new JLabel("Solicitudes pendientes en orden de llegada (FIFO). Saldo disponible: ");
        add(infoLabel, Cell.at(0, 0).growX().build());

        pendingModel = ViewUtils.createTableModel("#", "Fecha", "Integrante", "Concepto", "Categoría", "Monto");
        add(ViewUtils.createTableScroll(pendingModel), Cell.at(1, 0).grow().build());

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
        rejectButton.addActionListener(e -> validateReason());

        approveButton = ViewUtils.createPrimaryButton("Aprobar");
        approveButton.addActionListener(e -> validateReason());

        return ViewUtils.createButtonBar(rejectButton, approveButton);
    }

    /** El motivo es obligatorio tanto al aprobar como al rechazar. */
    private boolean validateReason() {
        boolean valid = !reasonField.getText().isBlank();
        reasonErrorLabel.setText(valid ? NO_ERROR : REASON_REQUIRED);
        if (!valid) {
            reasonField.requestFocusInWindow();
        }
        return valid;
    }
}
