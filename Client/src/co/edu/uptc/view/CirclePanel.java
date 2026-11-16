package co.edu.uptc.view;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CirclePanel extends JPanel{
    public CirclePanel(){
        setLayout(null);
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents(){
        JPanel circlePanel = ViewUtils.createTitledPanel("Mi círculo de trabajo", 15, 15, 400, 700);

        JTextField memberField = new JTextField();
        memberField.setBounds(10, 655, 280, 28);
        circlePanel.add(memberField);

        JButton addButton = ViewUtils.createPrimaryButton("Agregar", 300, 655, 90, 28);
        circlePanel.add(addButton);
        add(circlePanel);

        JPanel invitationsPanel = ViewUtils.createTitledPanel("Invitaciones recibidas", 430, 15, 400, 700);

        JButton rejectButton = new JButton("Rechazar");
        rejectButton.setBounds(195, 655, 90, 28);
        invitationsPanel.add(rejectButton);

        JButton acceptButton = ViewUtils.createPrimaryButton("Aceptar", 295, 655, 90, 28);
        invitationsPanel.add(acceptButton);
        add(invitationsPanel);
    }
}
