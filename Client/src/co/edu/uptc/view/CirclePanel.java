package co.edu.uptc.view;

import java.awt.GridBagLayout;
import java.util.List;

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
            // El controller enviará ADD_TO_CIRCLE con el usuario escrito.
        }
    }

    private void answerInvitation(boolean accept) {
        if (invitationsList.getSelectedValue() == null) {
            ViewUtils.showMessage(this, "Selecciona una invitación de la lista.");
        } else {
            // El controller enviará ACCEPT_INVITATION o REJECT_INVITATION; si aceptan y el servidor responde ok,
            // se pasa a la pantalla del proyecto como integrante.
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
