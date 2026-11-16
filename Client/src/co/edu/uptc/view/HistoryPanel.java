package co.edu.uptc.view;

import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class HistoryPanel extends JPanel {
    private static final String DATE_HINT = "Formato AAAA-MM-DD";

    private JComboBox<String> categoryCombo;
    private JComboBox<String> statusCombo;
    private JTextField fromField;
    private JTextField toField;
    private JComboBox<String> orderCombo;
    private JButton applyButton;
    private DefaultTableModel historyModel;

    public HistoryPanel() {
        super(new GridBagLayout());
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

        orderCombo = ViewUtils.createComboBox("Monto (mayor a menor)", "Fecha de registro",
                "Fecha de aprobación", "Integrante solicitante");
        ViewUtils.addLabeledField(panel, 0, 4, "Ordenar por", orderCombo);

        fromField = createDateField();
        ViewUtils.addLabeledField(panel, 1, 0, "Desde", fromField);

        toField = createDateField();
        ViewUtils.addLabeledField(panel, 1, 2, "Hasta", toField);

        applyButton = ViewUtils.createPrimaryButton("Aplicar");
        panel.add(applyButton, Cell.at(1, 4).span(2).east().build());
        return panel;
    }

    private JTextField createDateField() {
        JTextField field = ViewUtils.createTextField();
        field.setColumns(1);
        field.setToolTipText(DATE_HINT);
        return field;
    }
}
