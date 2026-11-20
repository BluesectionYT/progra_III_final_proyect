package co.edu.uptc.view.panels;

import co.edu.uptc.view.components.ViewUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.util.List;

public class SummaryPanel extends JPanel {
    private JLabel balanceLabel;
    private JLabel spentLabel;
    private DefaultTableModel categoryModel;
    private DefaultTableModel memberModel;

    public SummaryPanel() {
        this.setLayout(null);
        this.setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        balanceLabel();
        spentLabel();
        categoryTable();
        memberTable();
    }

    private void balanceLabel() {
        balanceLabel = new JLabel("Saldo disponible: ");
        balanceLabel.setFont(ViewUtils.HIGHLIGHT_FONT);
        balanceLabel.setBounds(20, 15, 600, 35);
        add(balanceLabel);
    }

    private void spentLabel() {
        spentLabel = new JLabel("Total gastado: ");
        spentLabel.setBounds(20, 52, 600, 25);
        add(spentLabel);
    }

    private void categoryTable() {
        categoryModel = ViewUtils.createTableModel("Categoría", "Presupuesto", "Gastado", "Uso");
        JPanel categoryPanel = ViewUtils.createTitledPanel("Consumo por categoría", 15, 90, 400, 640);
        categoryPanel.add(ViewUtils.createTableScroll(categoryModel, 10, 25, 380, 600));
        add(categoryPanel);
    }

    private void memberTable() {
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
