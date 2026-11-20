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
import co.edu.uptc.view.frames.MainFrame;
import co.edu.uptc.view.utills.ViewUtils;

public class ProjectPanel extends SessionPanel {
    private IController controller;
    private MainFrame frame;
    private String user;
    private JTextField nameField;
    private JTextField budgetField;
    private JTextField categoriesField;
    private Map<String, Double> categories;

    public ProjectPanel(MainFrame frame, IController controller, String user) {
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

        tab.add(ViewUtils.createLabel(
                "Aún no participas en un proyecto. Crea uno o acepta una invitación en «Círculo e invitaciones».",
                20, 20, 780));

        tab.add(ViewUtils.createLabel("Nombre del proyecto", 20, 70, 180));
        nameField = ViewUtils.createTextField(210, 70, 450);
        tab.add(nameField);

        tab.add(ViewUtils.createLabel("Presupuesto total ($)", 20, 115, 180));
        budgetField = ViewUtils.createTextField(210, 115, 450);
        tab.add(budgetField);

        tab.add(ViewUtils.createLabel("Categorías (Nombre:monto, ...)", 20, 160, 190));
        categoriesField = ViewUtils.createTextField(210, 160, 450);
        tab.add(categoriesField);

        JButton createButton = ViewUtils.createPrimaryButton("Crear proyecto", 680, 680, 130, 30);
        createButton.addActionListener(e -> createProject());
        tab.add(createButton);

        return tab;
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

    private boolean invalid(String message) {
        ViewUtils.showMessage(this, message);
        return false;
    }
}
