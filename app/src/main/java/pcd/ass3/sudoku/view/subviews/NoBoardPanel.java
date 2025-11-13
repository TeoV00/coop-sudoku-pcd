package pcd.ass3.sudoku.view.subviews;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class NoBoardPanel extends JPanel {

    public NoBoardPanel() {
        setLayout(new GridBagLayout());
        JLabel label = new JLabel("No board joined, before playing join a board!");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        add(label, new GridBagConstraints());
    }
}