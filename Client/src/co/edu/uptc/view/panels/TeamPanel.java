package co.edu.uptc.view.panels;

import java.awt.Color;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;

import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.frames.MainFrame;
import co.edu.uptc.view.utills.ViewUtils;

public class TeamPanel extends JPanel {
    private IController controller;
    private JList<String> membersList;
    private JComboBox<String> inviteCombo;
    private JButton inviteButton;
    private JButton removeButton;
    private JButton transferButton;
    private JButton closeProjectButton;

    private MainFrame frame;

    public TeamPanel(MainFrame frame, IController controller) {
        setLayout(null);
        this.frame = frame;
        this.controller = controller;
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        JPanel membersPanel = ViewUtils.createTitledPanel("Integrantes", 15, 15, 815, 680);
        membersList = new JList<>();
        membersPanel.add(ViewUtils.createListScroll(membersList, 10, 25, 795, 640));
        add(membersPanel);
        initInviteRow();
        initManagementRow();
    }

    private void initInviteRow() {
        add(ViewUtils.createLabel("Invitar:", 15, 710, 50));
        inviteCombo = ViewUtils.createComboBox(70, 710, 90);
        add(inviteCombo);
        inviteButton = ViewUtils.createPrimaryButton("Enviar invitación", 170, 710, 150, 28);
        inviteButton.addActionListener(e -> inviteSelectedUser());
        add(inviteButton);
    }

    private void initManagementRow() {
        removeButton = ViewUtils.createButton("Eliminar integrante", 330, 710, 160, 28);
        removeButton.addActionListener(e -> removeSelectedMember());
        add(removeButton);
        transferButton = ViewUtils.createButton("Transferir administración", 500, 710, 190, 28);
        transferButton.addActionListener(e -> transferToSelectedMember());
        add(transferButton);
        closeProjectButton = ViewUtils.createButton("Cerrar proyecto", 700, 710, 130, 28);
        closeProjectButton.addActionListener(e -> closeProject());
        add(closeProjectButton);
    }

    private void inviteSelectedUser() {
        if (inviteCombo.getSelectedItem() == null) {
            ViewUtils.showMessage(this, "Selecciona a quién invitar.");
        } else {
            controller.inviteMember((String) inviteCombo.getSelectedItem());
        }
    }

    private String requireSelectedMember() {
        String member = membersList.getSelectedValue();
        if (member == null) {
            ViewUtils.showMessage(this, "Selecciona un integrante de la lista.");
        }
        return member;
    }

    private void removeSelectedMember() {
        String member = requireSelectedMember();
        if (member == null) {
            return;
        }
        String password = ViewUtils.askPassword(this, "Confirma tu contraseña para eliminar a " + member);
        if (password != null) {
            controller.removeMember(member, password);
        }
    }

    private void transferToSelectedMember() {
        String member = requireSelectedMember();
        if (member == null) {
            return;
        }
        String password = ViewUtils.askPassword(this,
                "Confirma tu contraseña para transferir la administración a " + member);
        if (password != null) {
            controller.transferAdmin(member, password);
        }
    }

    private void closeProject() {
        String password = ViewUtils.askPassword(this, "Confirma tu contraseña para cerrar el proyecto");
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
