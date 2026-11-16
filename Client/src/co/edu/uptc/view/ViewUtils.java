package co.edu.uptc.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
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

    private ViewUtils() {
    }

    // ---------- Paneles ----------

    public static JPanel createPanel(Color background) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(background);
        return panel;
    }

    /** Aplica a un panel el estilo común de las pestañas: fondo blanco y margen interior. */
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

    public static JPanel createTablePanel(String title, DefaultTableModel model) {
        JPanel panel = createTitledPanel(title);
        panel.add(createTableScroll(model), Cell.at(0, 0).grow().build());
        return panel;
    }

    /** Panel con título y una lista que ocupa {@code columns} columnas (para dejar controles debajo). */
    public static JPanel createListPanel(String title, JList<String> list, int columns) {
        JPanel panel = createTitledPanel(title);
        panel.add(createListScroll(list), Cell.at(0, 0).span(columns).grow().build());
        return panel;
    }

    public static JPanel createListPanel(String title, JList<String> list) {
        return createListPanel(title, list, 1);
    }

    // ---------- Tablas y listas ----------

    public static DefaultTableModel createTableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static JScrollPane createTableScroll(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(Color.WHITE);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
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

    // ---------- Componentes ----------

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

    // ---------- Distribución con GridBagLayout ----------

    /** Fila de formulario: etiqueta en la columna 0 y campo expandible en la columna 1. */
    public static void addFormRow(JPanel panel, int row, String labelText, JComponent field) {
        panel.add(createLabel(labelText), Cell.at(row, 0).build());
        panel.add(field, Cell.at(row, 1).growX().build());
    }

    /** Etiqueta en la columna indicada y campo en la siguiente (para filas con varios campos). */
    public static void addLabeledField(JPanel panel, int row, int column, String labelText, JComponent field) {
        panel.add(createLabel(labelText), Cell.at(row, column).build());
        panel.add(field, Cell.at(row, column + 1).growX().build());
    }

    /** Barra de botones alineada a la derecha (un espacio elástico a la izquierda). */
    public static JPanel createButtonBar(JButton... buttons) {
        JPanel bar = createPanel(Color.WHITE);
        bar.setOpaque(false);
        bar.add(Box.createHorizontalGlue(), Cell.at(0, 0).growX().build());
        for (int i = 0; i < buttons.length; i++) {
            bar.add(buttons[i], Cell.at(0, i + 1).build());
        }
        return bar;
    }

    /** Relleno invisible que empuja hacia arriba lo que está encima de él. */
    public static void addFiller(JPanel panel, int row, int columns) {
        panel.add(Box.createVerticalGlue(), Cell.at(row, 0).span(columns).growY().build());
    }
}
