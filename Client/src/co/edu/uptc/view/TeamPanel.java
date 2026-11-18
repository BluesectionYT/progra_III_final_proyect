package co.edu.uptc.view;

import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;

public class TeamPanel extends JPanel {
    private JList<String> membersList;
    private JComboBox<String> inviteCombo;
    private JButton inviteButton;
    private JButton removeButton;
    private JButton transferButton;
    private JButton closeProjectButton;

    private MainFrame frame;

    public TeamPanel(MainFrame frame) {
        super(new GridBagLayout());
        this.frame = frame;
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
            // El controller enviará INVITE_MEMBER con el usuario seleccionado de su círculo.
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
            // El controller enviará REMOVE_MEMBER con el integrante y la contraseña.
        }
    }

    private void transferToSelectedMember() {
        String member = requireSelectedMember();
        if (member == null) {
            return;
        }
        String password = ViewUtils.askPassword(this, "Confirma tu contraseña para transferir la administración a " + member);
        if (password != null) {
            // El controller enviará TRANSFER_ADMIN con el integrante y la contraseña.
        }
    }

    private void closeProject() {
        String password = ViewUtils.askPassword(this, "Confirma tu contraseña para cerrar el proyecto");
        if (password != null) {
            // El controller enviará CLOSE_PROJECT y, si el servidor responde ok, se vuelve a la pantalla sin proyecto.
            frame.showProject();
        }
    }

    public void setMembers(List<String> members) {
        ViewUtils.setItems(membersList, members);
    }

    public void setInviteCandidates(List<String> users) {
        ViewUtils.setItems(inviteCombo, users);
    }
}
