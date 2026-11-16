package co.edu.uptc.view;

import java.awt.GridBagLayout;

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

    public TeamPanel() {
        super(new GridBagLayout());
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
        add(inviteButton, Cell.at(1, 2).build());
    }

    private void initManagementRow() {
        removeButton = ViewUtils.createButton("Eliminar integrante");
        transferButton = ViewUtils.createButton("Transferir administración");
        closeProjectButton = ViewUtils.createButton("Cerrar proyecto");
        add(ViewUtils.createButtonBar(removeButton, transferButton, closeProjectButton),
                Cell.at(2, 0).span(3).growX().build());
    }
}
