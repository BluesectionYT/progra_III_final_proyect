package co.edu.uptc.view.panels;

import java.awt.Color;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.utills.ViewUtils;

public class HistoryPanel extends JPanel {
    private static final String DATE_HINT = "Formato AAAA-MM-DD";

    private IController controller;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> statusCombo;
    private JTextField fromField;
    private JTextField toField;
    private JComboBox<String> orderCombo;
    private JButton applyButton;
    private DefaultTableModel historyModel;

    public HistoryPanel(IController controller) {
        setLayout(null);
        this.controller = controller;
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        add(ViewUtils.createLabel("Categoría", 15, 15, 70));
        categoryCombo = ViewUtils.createComboBox(90, 15, 130, "Todas");
        add(categoryCombo);

        add(ViewUtils.createLabel("Estado", 235, 15, 50));
        statusCombo = ViewUtils.createComboBox(290, 15, 130, "Todos", "Pendiente", "Aprobada", "Rechazada");
        add(statusCombo);

        add(ViewUtils.createLabel("Desde", 435, 15, 45));
        fromField = ViewUtils.createTextField(485, 15, 110);
        fromField.setToolTipText(DATE_HINT);
        add(fromField);

        add(ViewUtils.createLabel("Hasta", 610, 15, 45));
        toField = ViewUtils.createTextField(660, 15, 110);
        toField.setToolTipText(DATE_HINT);
        add(toField);

        add(ViewUtils.createLabel("Ordenar por", 15, 55, 80));
        orderCombo = ViewUtils.createComboBox(100, 55, 230, "Monto (mayor a menor)", "Fecha de registro",
                "Fecha de aprobación", "Integrante solicitante");
        add(orderCombo);

        applyButton = ViewUtils.createPrimaryButton("Aplicar", 345, 55, 100, 28);
        applyButton.addActionListener(e -> applyFilters());
        add(applyButton);

        historyModel = ViewUtils.createTableModel("#", "Fecha", "Integrante", "Concepto", "Categoría", "Monto", "Estado",
                "Motivo");
        add(ViewUtils.createTableScroll(historyModel, 15, 100, 815, 620));
    }

    private void applyFilters() {
        String from = fromField.getText().trim();
        String to = toField.getText().trim();
        if (!isEmptyOrDate(from) || !isEmptyOrDate(to)) {
            ViewUtils.showMessage(this, "Las fechas deben tener el formato AAAA-MM-DD.");
        } else if (!from.isEmpty() && !to.isEmpty() && from.compareTo(to) > 0) {
            ViewUtils.showMessage(this, "La fecha «Desde» no puede ser posterior a «Hasta».");
        } else {
            controller.getHistory((String) categoryCombo.getSelectedItem(), (String) statusCombo.getSelectedItem(),
                    from, to, (String) orderCombo.getSelectedItem());
        }
    }

    private boolean isEmptyOrDate(String text) {
        return text.isEmpty() || ViewUtils.isValidDate(text);
    }

    public void setCategories(List<String> categories) {
        categoryCombo.removeAllItems();
        categoryCombo.addItem("Todas");
        categories.forEach(categoryCombo::addItem);
    }

    public void setHistory(List<Object[]> rows) {
        ViewUtils.setRows(historyModel, rows);
    }
}
