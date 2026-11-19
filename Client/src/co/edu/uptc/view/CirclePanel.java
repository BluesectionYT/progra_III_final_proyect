package co.edu.uptc.view;

import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import co.edu.uptc.interfaces.IController;

public class CirclePanel extends JPanel {
    private IController controller;
    private JList<String> circleList;
    private JTextField memberField;
    private JButton addButton;
    private JList<String> invitationsList;
    private JButton rejectButton;
    private JButton acceptButton;

    public CirclePanel(IController controller, boolean showInvitations) {
        super(new GridBagLayout());
        this.controller = controller;
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
        addButton.addActionListener(e -> addMember());
        panel.add(addButton, Cell.at(1, 1).build());
        return panel;
    }

    private JPanel createInvitationsPanel() {
        invitationsList = new JList<>();
        JPanel panel = ViewUtils.createListPanel("Invitaciones recibidas", invitationsList);
        rejectButton = ViewUtils.createButton("Rechazar");
        acceptButton = ViewUtils.createPrimaryButton("Aceptar");
        rejectButton.addActionListener(e -> answerInvitation(false));
        acceptButton.addActionListener(e -> answerInvitation(true));
        panel.add(ViewUtils.createButtonBar(rejectButton, acceptButton), Cell.at(1, 0).growX().build());
        return panel;
    }

    private void addMember() {
        if (memberField.getText().isBlank()) {
            ViewUtils.showMessage(this, "Escribe el usuario que quieres agregar a tu círculo.");
        } else {
            controller.addToCircle(memberField.getText().trim());
        }
    }

    private void answerInvitation(boolean accept) {
        String invitation = invitationsList.getSelectedValue();
        if (invitation == null) {
            ViewUtils.showMessage(this, "Selecciona una invitación de la lista.");
        } else if (accept) {
            controller.acceptInvitation(invitation);
        } else {
            controller.rejectInvitation(invitation);
        }
    }

    public void setCircle(List<String> members) {
        ViewUtils.setItems(circleList, members);
        memberField.setText("");
    }

    public void setInvitations(List<String> invitations) {
        if (invitationsList != null) {
            ViewUtils.setItems(invitationsList, invitations);
        }
    }
}
