package co.edu.uptc.view.panels;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.components.ViewUtils;
import co.edu.uptc.view.frames.MainFrame;

import javax.swing.*;
import java.awt.Color;
import java.util.List;

public class TeamPanel extends JPanel {
    private IController controller;
    private MainFrame frame;
    private JList<String> membersList;
    private JComboBox<String> inviteCombo;
    private JButton inviteButton;
    private JButton removeButton;
    private JButton transferButton;
    private JButton closeProjectButton;

    public TeamPanel(MainFrame frame, IController controller) {
        this.frame = frame;
        this.controller = controller;
        this.setLayout(null);
        this.setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        membersList();
        inviteCombo();
        inviteButton();
        removeButton();
        transferButton();
        closeProjectButton();
    }

    private void membersList() {
        JPanel membersPanel = ViewUtils.createTitledPanel("Integrantes", 15, 15, 815, 680);
        membersList = new JList<>();
        membersPanel.add(ViewUtils.createListScroll(membersList, 10, 25, 795, 640));
        add(membersPanel);
    }

    private void inviteCombo() {
        add(ViewUtils.createLabel("Invitar:", 15, 710, 50));
        inviteCombo = ViewUtils.createComboBox(70, 710, 90);
        add(inviteCombo);
    }

    private void inviteButton() {
        inviteButton = ViewUtils.createPrimaryButton("Enviar invitación", 170, 710, 150, 28);
        inviteButton.addActionListener(e -> inviteSelectedUser());
        add(inviteButton);
    }

    private void removeButton() {
        removeButton = ViewUtils.createButton("Eliminar integrante", 330, 710, 160, 28);
        removeButton.addActionListener(e -> removeSelectedMember());
        add(removeButton);
    }

    private void transferButton() {
        transferButton = ViewUtils.createButton("Transferir administración", 500, 710, 190, 28);
        transferButton.addActionListener(e -> transferToSelectedMember());
        add(transferButton);
    }

    private void closeProjectButton() {
        closeProjectButton = ViewUtils.createButton("Cerrar proyecto", 700, 710, 130, 28);
        closeProjectButton.addActionListener(e -> closeProject());
        add(closeProjectButton);
    }

    private void inviteSelectedUser() {
        if (inviteCombo.getSelectedItem() == null) {
            ViewUtils.showMessage(this, ErrorMessages.SELECT_INVITEE.getText());
        } else {
            controller.inviteMember((String) inviteCombo.getSelectedItem());
        }
    }

    private String requireSelectedMember() {
        String member = membersList.getSelectedValue();
        if (member == null) {
            ViewUtils.showMessage(this, ErrorMessages.SELECT_MEMBER.getText());
        }
        return member;
    }

    private void removeSelectedMember() {
        String member = requireSelectedMember();
        if (member == null) {
            return;
        }
        String password = ViewUtils.askPassword(this, ErrorMessages.CONFIRM_REMOVE.format(member));
        if (password != null) {
            controller.removeMember(member, password);
        }
    }

    private void transferToSelectedMember() {
        String member = requireSelectedMember();
        if (member == null) {
            return;
        }
        String password = ViewUtils.askPassword(this, ErrorMessages.CONFIRM_TRANSFER.format(member));
        if (password != null) {
            controller.transferAdmin(member, password);
        }
    }

    private void closeProject() {
        String password = ViewUtils.askPassword(this, ErrorMessages.CONFIRM_CLOSE.getText());
        if (password != null) {
            controller.closeProject(password);
        }
    }

    public void setMembers(List<String> members) {
        ViewUtils.setItems(membersList, members);
    }

    public void setInviteCandidates(List<String> users) {
        ViewUtils.setItems(inviteCombo, users);
    }
}
