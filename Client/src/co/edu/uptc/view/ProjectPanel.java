package co.edu.uptc.view;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class ProjectPanel extends JPanel{
    private MainFrame frame;
    private String user;
    private JLabel userLabel;
    private JButton logoutButton;
    private JTabbedPane tabs;
    private JTextField nameField;
    private JTextField budgetField;
    private JTextField categoriesField;

    public ProjectPanel(MainFrame frame, String user){
        this.frame = frame;
        this.user = user;
        setLayout(null);
        setBackground(new Color(235, 239, 245));
        initComponents();
    }

    private void initComponents(){
        userLabel = new JLabel(user + " · Sin proyecto");
        userLabel.setBounds(20, 10, 300, 28);
        add(userLabel);

        logoutButton = new JButton("Cerrar sesión");
        logoutButton.setBounds(745, 10, 120, 28);
        logoutButton.addActionListener(e -> frame.showLogin());
        add(logoutButton);

        tabs = new JTabbedPane();
        tabs.setBounds(20, 50, 845, 780);
        tabs.addTab("Proyecto", createProjectTab());
        tabs.addTab("Círculo e invitaciones", new CirclePanel());
        add(tabs);
    }

    private JPanel createProjectTab(){
        JPanel tab = new JPanel(null);
        tab.setBackground(Color.WHITE);

        JLabel info = new JLabel("Aún no participas en un proyecto. Crea uno o acepta una invitación en «Círculo e invitaciones».");
        info.setBounds(20, 20, 750, 25);
        tab.add(info);

        JLabel nameLabel = new JLabel("Nombre del proyecto");
        nameLabel.setBounds(20, 70, 180, 28);
        tab.add(nameLabel);
        nameField = new JTextField();
        nameField.setBounds(210, 70, 450, 28);
        tab.add(nameField);

        JLabel budgetLabel = new JLabel("Presupuesto total ($)");
        budgetLabel.setBounds(20, 115, 180, 28);
        tab.add(budgetLabel);
        budgetField = new JTextField();
        budgetField.setBounds(210, 115, 450, 28);
        tab.add(budgetField);

        JLabel categoriesLabel = new JLabel("Categorías (Nombre:monto, ...)");
        categoriesLabel.setBounds(20, 160, 190, 28);
        tab.add(categoriesLabel);
        categoriesField = new JTextField();
        categoriesField.setBounds(210, 160, 450, 28);
        tab.add(categoriesField);

        JButton createButton = ViewUtils.createPrimaryButton("Crear proyecto", 680, 680, 130, 30);
        createButton.addActionListener(e -> frame.showAdmin(user, nameField.getText()));
        tab.add(createButton);

        return tab;
    }
}
