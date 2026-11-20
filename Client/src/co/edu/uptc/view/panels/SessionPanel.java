package co.edu.uptc.view.panels;

import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.components.ViewUtils;
import co.edu.uptc.view.frames.MainFrame;

import javax.swing.*;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public abstract class SessionPanel extends JPanel {
    public static final String TAB_NOTIFICATIONS = "Notificaciones";
    private static final int RIGHT_EDGE = 865;

    private JTabbedPane tabs;
    private List<JButton> headerButtonList;
    private NotificationsPanel notificationsPanel;

    protected SessionPanel(MainFrame frame, IController controller, String headerText) {
        this.headerButtonList = new ArrayList<>();
        this.setLayout(null);
        this.setBackground(ViewUtils.BACKGROUND);
        initHeader(controller, headerText);
        initTabs();
    }

    private void initHeader(IController controller, String headerText) {
        JLabel headerLabel = new JLabel(headerText);
        headerLabel.setBounds(20, 10, 520, 28);
        add(headerLabel);
        JButton logoutButton = ViewUtils.createButton("Cerrar sesión", 0, 0, 120, 28);
        logoutButton.addActionListener(e -> controller.logout());
        headerButtonList.add(logoutButton);
        rebuildHeaderButtons();
    }

    private void initTabs() {
        tabs = new JTabbedPane();
        tabs.setBounds(20, 50, 845, 780);
        notificationsPanel = new NotificationsPanel();
        tabs.addTab(TAB_NOTIFICATIONS, notificationsPanel);
        tabs.addChangeListener(e -> notifyTabSelected());
        add(tabs);
    }

    private void notifyTabSelected() {
        int index = tabs.getSelectedIndex();
        if (index >= 0) {
            onTabSelected(tabs.getTitleAt(index));
        }
    }

    private void rebuildHeaderButtons() {
        int right = RIGHT_EDGE;
        for (int i = headerButtonList.size() - 1; i >= 0; i--) {
            JButton button = headerButtonList.get(i);
            int width = Math.max(120, button.getPreferredSize().width + 20);
            right -= width;
            button.setBounds(right, 10, width, 28);
            add(button);
            right -= 10;
        }
        repaint();
    }

    protected void addHeaderButton(JButton button) {
        headerButtonList.add(0, button);
        rebuildHeaderButtons();
    }

    protected void addTab(String title, JComponent content) {
        tabs.insertTab(title, null, content, null, tabs.getTabCount() - 1);
        if (tabs.getTabCount() == 2) {
            tabs.setSelectedIndex(0);
        }
    }

    protected void setTabEnabled(String title, boolean enabled) {
        int index = tabs.indexOfTab(title);
        if (index >= 0) {
            tabs.setEnabledAt(index, enabled);
        }
    }

    public void addNotification(String message) {
        notificationsPanel.addNotification(message);
    }

    protected void onTabSelected(String title) {
    }

    public <T> T findTab(Class<T> type) {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            Component component = tabs.getComponentAt(i);
            if (type.isInstance(component)) {
                return type.cast(component);
            }
        }
        return null;
    }
}
