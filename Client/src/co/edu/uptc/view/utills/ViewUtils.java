package co.edu.uptc.view.utills;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableModel;

public class ViewUtils {
    public static final Color BACKGROUND = new Color(235, 239, 245);
    public static final Color PRIMARY = new Color(46, 109, 180);
    public static final Color ERROR = new Color(170, 0, 30);
    public static final Font TITLE_FONT = new Font("SansSerif", Font.PLAIN, 28);
    public static final Font HIGHLIGHT_FONT = new Font("SansSerif", Font.PLAIN, 24);

    private static final int FIELD_HEIGHT = 28;

    public static JPanel createPanel(Color background) {
        JPanel panel = new JPanel(null);
        panel.setBackground(background);
        return panel;
    }

    public static JPanel createTitledPanel(String title, int x, int y, int width, int height) {
        JPanel panel = createPanel(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBounds(x, y, width, height);
        return panel;
    }

    public static DefaultTableModel createTableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static JTable createTable(DefaultTableModel model, int... columnWidths) {
        JTable table = new JTable(model);
        table.setBackground(Color.WHITE);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        for (int i = 0; i < columnWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        return table;
    }

    public static JScrollPane createScroll(JTable table, int x, int y, int width, int height) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setColumnHeaderView(table.getTableHeader());
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBounds(x, y, width, height);
        scroll.setBorder(null);
        return scroll;
    }

    public static JScrollPane createTableScroll(DefaultTableModel model, int x, int y, int width, int height,
            int... columnWidths) {
        return createScroll(createTable(model, columnWidths), x, y, width, height);
    }

    public static JScrollPane createListScroll(JList<String> list, int x, int y, int width, int height) {
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scroll.setBounds(x, y, width, height);
        return scroll;
    }

    public static JLabel createLabel(String text, int x, int y, int width) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, FIELD_HEIGHT);
        return label;
    }

    public static JTextField createTextField(int x, int y, int width) {
        JTextField field = new JTextField();
        field.setBounds(x, y, width, FIELD_HEIGHT);
        return field;
    }

    public static JPasswordField createPasswordField(int x, int y, int width) {
        JPasswordField field = new JPasswordField();
        field.setBounds(x, y, width, FIELD_HEIGHT);
        return field;
    }

    public static JComboBox<String> createComboBox(int x, int y, int width, String... items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setBackground(Color.WHITE);
        combo.setBounds(x, y, width, FIELD_HEIGHT);
        return combo;
    }

    public static JButton createButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        return button;
    }

    public static JButton createPrimaryButton(String text, int x, int y, int width, int height) {
        JButton button = createButton(text, x, y, width, height);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        return button;
    }

    public static String askPassword(Component parent, String message) {
        JPasswordField field = new JPasswordField(20);
        field.addAncestorListener(new FocusOnShow(field));
        int option = JOptionPane.showConfirmDialog(parent, new Object[] { message, field },
                "Confirmar contraseña", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return null;
        }
        String password = new String(field.getPassword());
        if (password.isEmpty()) {
            showMessage(parent, "La contraseña es obligatoria.");
            return null;
        }
        return password;
    }

    public static void showMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }

    private static class FocusOnShow implements AncestorListener {
        private final JComponent component;

        FocusOnShow(JComponent component) {
            this.component = component;
        }

        @Override
        public void ancestorAdded(AncestorEvent event) {
            component.requestFocusInWindow();
            component.removeAncestorListener(this);
        }

        @Override
        public void ancestorRemoved(AncestorEvent event) {
        }

        @Override
        public void ancestorMoved(AncestorEvent event) {
        }
    }

    public static void setRows(DefaultTableModel model, List<Object[]> rows) {
        model.setRowCount(0);
        for (Object[] row : rows) {
            model.addRow(row);
        }
    }

    public static void setItems(JComboBox<String> combo, List<String> items) {
        combo.removeAllItems();
        for (String item : items) {
            combo.addItem(item);
        }
    }

    public static void setItems(JList<String> list, List<String> items) {
        list.setListData(items.toArray(new String[0]));
    }

    public static Double parseAmount(String text) {
        try {
            double amount = Double.parseDouble(text.trim());
            return amount > 0 && !Double.isInfinite(amount) ? amount : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean isValidDate(String text) {
        try {
            LocalDate.parse(text.trim());
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
