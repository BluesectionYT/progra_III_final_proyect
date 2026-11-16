package co.edu.uptc.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SummaryPanel extends JPanel{
    private JLabel balanceLabel;
    private JLabel spentLabel;

    public SummaryPanel(){
        setLayout(null);
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents(){
        balanceLabel = new JLabel("Saldo disponible: ");
        balanceLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        balanceLabel.setBounds(20, 15, 600, 35);
        add(balanceLabel);

        spentLabel = new JLabel("Total gastado: ");
        spentLabel.setBounds(20, 52, 600, 25);
        add(spentLabel);

        JPanel categoryPanel = ViewUtils.createTitledPanel("Consumo por categoría", 15, 90, 400, 640);
        categoryPanel.add(ViewUtils.createTable(10, 25, 380, 600, "Categoría", "Presupuesto", "Gastado", "Uso"));
        add(categoryPanel);

        JPanel memberPanel = ViewUtils.createTitledPanel("Gasto por integrante", 430, 90, 400, 640);
        memberPanel.add(ViewUtils.createTable(10, 25, 380, 600, "Integrante", "Gasto acumulado"));
        add(memberPanel);
    }
}
