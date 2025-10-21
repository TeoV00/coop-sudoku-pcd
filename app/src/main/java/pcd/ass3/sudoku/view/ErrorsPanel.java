package pcd.ass3.sudoku.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;

class ErrorsPanel extends JPanel implements ErrorsListener {

  private ListModel listModel = new DefaultListModel<>();
  private final JList errorList;

  public ErrorsPanel() {
        this.listModel = new DefaultListModel<>();
        this.errorList = new JList<>(listModel);
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK));
        this.setPreferredSize(new Dimension(200, 150));

        errorList.enableInputMethods(false);
        JLabel errorsLabel = new JLabel("ERRORS");
        errorsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        errorsLabel.setBorder(new EmptyBorder(10, 5, 10, 5));
        this.add(errorsLabel, BorderLayout.NORTH);
        JScrollPane listScrollPane = new JScrollPane(errorList);
        listScrollPane.setBorder(null);
        this.add(listScrollPane);
  }

    @Override
    public void newError(String error, String description) {
        errorList.setModel(listModel);
        errorList.repaint();
    }

}
        