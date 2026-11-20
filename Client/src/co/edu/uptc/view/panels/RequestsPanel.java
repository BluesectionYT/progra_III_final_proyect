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

public class RequestsPanel extends JPanel {
    private IController controller;
    private JTextField conceptField;
    private JTextField amountField;
    private JComboBox<String> categoryCombo;
    private JButton sendButton;
    private DefaultTableModel requestsModel;

    public RequestsPanel(IController controller) {
        setLayout(null);
        this.controller = controller;
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        add(createNewRequestPanel());

        requestsModel = ViewUtils.createTableModel("#", "Fecha", "Concepto", "Categoría", "Monto", "Estado", "Motivo");
        JPanel myRequestsPanel = ViewUtils.createTitledPanel("Mis solicitudes", 15, 230, 815, 480);
        myRequestsPanel.add(ViewUtils.createTableScroll(requestsModel, 10, 25, 795, 440));
        add(myRequestsPanel);
    }

    private JPanel createNewRequestPanel() {
        JPanel panel = ViewUtils.createTitledPanel("Nueva solicitud", 15, 15, 815, 200);

        panel.add(ViewUtils.createLabel("Concepto", 20, 30, 100));
        conceptField = ViewUtils.createTextField(130, 30, 260);
        panel.add(conceptField);

        panel.add(ViewUtils.createLabel("Monto ($)", 20, 70, 100));
        amountField = ViewUtils.createTextField(130, 70, 260);
        panel.add(amountField);

        panel.add(ViewUtils.createLabel("Categoría", 20, 110, 100));
        categoryCombo = ViewUtils.createComboBox(130, 110, 260);
        panel.add(categoryCombo);

        sendButton = ViewUtils.createPrimaryButton("Enviar solicitud", 665, 150, 130, 30);
        sendButton.addActionListener(e -> sendRequest());
        panel.add(sendButton);
        return panel;
    }

    private void sendRequest() {
        Double amount = ViewUtils.parseAmount(amountField.getText());
        if (conceptField.getText().isBlank()) {
            ViewUtils.showMessage(this, "El concepto es obligatorio.");
        } else if (amount == null) {
            ViewUtils.showMessage(this, "El monto debe ser un número mayor que cero.");
        } else if (categoryCombo.getSelectedItem() == null) {
            ViewUtils.showMessage(this, "Selecciona una categoría.");
        } else {
            controller.createRequest(conceptField.getText().trim(), amount, (String) categoryCombo.getSelectedItem());
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
