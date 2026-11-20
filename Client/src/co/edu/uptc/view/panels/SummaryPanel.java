package co.edu.uptc.view.panels;

import java.awt.Color;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import co.edu.uptc.view.utills.ViewUtils;

public class SummaryPanel extends JPanel {
    private JLabel balanceLabel;
    private JLabel spentLabel;
    private DefaultTableModel categoryModel;
    private DefaultTableModel memberModel;

    public SummaryPanel() {
        setLayout(null);
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        initTotals();
        initTables();
    }

    private void initTotals() {
        balanceLabel = new JLabel("Saldo disponible: ");
        balanceLabel.setFont(ViewUtils.HIGHLIGHT_FONT);
        balanceLabel.setBounds(20, 15, 600, 35);
        add(balanceLabel);

        spentLabel = new JLabel("Total gastado: ");
        spentLabel.setBounds(20, 52, 600, 25);
        add(spentLabel);
    }

    private void initTables() {
        categoryModel = ViewUtils.createTableModel("Categoría", "Presupuesto", "Gastado", "Uso");
        JPanel categoryPanel = ViewUtils.createTitledPanel("Consumo por categoría", 15, 90, 400, 640);
        categoryPanel.add(ViewUtils.createTableScroll(categoryModel, 10, 25, 380, 600));
        add(categoryPanel);

        memberModel = ViewUtils.createTableModel("Integrante", "Gasto acumulado");
        JPanel memberPanel = ViewUtils.createTitledPanel("Gasto por integrante", 430, 90, 400, 640);
        memberPanel.add(ViewUtils.createTableScroll(memberModel, 10, 25, 380, 600));
        add(memberPanel);
    }

    public void setBalance(String balance) {
        balanceLabel.setText("Saldo disponible: " + balance);
    }

    public void setSpent(String spent) {
        spentLabel.setText("Total gastado: " + spent);
    }

    public void setCategories(List<Object[]> rows) {
        ViewUtils.setRows(categoryModel, rows);
    }

    public void setMembers(List<Object[]> rows) {
        ViewUtils.setRows(memberModel, rows);
    }
}
