package co.edu.uptc.view.panels;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.components.ViewUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.util.List;

public class HistoryPanel extends JPanel {
    private static final String DATE_HINT = "Formato AAAA-MM-DD";
    private static final String ALL_CATEGORIES = "Todas";

    private IController controller;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> statusCombo;
    private JTextField fromField;
    private JTextField toField;
    private JComboBox<String> orderCombo;
    private JButton applyButton;
    private DefaultTableModel historyModel;

    public HistoryPanel(IController controller) {
        this.controller = controller;
        this.setLayout(null);
        this.setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        categoryCombo();
        statusCombo();
        fromField();
        toField();
        orderCombo();
        applyButton();
        historyTable();
    }

    private void categoryCombo() {
        add(ViewUtils.createLabel("Categoría", 15, 15, 70));
        categoryCombo = ViewUtils.createComboBox(90, 15, 130, ALL_CATEGORIES);
        add(categoryCombo);
    }

    private void statusCombo() {
        add(ViewUtils.createLabel("Estado", 235, 15, 50));
        statusCombo = ViewUtils.createComboBox(290, 15, 130, "Todos", "Pendiente", "Aprobada", "Rechazada");
        add(statusCombo);
    }

    private void fromField() {
        add(ViewUtils.createLabel("Desde", 435, 15, 45));
        fromField = ViewUtils.createTextField(485, 15, 110);
        fromField.setToolTipText(DATE_HINT);
        add(fromField);
    }

    private void toField() {
        add(ViewUtils.createLabel("Hasta", 610, 15, 45));
        toField = ViewUtils.createTextField(660, 15, 110);
        toField.setToolTipText(DATE_HINT);
        add(toField);
    }

    private void orderCombo() {
        add(ViewUtils.createLabel("Ordenar por", 15, 55, 80));
        orderCombo = ViewUtils.createComboBox(100, 55, 230, "Monto (mayor a menor)", "Fecha de registro",
                "Fecha de aprobación", "Integrante solicitante");
        add(orderCombo);
    }

    private void applyButton() {
        applyButton = ViewUtils.createPrimaryButton("Aplicar", 345, 55, 100, 28);
        applyButton.addActionListener(e -> applyFilters());
        add(applyButton);
    }

    private void historyTable() {
        historyModel = ViewUtils.createTableModel("#", "Fecha", "Integrante", "Concepto", "Categoría", "Monto",
                "Estado", "Motivo");
        add(ViewUtils.createTableScroll(historyModel, 15, 100, 815, 620));
    }

    private void applyFilters() {
        String from = fromField.getText().trim();
        String to = toField.getText().trim();
        if (!isEmptyOrDate(from) || !isEmptyOrDate(to)) {
            ViewUtils.showMessage(this, ErrorMessages.INVALID_DATES.getText());
        } else if (!from.isEmpty() && !to.isEmpty() && from.compareTo(to) > 0) {
            ViewUtils.showMessage(this, ErrorMessages.DATE_ORDER.getText());
        } else {
            requestHistory(from, to);
        }
    }

    private void requestHistory(String from, String to) {
        controller.getHistory((String) categoryCombo.getSelectedItem(), (String) statusCombo.getSelectedItem(),
                from, to, (String) orderCombo.getSelectedItem());
    }

    private boolean isEmptyOrDate(String text) {
        return text.isEmpty() || ViewUtils.isValidDate(text);
    }

    public void setCategories(List<String> categories) {
        categoryCombo.removeAllItems();
        categoryCombo.addItem(ALL_CATEGORIES);
        categories.forEach(categoryCombo::addItem);
    }

    public void setHistory(List<Object[]> rows) {
        ViewUtils.setRows(historyModel, rows);
    }
}
