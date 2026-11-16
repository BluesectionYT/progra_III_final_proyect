package co.edu.uptc.view;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ApprovalPanel extends JPanel{
    private JLabel infoLabel;
    private JButton rejectButton;
    private JButton approveButton;

    public ApprovalPanel(){
        setLayout(null);
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents(){
        infoLabel = new JLabel("Solicitudes pendientes en orden de llegada (FIFO). Saldo disponible: ");
        infoLabel.setBounds(15, 15, 800, 25);
        add(infoLabel);

        add(ViewUtils.createTable(15, 50, 815, 620,"#", "Fecha", "Integrante", "Concepto", "Categoría", "Monto"));

        rejectButton = new JButton("Rechazar");
        rejectButton.setBounds(605, 690, 105, 30);
        add(rejectButton);

        approveButton = ViewUtils.createPrimaryButton("Aprobar", 720, 690, 110, 30);
        add(approveButton);
    }
}
