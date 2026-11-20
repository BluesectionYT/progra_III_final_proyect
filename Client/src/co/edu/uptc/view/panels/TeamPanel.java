package co.edu.uptc.view.panels;

import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;

import co.edu.uptc.interfaces.IController;
import co.edu.uptc.view.utills.Cell;
import co.edu.uptc.view.utills.ViewUtils;
import co.edu.uptc.view.frames.MainFrame;

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
        super(new GridBagLayout());
        this.frame = frame;
        this.controller = controller;
        ViewUtils.styleAsTab(this);
        initComponents();
    }

    private void initComponents() {
        membersList = new JList<>();
        add(ViewUtils.createListPanel("Integrantes", membersList), Cell.at(0, 0).span(3).grow().build());
        initInviteRow();
        initManagementRow();
    }

    private void initInviteRow() {
        add(ViewUtils.createLabel("Invitar:"), Cell.at(1, 0).build());
        inviteCombo = ViewUtils.createComboBox();
        add(inviteCombo, Cell.at(1, 1).growX().build());
        inviteButton = ViewUtils.createPrimaryButton("Enviar invitación");
        inviteButton.addActionListener(e -> inviteSelectedUser());
        add(inviteButton, Cell.at(1, 2).build());
    }

    private void initManagementRow() {
        removeButton = ViewUtils.createButton("Eliminar integrante");
        removeButton.addActionListener(e -> removeSelectedMember());
        transferButton = ViewUtils.createButton("Transferir administración");
        transferButton.addActionListener(e -> transferToSelectedMember());
        closeProjectButton = ViewUtils.createButton("Cerrar proyecto");
        closeProjectButton.addActionListener(e -> closeProject());

        add(ViewUtils.createButtonBar(removeButton, transferButton, closeProjectButton),
                Cell.at(2, 0).span(3).growX().build());
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
        String password = ViewUtils.askPassword(this, "Confirma tu contraseña para transferir la administración a " + member);
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
