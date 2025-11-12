
package pcd.ass3.sudoku.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import pcd.ass3.sudoku.controller.Controller;

 class UserConfigUI extends JFrame {
    private Controller controller;
    private Optional<String> nickname;
    private Optional<Color> color;

    protected UserConfigUI(Controller controller, Runnable onValidSetup) {
        this.nickname = Optional.empty();
        this.color = Optional.empty();
        this.controller = controller;
        setTitle("User Configuration");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // Container principale con BoxLayout verticale
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Campo nickname: Label sopra TextField
        JLabel nickLabel = new JLabel("Nickname");
        nickLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField nicknameField = new JTextField(15);
        nicknameField.setMaximumSize(new Dimension(Integer.MAX_VALUE - 50, nicknameField.getPreferredSize().height));

        // Spazio verticale
        mainPanel.add(nickLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(nicknameField);
        mainPanel.add(Box.createVerticalStrut(15));

        // Label per il colore
        JLabel colorLabel = new JLabel("Cursor Color");
        colorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(colorLabel);
        mainPanel.add(Box.createVerticalStrut(5));

        JColorChooser colorChooser = new JColorChooser();
        for (AbstractColorChooserPanel panel : colorChooser.getChooserPanels()) {
            if (!"Swatches".equals(panel.getDisplayName())) {
                colorChooser.removeChooserPanel(panel);
            }
        }

        mainPanel.add(colorChooser);
        mainPanel.add(Box.createVerticalStrut(15));

        // Pulsante OK centrato
        JButton okButton = new JButton("OK");
        okButton.setEnabled(false);
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.addActionListener(e -> {
            if(checkRequiredSet()) {
                Color c = color.get();
                String hex = String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
                this.controller.setUser(this.nickname.get(), hex);
                onValidSetup.run();
                dispose();
            }
        });

        colorChooser.getSelectionModel().addChangeListener(e -> {
            this.color = Optional.ofNullable(colorChooser.getColor());
            okButton.setEnabled(checkRequiredSet());
        });

        var docListener = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                textChanged();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                textChanged();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                textChanged();
            }

            private void textChanged() {
                String str = nicknameField.getText();
                nickname = str.length() > 0 ? Optional.of(str) : Optional.empty();
                okButton.setEnabled(checkRequiredSet());
            }
        };

        nicknameField.getDocument().addDocumentListener(docListener);
        mainPanel.add(okButton);
        setContentPane(mainPanel);
        setResizable(false);
        pack();
    }

    private boolean checkRequiredSet() {
        return this.color.isPresent() && this.nickname.isPresent();
    }

}