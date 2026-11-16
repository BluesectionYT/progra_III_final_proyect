package co.edu.uptc.view;

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
        addTab("Círculo e invitaciones", new CirclePanel());
    }

    private JPanel createProjectTab() {
        JPanel tab = ViewUtils.createPanel(java.awt.Color.WHITE);
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
        createButton.addActionListener(e -> frame.showAdmin(user, nameField.getText()));
        return ViewUtils.createButtonBar(createButton);
    }
}
