package co.edu.uptc.view.panels;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.components.ViewUtils;

import javax.swing.*;
import java.awt.Color;
import java.util.List;

public class CirclePanel extends JPanel {
    private IController controller;
    private JList<String> circleList;
    private JTextField memberField;
    private JButton addButton;
    private JList<String> invitationsList;
    private JButton rejectButton;
    private JButton acceptButton;

    public CirclePanel(IController controller, boolean showInvitations) {
        this.controller = controller;
        this.setLayout(null);
        this.setBackground(Color.WHITE);
        initComponents(showInvitations);
    }

    private void initComponents(boolean showInvitations) {
        add(createCirclePanel(showInvitations ? 400 : 815));
        if (showInvitations) {
            add(createInvitationsPanel());
        }
    }

    private JPanel createCirclePanel(int width) {
        JPanel panel = ViewUtils.createTitledPanel("Mi círculo de trabajo", 15, 15, width, 700);
        circleList = new JList<>();
        panel.add(ViewUtils.createListScroll(circleList, 10, 25, width - 20, 620));
        memberField(panel, width);
        addButton(panel, width);
        return panel;
    }

    private void memberField(JPanel panel, int width) {
        memberField = ViewUtils.createTextField(10, 655, width - 120);
        panel.add(memberField);
    }

    private void addButton(JPanel panel, int width) {
        addButton = ViewUtils.createPrimaryButton("Agregar", width - 100, 655, 90, 28);
        addButton.addActionListener(e -> addMember());
        panel.add(addButton);
    }

    private JPanel createInvitationsPanel() {
        JPanel panel = ViewUtils.createTitledPanel("Invitaciones recibidas", 430, 15, 400, 700);
        invitationsList = new JList<>();
        panel.add(ViewUtils.createListScroll(invitationsList, 10, 25, 380, 620));
        rejectButton(panel);
        acceptButton(panel);
        return panel;
    }

    private void rejectButton(JPanel panel) {
        rejectButton = ViewUtils.createButton("Rechazar", 195, 655, 90, 28);
        rejectButton.addActionListener(e -> answerInvitation(false));
        panel.add(rejectButton);
    }

    private void acceptButton(JPanel panel) {
        acceptButton = ViewUtils.createPrimaryButton("Aceptar", 295, 655, 90, 28);
        acceptButton.addActionListener(e -> answerInvitation(true));
        panel.add(acceptButton);
    }

    private void addMember() {
        if (memberField.getText().isBlank()) {
            ViewUtils.showMessage(this, ErrorMessages.CIRCLE_USER_REQUIRED.getText());
        } else {
            controller.addToCircle(memberField.getText().trim());
        }
    }

    private void answerInvitation(boolean accept) {
        String invitation = invitationsList.getSelectedValue();
        if (invitation == null) {
            ViewUtils.showMessage(this, ErrorMessages.SELECT_INVITATION.getText());
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
