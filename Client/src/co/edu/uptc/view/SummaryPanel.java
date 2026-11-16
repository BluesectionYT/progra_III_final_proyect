package co.edu.uptc.view;

import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

public class SummaryPanel extends JPanel {
    private JLabel balanceLabel;
    private JLabel spentLabel;
    private DefaultTableModel categoryModel;
    private DefaultTableModel memberModel;

    public SummaryPanel() {
        super(new GridBagLayout());
        ViewUtils.styleAsTab(this);
        initComponents();
    }

    private void initComponents() {
        initTotals();
        initTables();
    }

    private void initTotals() {
        balanceLabel = new JLabel("Saldo disponible: ");
        balanceLabel.setFont(ViewUtils.HIGHLIGHT_FONT);
        add(balanceLabel, Cell.at(0, 0).span(2).build());

        spentLabel = new JLabel("Total gastado: ");
        add(spentLabel, Cell.at(1, 0).span(2).build());
    }

    private void initTables() {
        categoryModel = ViewUtils.createTableModel("Categoría", "Presupuesto", "Gastado", "Uso");
        add(ViewUtils.createTablePanel("Consumo por categoría", categoryModel), Cell.at(2, 0).grow().build());

        memberModel = ViewUtils.createTableModel("Integrante", "Gasto acumulado");
        add(ViewUtils.createTablePanel("Gasto por integrante", memberModel), Cell.at(2, 1).grow().build());
    }
}
