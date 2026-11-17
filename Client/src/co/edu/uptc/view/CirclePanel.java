package co.edu.uptc.view;

import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CirclePanel extends JPanel {
    private JList<String> circleList;
    private JTextField memberField;
    private JButton addButton;
    private JList<String> invitationsList;
    private JButton rejectButton;
    private JButton acceptButton;

    /** @param showInvitations false para quien ya está en un proyecto: no puede aceptar invitaciones. */
    public CirclePanel(boolean showInvitations) {
        super(new GridBagLayout());
        ViewUtils.styleAsTab(this);
        initComponents(showInvitations);
    }

    private void initComponents(boolean showInvitations) {
        add(createCirclePanel(), Cell.at(0, 0).grow().build());
        if (showInvitations) {
            add(createInvitationsPanel(), Cell.at(0, 1).grow().build());
        }
    }

    private JPanel createCirclePanel() {
        circleList = new JList<>();
        JPanel panel = ViewUtils.createListPanel("Mi círculo de trabajo", circleList, 2);

        memberField = ViewUtils.createTextField();
        memberField.setColumns(1);
        panel.add(memberField, Cell.at(1, 0).growX().build());

        addButton = ViewUtils.createPrimaryButton("Agregar");
        panel.add(addButton, Cell.at(1, 1).build());
        return panel;
    }

    private JPanel createInvitationsPanel() {
        invitationsList = new JList<>();
        JPanel panel = ViewUtils.createListPanel("Invitaciones recibidas", invitationsList);

        rejectButton = ViewUtils.createButton("Rechazar");
        acceptButton = ViewUtils.createPrimaryButton("Aceptar");
        panel.add(ViewUtils.createButtonBar(rejectButton, acceptButton), Cell.at(1, 0).growX().build());
        return panel;
    }
}
