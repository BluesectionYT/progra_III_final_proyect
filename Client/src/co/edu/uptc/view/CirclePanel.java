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

    public CirclePanel() {
        super(new GridBagLayout());
        ViewUtils.styleAsTab(this);
        initComponents();
    }

    private void initComponents() {
        add(createCirclePanel(), Cell.at(0, 0).grow().build());
        add(createInvitationsPanel(), Cell.at(0, 1).grow().build());
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
