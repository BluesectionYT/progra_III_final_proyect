package co.edu.uptc.view;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ProjectPanel extends SessionPanel {
    private MainFrame frame;
    private String user;
    private JTextField nameField;
    private JTextField budgetField;
    private JTextField categoriesField;

    public ProjectPanel(MainFrame frame, String user) {
        super(frame, user + " · Sin proyecto");
        this.frame = frame;
        this.user = user;
        initComponents();
    }

    private void initComponents() {
        addTab("Proyecto", createProjectTab());
        addTab("Círculo e invitaciones", new CirclePanel(true));
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
            // El controller enviará CREATE_PROJECT con los datos del formulario y la contraseña.
            frame.showAdmin(user, nameField.getText());
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
