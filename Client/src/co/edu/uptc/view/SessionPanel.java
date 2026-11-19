package co.edu.uptc.view;

import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import co.edu.uptc.interfaces.IController;

public abstract class SessionPanel extends JPanel {
    public static final String TAB_NOTIFICATIONS = "Notificaciones";

    private JTabbedPane tabs;
    private JPanel headerButtons;
    private List<JButton> headerButtonList = new ArrayList<>();
    private NotificationsPanel notificationsPanel;

    protected SessionPanel(MainFrame frame, IController controller, String headerText) {
        super(new GridBagLayout());
        setBackground(ViewUtils.BACKGROUND);
        initHeader(frame, controller, headerText);
        initTabs();
    }

    private void initHeader(MainFrame frame, IController controller, String headerText) {
        add(ViewUtils.createLabel(headerText), Cell.at(0, 0).growX().insets(10, 20, 5, 5).build());
        headerButtons = ViewUtils.createPanel(ViewUtils.BACKGROUND);
        headerButtons.setOpaque(false);
        add(headerButtons, Cell.at(0, 1).east().insets(10, 5, 5, 20).build());
        JButton logoutButton = ViewUtils.createButton("Cerrar sesión");
        logoutButton.addActionListener(e -> {
            controller.logout();
            frame.showLogin();
        });
        headerButtonList.add(logoutButton);
        rebuildHeaderButtons();
    }

    private void initTabs() {
        tabs = new JTabbedPane();
        add(tabs, Cell.at(1, 0).span(2).grow().insets(5, 20, 20, 20).build());
        notificationsPanel = new NotificationsPanel();
        tabs.addTab(TAB_NOTIFICATIONS, notificationsPanel);
    }

    private void rebuildHeaderButtons() {
        headerButtons.removeAll();
        for (int i = 0; i < headerButtonList.size(); i++) {
            headerButtons.add(headerButtonList.get(i), Cell.at(0, i).build());
        }
        headerButtons.revalidate();
        headerButtons.repaint();
    }

    protected void addHeaderButton(JButton button) {
        headerButtonList.add(0, button);
        rebuildHeaderButtons();
    }

    protected void addTab(String title, JComponent content) {
        tabs.insertTab(title, null, content, null, tabs.getTabCount() - 1);
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
}
