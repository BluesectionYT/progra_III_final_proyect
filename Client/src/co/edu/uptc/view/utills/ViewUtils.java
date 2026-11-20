package co.edu.uptc.view.utills;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
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

    private static final int TEXT_FIELD_COLUMNS = 20;
    private static final Dimension LIST_PREFERRED_SIZE = new Dimension(300, 200);
    private static final int TAB_MARGIN = 15;
    private static final int PANEL_MARGIN = 5;

    public static JPanel createPanel(Color background) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(background);
        return panel;
    }

    public static void styleAsTab(JPanel panel) {
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(TAB_MARGIN, TAB_MARGIN, TAB_MARGIN, TAB_MARGIN));
    }

    public static JPanel createTitledPanel(String title) {
        JPanel panel = createPanel(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(PANEL_MARGIN, PANEL_MARGIN, PANEL_MARGIN, PANEL_MARGIN)));
        return panel;
    }

    public static JPanel createTablePanel(String title, DefaultTableModel model, int... columnWidths) {
        JPanel panel = createTitledPanel(title);
        panel.add(createTableScroll(model, columnWidths), Cell.at(0, 0).grow().build());
        return panel;
    }

    public static JPanel createListPanel(String title, JList<String> list, int columns) {
        JPanel panel = createTitledPanel(title);
        panel.add(createListScroll(list), Cell.at(0, 0).span(columns).grow().build());
        return panel;
    }

    public static JPanel createListPanel(String title, JList<String> list) {
        return createListPanel(title, list, 1);
    }


    public static DefaultTableModel createTableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static JScrollPane createTableScroll(DefaultTableModel model, int... columnWidths) {
        return createScroll(createTable(model, columnWidths));
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

    public static JScrollPane createScroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setColumnHeaderView(table.getTableHeader());
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(null);
        return scroll;
    }

    public static JScrollPane createListScroll(JList<String> list) {
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scroll.setPreferredSize(LIST_PREFERRED_SIZE);
        return scroll;
    }


    public static JLabel createLabel(String text) {
        return new JLabel(text);
    }

    public static JTextField createTextField() {
        return new JTextField(TEXT_FIELD_COLUMNS);
    }

    public static JPasswordField createPasswordField() {
        return new JPasswordField(TEXT_FIELD_COLUMNS);
    }

    public static JComboBox<String> createComboBox(String... items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setBackground(Color.WHITE);
        return combo;
    }

    public static JButton createButton(String text) {
        return new JButton(text);
    }

    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        return button;
    }

    public static String askPassword(Component parent, String message) {
        JPasswordField field = createPasswordField();
        field.addAncestorListener(new FocusOnShow(field));
        int option = JOptionPane.showConfirmDialog(parent, createPasswordPrompt(message, field),
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

    static JPanel createPasswordPrompt(String message, JPasswordField field) {
        JPanel prompt = new JPanel(new GridBagLayout());
        prompt.setOpaque(false);
        prompt.add(createLabel(message), Cell.at(0, 0).build());
        prompt.add(field, Cell.at(1, 0).growX().build());
        return prompt;
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

    public static void setRows(DefaultTableModel model, List<Object[]> rows){
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

    public static void addFormRow(JPanel panel, int row, String labelText, JComponent field) {
        panel.add(createLabel(labelText), Cell.at(row, 0).build());
        panel.add(field, Cell.at(row, 1).growX().build());
    }

    public static void addLabeledField(JPanel panel, int row, int column, String labelText, JComponent field) {
        panel.add(createLabel(labelText), Cell.at(row, column).build());
        panel.add(field, Cell.at(row, column + 1).growX().build());
    }

    public static JPanel createButtonBar(JButton... buttons) {
        JPanel bar = createPanel(Color.WHITE);
        bar.setOpaque(false);
        bar.add(Box.createHorizontalGlue(), Cell.at(0, 0).growX().build());
        for (int i = 0; i < buttons.length; i++) {
            bar.add(buttons[i], Cell.at(0, i + 1).build());
        }
        return bar;
    }

    public static void addFiller(JPanel panel, int row, int columns) {
        panel.add(Box.createVerticalGlue(), Cell.at(row, 0).span(columns).growY().build());
    }
}
