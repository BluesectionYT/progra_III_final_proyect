package co.edu.uptc.view.panels;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.components.ViewUtils;
import co.edu.uptc.view.frames.MainFrame;

import javax.swing.*;
import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProjectPanel extends SessionPanel {
    private static final String TAB_PROJECT = "Proyecto";
    private static final String TAB_CIRCLE = "Círculo e invitaciones";

    private IController controller;
    private MainFrame frame;
    private String user;
    private JPanel projectTab;
    private JTextField nameField;
    private JTextField budgetField;
    private JTextField categoriesField;
    private JButton createButton;
    private Map<String, Double> categories;

    public ProjectPanel(MainFrame frame, IController controller, String user) {
        super(frame, controller, user + " · Sin proyecto");
        this.frame = frame;
        this.controller = controller;
        this.user = user;
        initComponents();
    }

    private void initComponents() {
        projectTab();
        addTab(TAB_PROJECT, projectTab);
        addTab(TAB_CIRCLE, new CirclePanel(controller, true));
    }

    private void projectTab() {
        projectTab = ViewUtils.createPanel(Color.WHITE);
        projectTab.add(ViewUtils.createLabel(
                "Aún no participas en un proyecto. Crea uno o acepta una invitación en «Círculo e invitaciones».",
                20, 20, 780));
        nameField();
        budgetField();
        categoriesField();
        createButton();
    }

    private void nameField() {
        projectTab.add(ViewUtils.createLabel("Nombre del proyecto", 20, 70, 180));
        nameField = ViewUtils.createTextField(210, 70, 450);
        projectTab.add(nameField);
    }

    private void budgetField() {
        projectTab.add(ViewUtils.createLabel("Presupuesto total ($)", 20, 115, 180));
        budgetField = ViewUtils.createTextField(210, 115, 450);
        projectTab.add(budgetField);
    }

    private void categoriesField() {
        projectTab.add(ViewUtils.createLabel("Categorías (Nombre:monto, ...)", 20, 160, 190));
        categoriesField = ViewUtils.createTextField(210, 160, 450);
        projectTab.add(categoriesField);
    }

    private void createButton() {
        createButton = ViewUtils.createPrimaryButton("Crear proyecto", 680, 680, 130, 30);
        createButton.addActionListener(e -> createProject());
        projectTab.add(createButton);
    }

    @Override
    protected void onTabSelected(String title) {
        if (controller == null) {
            return;
        }
        if (TAB_CIRCLE.equals(title)) {
            controller.getCircle();
            controller.getInvitations();
        }
    }

    private void createProject() {
        if (!validateForm()) {
            return;
        }
        String password = ViewUtils.askPassword(this, ErrorMessages.CONFIRM_CREATE_PROJECT.getText());
        if (password != null) {
            double budget = ViewUtils.parseAmount(budgetField.getText());
            controller.createProject(nameField.getText().trim(), budget, categories, password);
        }
    }

    private boolean validateForm() {
        if (nameField.getText().isBlank()) {
            return invalid(ErrorMessages.PROJECT_NAME_REQUIRED.getText());
        }
        Double budget = ViewUtils.parseAmount(budgetField.getText());
        if (budget == null) {
            return invalid(ErrorMessages.BUDGET_INVALID.getText());
        }
        return validateCategories(budget);
    }

    private boolean validateCategories(double budget) {
        if (categoriesField.getText().isBlank()) {
            return invalid(ErrorMessages.CATEGORIES_REQUIRED.getText());
        }
        categories = new LinkedHashMap<>();
        for (String item : categoriesField.getText().split(",")) {
            if (!addCategory(item)) {
                return false;
            }
        }
        return validateTotal(budget);
    }

    private boolean addCategory(String item) {
        String[] parts = item.split(":");
        Double amount = parts.length == 2 ? ViewUtils.parseAmount(parts[1]) : null;
        if (amount == null || parts[0].isBlank()) {
            return invalid(ErrorMessages.CATEGORY_INVALID.format(item.trim()));
        }
        if (isRepeated(parts[0].trim())) {
            return invalid(ErrorMessages.CATEGORY_REPEATED.format(parts[0].trim()));
        }
        categories.put(parts[0].trim(), amount);
        return true;
    }

    private boolean isRepeated(String name) {
        return categories.keySet().stream().anyMatch(existing -> existing.toLowerCase().equals(name.toLowerCase()));
    }

    private boolean validateTotal(double budget) {
        double total = 0;
        for (double amount : categories.values()) {
            total += amount;
        }
        if (total > budget) {
            return invalid(ErrorMessages.CATEGORIES_EXCEED_BUDGET.getText());
        }
        return true;
    }

    private boolean invalid(String message) {
        ViewUtils.showMessage(this, message);
        return false;
    }
}
