package co.edu.uptc.view;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class HistoryPanel extends JPanel{
    private JComboBox<String> categoryCombo;
    private JComboBox<String> statusCombo;
    private JTextField fromField;
    private JTextField toField;
    private JComboBox<String> orderCombo;
    private JButton applyButton;

    public HistoryPanel() {
        setLayout(null);
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents(){
        add(createLabel("Categoría", 15, 15, 70));
        categoryCombo = new JComboBox<>();
        categoryCombo.setBackground(Color.WHITE);
        categoryCombo.setBounds(90, 15, 130, 28);
        add(categoryCombo);

        add(createLabel("Estado", 235, 15, 50));
        statusCombo = new JComboBox<>();
        statusCombo.setBackground(Color.WHITE);
        statusCombo.setBounds(290, 15, 130, 28);
        add(statusCombo);

        add(createLabel("Desde", 435, 15, 45));
        fromField = new JTextField();
        fromField.setBounds(485, 15, 110, 28);
        add(fromField);

        add(createLabel("Hasta", 610, 15, 45));
        toField = new JTextField();
        toField.setBounds(660, 15, 110, 28);
        add(toField);

        add(createLabel("Ordenar por", 15, 55, 80));
        orderCombo = new JComboBox<>();
        orderCombo.setBackground(Color.WHITE);
        orderCombo.setBounds(100, 55, 230, 28);
        add(orderCombo);

        applyButton = ViewUtils.createPrimaryButton("Aplicar", 345, 55, 100, 28);
        add(applyButton);

        add(ViewUtils.createTable(15, 100, 815, 620,"#", "Fecha", "Integrante", "Concepto", "Categoría", "Monto", "Estado", "Motivo"));
    }

    private JLabel createLabel(String text, int x, int y, int width) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, 28);
        return label;
    }
}