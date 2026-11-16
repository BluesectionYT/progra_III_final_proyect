package co.edu.uptc.view;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TeamPanel extends JPanel {
    private JList<String> membersList;
    private JComboBox<String> inviteCombo;
    private JButton inviteButton;
    private JButton removeButton;
    private JButton transferButton;
    private JButton closeProjectButton;

    public TeamPanel() {
        setLayout(null);
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        JPanel membersPanel = ViewUtils.createTitledPanel("Integrantes", 15, 15, 815, 680);
        membersList = new JList<>();
        JScrollPane scroll = new JScrollPane(membersList);
        scroll.setBounds(10, 25, 795, 640);
        scroll.setBorder(null);
        membersPanel.add(scroll);
        add(membersPanel);

        JLabel inviteLabel = new JLabel("Invitar:");
        inviteLabel.setBounds(15, 710, 50, 28);
        add(inviteLabel);

        inviteCombo = new JComboBox<>();
        inviteCombo.setBackground(Color.WHITE);
        inviteCombo.setBounds(70, 710, 110, 28);
        add(inviteCombo);

        inviteButton = ViewUtils.createPrimaryButton("Enviar invitación", 190, 710, 140, 28);
        add(inviteButton);

        removeButton = new JButton("Eliminar integrante");
        removeButton.setBounds(340, 710, 140, 28);
        add(removeButton);

        transferButton = new JButton("Transferir administración");
        transferButton.setBounds(490, 710, 170, 28);
        add(transferButton);

        closeProjectButton = new JButton("Cerrar proyecto");
        closeProjectButton.setBounds(670, 710, 130, 28);
        add(closeProjectButton);
    }
}