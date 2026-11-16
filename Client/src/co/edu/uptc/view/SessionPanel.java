package co.edu.uptc.view;

import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public abstract class SessionPanel extends JPanel {
    private JTabbedPane tabs;

    protected SessionPanel(MainFrame frame, String headerText) {
        super(new GridBagLayout());
        setBackground(ViewUtils.BACKGROUND);
        initHeader(frame, headerText);
        initTabs();
    }

    private void initHeader(MainFrame frame, String headerText) {
        add(ViewUtils.createLabel(headerText), Cell.at(0, 0).growX().insets(10, 20, 5, 5).build());

        JButton logoutButton = ViewUtils.createButton("Cerrar sesión");
        logoutButton.addActionListener(e -> frame.showLogin());
        add(logoutButton, Cell.at(0, 1).east().insets(10, 5, 5, 20).build());
    }

    private void initTabs() {
        tabs = new JTabbedPane();
        add(tabs, Cell.at(1, 0).span(2).grow().insets(5, 20, 20, 20).build());
    }

    protected void addTab(String title, JComponent content) {
        tabs.addTab(title, content);
    }
}
