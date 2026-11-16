package co.edu.uptc.view;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ViewUtils {
    public static JPanel createTitledPanel(String title, int x, int y, int width, int height) {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBounds(x, y, width, height);
        return panel;
    }

    public static JScrollPane createTable(int x, int y, int width, int height, String... columns) {
        JTable table = new JTable(new DefaultTableModel(columns, 0));
        table.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBounds(x, y, width, height);
        scroll.setBorder(null);
        return scroll;
    }

    public static JButton createPrimaryButton(String text, int x, int y, int width, int height) {
    JButton button = new JButton(text);
    button.setBounds(x, y, width, height);
    button.setBackground(new Color(46, 109, 180));
    button.setForeground(Color.WHITE);
    return button;
}
}