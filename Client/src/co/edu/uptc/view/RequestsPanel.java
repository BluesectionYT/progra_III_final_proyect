package co.edu.uptc.view;

import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class RequestsPanel extends JPanel {
    private JTextField conceptField;
    private JTextField amountField;
    private JComboBox<String> categoryCombo;
    private JButton sendButton;
    private DefaultTableModel requestsModel;

    public RequestsPanel() {
        super(new GridBagLayout());
        ViewUtils.styleAsTab(this);
        initComponents();
    }

    private void initComponents() {
        add(createNewRequestPanel(), Cell.at(0, 0).growX().build());

        requestsModel = ViewUtils.createTableModel("#", "Fecha", "Concepto", "Categoría", "Monto", "Estado", "Motivo");
        add(ViewUtils.createTablePanel("Mis solicitudes", requestsModel), Cell.at(1, 0).grow().build());
    }

    private JPanel createNewRequestPanel() {
        JPanel panel = ViewUtils.createTitledPanel("Nueva solicitud");

        conceptField = ViewUtils.createTextField();
        ViewUtils.addFormRow(panel, 0, "Concepto", conceptField);

        amountField = ViewUtils.createTextField();
        ViewUtils.addFormRow(panel, 1, "Monto ($)", amountField);

        categoryCombo = ViewUtils.createComboBox();
        ViewUtils.addFormRow(panel, 2, "Categoría", categoryCombo);

        sendButton = ViewUtils.createPrimaryButton("Enviar solicitud");
        sendButton.addActionListener(e -> sendRequest());
        panel.add(ViewUtils.createButtonBar(sendButton), Cell.at(3, 0).span(2).growX().build());
        return panel;
    }

    private void sendRequest() {
        if (conceptField.getText().isBlank()) {
            ViewUtils.showMessage(this, "El concepto es obligatorio.");
        } else if (ViewUtils.parseAmount(amountField.getText()) == null) {
            ViewUtils.showMessage(this, "El monto debe ser un número mayor que cero.");
        } else if (categoryCombo.getSelectedItem() == null) {
            ViewUtils.showMessage(this, "Selecciona una categoría.");
        } else {
            // El controller enviará CREATE_REQUEST con concepto, monto y categoría.
        }
    }

    public void clearForm() {
        conceptField.setText("");
        amountField.setText("");
    }

    public void setCategories(List<String> categories) {
        ViewUtils.setItems(categoryCombo, categories);
    }

    public void setRequests(List<Object[]> rows) {
        ViewUtils.setRows(requestsModel, rows);
    }
}
