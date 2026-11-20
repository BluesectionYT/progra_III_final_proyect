package co.edu.uptc.view.panels;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.components.ViewUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.util.List;

public class RequestsPanel extends JPanel {
    private IController controller;
    private JPanel newRequestPanel;
    private JTextField conceptField;
    private JTextField amountField;
    private JComboBox<String> categoryCombo;
    private JButton sendButton;
    private DefaultTableModel requestsModel;

    public RequestsPanel(IController controller) {
        this.controller = controller;
        this.setLayout(null);
        this.setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        newRequestPanel();
        requestsTable();
    }

    private void newRequestPanel() {
        newRequestPanel = ViewUtils.createTitledPanel("Nueva solicitud", 15, 15, 815, 200);
        conceptField();
        amountField();
        categoryCombo();
        sendButton();
        add(newRequestPanel);
    }

    private void conceptField() {
        newRequestPanel.add(ViewUtils.createLabel("Concepto", 20, 30, 100));
        conceptField = ViewUtils.createTextField(130, 30, 260);
        newRequestPanel.add(conceptField);
    }

    private void amountField() {
        newRequestPanel.add(ViewUtils.createLabel("Monto ($)", 20, 70, 100));
        amountField = ViewUtils.createTextField(130, 70, 260);
        newRequestPanel.add(amountField);
    }

    private void categoryCombo() {
        newRequestPanel.add(ViewUtils.createLabel("Categoría", 20, 110, 100));
        categoryCombo = ViewUtils.createComboBox(130, 110, 260);
        newRequestPanel.add(categoryCombo);
    }

    private void sendButton() {
        sendButton = ViewUtils.createPrimaryButton("Enviar solicitud", 665, 150, 130, 30);
        sendButton.addActionListener(e -> sendRequest());
        newRequestPanel.add(sendButton);
    }

    private void requestsTable() {
        requestsModel = ViewUtils.createTableModel("#", "Fecha", "Concepto", "Categoría", "Monto", "Estado", "Motivo");
        JPanel myRequestsPanel = ViewUtils.createTitledPanel("Mis solicitudes", 15, 230, 815, 480);
        myRequestsPanel.add(ViewUtils.createTableScroll(requestsModel, 10, 25, 795, 440));
        add(myRequestsPanel);
    }

    private void sendRequest() {
        Double amount = ViewUtils.parseAmount(amountField.getText());
        if (conceptField.getText().isBlank()) {
            ViewUtils.showMessage(this, ErrorMessages.CONCEPT_REQUIRED.getText());
        } else if (amount == null) {
            ViewUtils.showMessage(this, ErrorMessages.AMOUNT_INVALID.getText());
        } else if (categoryCombo.getSelectedItem() == null) {
            ViewUtils.showMessage(this, ErrorMessages.SELECT_CATEGORY.getText());
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
