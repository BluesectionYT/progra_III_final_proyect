package co.edu.uptc.view.panels;

import java.awt.Color;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.utills.Cell;
import co.edu.uptc.view.utills.ViewUtils;
import co.edu.uptc.view.frames.MainFrame;

public class ProjectPanel extends SessionPanel {
    private IController controller;
    private MainFrame frame;
    private String user;
    private JTextField nameField;
    private JTextField budgetField;
    private JTextField categoriesField;
    private Map<String, Double> categories;

    public ProjectPanel(MainFrame frame,IController controller, String user) {
        super(frame, controller, user + " · Sin proyecto");
        this.frame = frame;
        this.controller = controller;
        this.user = user;
        initComponents();
    }

    private void initComponents() {
        addTab("Proyecto", createProjectTab());
        addTab("Círculo e invitaciones", new CirclePanel(controller, true));
    }

    @Override
    protected void onTabSelected(String title) {
        if (controller == null) {
            return;
        }
        if ("Círculo e invitaciones".equals(title)) {
            controller.getCircle();
            controller.getInvitations();
        }
    }

    private JPanel createProjectTab() {
        JPanel tab = ViewUtils.createPanel(Color.WHITE);
        ViewUtils.styleAsTab(tab);
        tab.add(ViewUtils.createLabel("Aún no participas en un proyecto. Crea uno o acepta una invitación en «Círculo e invitaciones»."),
                Cell.at(0, 0).span(2).insets(5, 5, 20, 5).build());
        nameField = ViewUtils.createTextField();
        ViewUtils.addFormRow(tab, 1, "Nombre del proyecto", nameField);
        budgetField = ViewUtils.createTextField();
        ViewUtils.addFormRow(tab, 2, "Presupuesto total ($)", budgetField);
        categoriesField = ViewUtils.createTextField();
        ViewUtils.addFormRow(tab, 3, "Categorías (Nombre:monto, ...)", categoriesField);
        ViewUtils.addFiller(tab, 4, 2);
        tab.add(createActionBar(), Cell.at(5, 0).span(2).growX().build());
        return tab;
    }

    private JPanel createActionBar() {
        JButton createButton = ViewUtils.createPrimaryButton("Crear proyecto");
        createButton.addActionListener(e -> createProject());
        return ViewUtils.createButtonBar(createButton);
    }

    private void createProject() {
        if (!validateForm()) {
            return;
        }
        String password = ViewUtils.askPassword(this, "Confirma tu contraseña para crear el proyecto");
        if (password != null) {
            double budget = ViewUtils.parseAmount(budgetField.getText());
            controller.createProject(nameField.getText().trim(), budget, categories, password);
        }
    }

    private boolean validateForm() {
        if (nameField.getText().isBlank()) {
            return invalid("El nombre del proyecto es obligatorio.");
        }
        Double budget = ViewUtils.parseAmount(budgetField.getText());
        if (budget == null) {
            return invalid("El presupuesto total debe ser un número mayor que cero.");
        }
        return validateCategories(budget);
    }

    private boolean validateCategories(double budget) {
        if (categoriesField.getText().isBlank()) {
            return invalid("Ingresa al menos una categoría con el formato Nombre:monto.");
        }
        Set<String> names = new HashSet<>();
        categories = new LinkedHashMap<>();
        double total = 0;
        for (String item : categoriesField.getText().split(",")) {
            String[] parts = item.split(":");
            Double amount = parts.length == 2 ? ViewUtils.parseAmount(parts[1]) : null;
            if (amount == null || parts[0].isBlank()) {
                return invalid("Categoría inválida: «" + item.trim() + "». Usa el formato Nombre:monto.");
            }
            if (!names.add(parts[0].trim().toLowerCase())) {
                return invalid("La categoría «" + parts[0].trim() + "» está repetida.");
            }
            categories.put(parts[0].trim(), amount);
            total += amount;
        }
        if (total > budget) {
            return invalid("La suma de las categorías supera el presupuesto total.");
        }
        return true;
    }

    private boolean invalid(String message){
        ViewUtils.showMessage(this, message);
        return false;
    }
}
