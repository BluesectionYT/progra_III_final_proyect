package co.edu.uptc.view;

import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

public class ApprovalPanel extends JPanel {
    private JLabel infoLabel;
    private JButton rejectButton;
    private JButton approveButton;
    private DefaultTableModel pendingModel;

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

        rejectButton = ViewUtils.createButton("Rechazar");
        approveButton = ViewUtils.createPrimaryButton("Aprobar");
        add(ViewUtils.createButtonBar(rejectButton, approveButton), Cell.at(2, 0).growX().build());
    }
}
