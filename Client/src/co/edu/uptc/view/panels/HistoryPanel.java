package co.edu.uptc.view.panels;

import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.utills.Cell;
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
        super(new GridBagLayout());
        this.controller = controller;
        ViewUtils.styleAsTab(this);
        initComponents();
    }

    private void initComponents() {
        add(createFiltersPanel(), Cell.at(0, 0).growX().build());
        historyModel = ViewUtils.createTableModel("#", "Fecha", "Integrante", "Concepto", "Categoría", "Monto", "Estado", "Motivo");
        add(ViewUtils.createTablePanel("Historial de gastos", historyModel), Cell.at(1, 0).grow().build());
    }

    private JPanel createFiltersPanel() {
        JPanel panel = ViewUtils.createTitledPanel("Filtros y orden");
        categoryCombo = ViewUtils.createComboBox("Todas");
        ViewUtils.addLabeledField(panel, 0, 0, "Categoría", categoryCombo);
        statusCombo = ViewUtils.createComboBox("Todos", "Pendiente", "Aprobada", "Rechazada");
        ViewUtils.addLabeledField(panel, 0, 2, "Estado", statusCombo);
        orderCombo = ViewUtils.createComboBox("Monto (mayor a menor)", "Fecha de registro", "Fecha de aprobación", "Integrante solicitante");
        ViewUtils.addLabeledField(panel, 0, 4, "Ordenar por", orderCombo);
        fromField = createDateField();
        ViewUtils.addLabeledField(panel, 1, 0, "Desde", fromField);
        toField = createDateField();
        ViewUtils.addLabeledField(panel, 1, 2, "Hasta", toField);
        applyButton = ViewUtils.createPrimaryButton("Aplicar");
        applyButton.addActionListener(e -> applyFilters());
        panel.add(applyButton, Cell.at(1, 4).span(2).east().build());
        return panel;
    }

    private JTextField createDateField() {
        JTextField field = ViewUtils.createTextField();
        field.setColumns(1);
        field.setToolTipText(DATE_HINT);
        return field;
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
