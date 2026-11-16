package co.edu.uptc.view;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RequestsPanel extends JPanel {
    private JTextField conceptField;
    private JTextField amountField;
    private JComboBox<String> categoryCombo;

    public RequestsPanel() {
        setLayout(null);
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        JPanel newPanel = ViewUtils.createTitledPanel("Nueva solicitud", 15, 15, 815, 200);

        JLabel conceptLabel = new JLabel("Concepto");
        conceptLabel.setBounds(20, 30, 100, 28);
        newPanel.add(conceptLabel);
        conceptField = new JTextField();
        conceptField.setBounds(130, 30, 260, 28);
        newPanel.add(conceptField);

        JLabel amountLabel = new JLabel("Monto ($)");
        amountLabel.setBounds(20, 70, 100, 28);
        newPanel.add(amountLabel);
        amountField = new JTextField();
        amountField.setBounds(130, 70, 260, 28);
        newPanel.add(amountField);

        JLabel categoryLabel = new JLabel("Categoría");
        categoryLabel.setBounds(20, 110, 100, 28);
        newPanel.add(categoryLabel);
        categoryCombo = new JComboBox<>();
        categoryCombo.setBounds(130, 110, 260, 28);
        categoryCombo.setBackground(Color.WHITE);
        newPanel.add(categoryCombo);

        JButton sendButton = ViewUtils.createPrimaryButton("Enviar solicitud", 665, 150, 130, 30);
        newPanel.add(sendButton);
        add(newPanel);

        JPanel myRequestsPanel = ViewUtils.createTitledPanel("Mis solicitudes", 15, 230, 815, 480);
        myRequestsPanel.add(ViewUtils.createTable(10, 25, 795, 440,"#", "Fecha", "Concepto", "Categoría", "Monto", "Estado", "Motivo"));
        add(myRequestsPanel);
    }
}