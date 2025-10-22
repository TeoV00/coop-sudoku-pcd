package pcd.ass3.sudoku.view.subviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import pcd.ass3.sudoku.Domain;
import pcd.ass3.sudoku.Domain.BoardInfo;
import pcd.ass3.sudoku.controller.Controller;
import pcd.ass3.sudoku.utils.Pair;
import pcd.ass3.sudoku.utils.Pos;
import pcd.ass3.sudoku.view.UpdateObserver;


public final class BoardPanel extends JPanel implements UpdateObserver {

    private final JPanel gridPanel;
    private final JButton[][] cells;
    private Pair<Pos, JButton> selectedCell = null;
    private final Color selectedCellColor;
    private final Map<String, Color> usersCursors;
    private final Controller controller;

    public BoardPanel(Controller controller, String boardName, int size, Color usrColor) {
        this.controller = controller;
        this.usersCursors = new HashMap();
        this.selectedCellColor = usrColor;
        setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.setBackground(Color.WHITE);
        
        // Titolo board
        JLabel boardNameLabel = new JLabel(boardName, SwingConstants.CENTER);
        boardNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        boardNameLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        this.add(boardNameLabel, BorderLayout.NORTH);
        
        // Contenitore per griglia e controlli
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(Color.WHITE);
        
        // Griglia Sudoku 9x9
        gridPanel = new JPanel(new GridLayout(size, size));
        gridPanel.setPreferredSize(new Dimension(500, 500));
        cells = new JButton[size][size];
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                JButton cell = new JButton("");
                cell.setFont(new Font("Arial", Font.BOLD, 20));
                cell.setFocusPainted(false);                
                cell.setBorder(makeBorder(row, col));
                
                final int cellRow = row;
                final int cellCol = col;
                cell.addActionListener((ActionEvent e) -> {
                    selectCell(cellRow, cellCol);
                });
                
                cells[row][col] = cell;
                gridPanel.add(cell);
            }
        }
        
        JPanel gridContainer = new JPanel(new GridBagLayout());
        gridContainer.setBackground(Color.WHITE);
        gridContainer.add(gridPanel);
        
        gamePanel.add(gridContainer, BorderLayout.CENTER);
        
        // Pannello con i numeri 1-9 + pulsante cancella
        JPanel numberPanel = createNumberPanel();
        gamePanel.add(numberPanel, BorderLayout.SOUTH);
        this.add(gamePanel, BorderLayout.CENTER);
        
        // Pannello bottom con pulsante LEAVE
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        var leaveButton = new JButton("LEAVE");
        leaveButton.setFont(new Font("Arial", Font.BOLD, 14));
        leaveButton.setFocusPainted(false);
        leaveButton.addActionListener(e -> leaveBoard());
        
        JPanel leavePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        leavePanel.setBackground(Color.WHITE);
        leavePanel.add(leaveButton);
        
        bottomPanel.add(leavePanel, BorderLayout.EAST);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    private Border makeBorder(int row, int col) {
        int top = (row % 3 == 0) ? 3 : 1;
        int left = (col % 3 == 0) ? 3 : 1;
        int bottom = (row == 8) ? 3 : 1;
        int right = (col == 8) ? 3 : 1;
        return BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK);

    }

    private void leaveBoard() {
      int confirm = JOptionPane.showConfirmDialog(this, 
          "Are you sure you want to leave this board?", 
          "Leave Board", 
          JOptionPane.YES_NO_OPTION);
      if (confirm == JOptionPane.YES_OPTION) {
          // Logica per lasciare la board
            controller.leaveBoard();
      }
    }

    private void selectCell(int row, int col) {
        // Ripristina il colore della cella precedentemente selezionata
        if (selectedCell != null) {
            selectedCell.y().setBorder(makeBorder(row, col));
        }
        selectedCell = new Pair(new Pos(row, col),cells[row][col]);
        controller.selectCell(new Pos(row, col));
        selectedCell.y().setBackground(Color.BLACK);
        selectedCell.y().setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, selectedCellColor));
    }

    private JPanel createNumberPanel() {
        JPanel numberPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        numberPanel.setBackground(Color.WHITE);
        
        for (int i = 1; i <= 9; i++) {
            JButton numberButton = new JButton(String.valueOf(i));
            numberButton.setFont(new Font("Arial", Font.BOLD, 18));
            numberButton.setPreferredSize(new Dimension(50, 50));
            numberButton.setFocusPainted(false);
            
            final int number = i;
            numberButton.addActionListener((ActionEvent e) -> {
                insertNumber(number);
            });
            
            numberPanel.add(numberButton);
        }
        
        // Pulsante per cancellare (X o Clear)
        JButton clearButton = new JButton("X");
        clearButton.setFont(new Font("Arial", Font.BOLD, 18));
        clearButton.setPreferredSize(new Dimension(50, 50));
        clearButton.setFocusPainted(false);
        clearButton.setBackground(new Color(255, 200, 200));
        clearButton.addActionListener((ActionEvent e) -> {
            clearSelectedCell();
        });
        
        numberPanel.add(clearButton);
        
        return numberPanel;
    }
    
    private void insertNumber(int number) {
        if (selectedCell != null) {
            selectedCell.y().setText(String.valueOf(number));
            controller.setCellValue(selectedCell.x(), number);
        } else {
            JOptionPane.showMessageDialog(this, "Seleziona prima una cella!");
        }
    }
    
    private void clearSelectedCell() {
        if (selectedCell != null) {
            selectedCell.y().setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Seleziona prima una cella!");
        }
    }

/*** HERE RECEIVED UPDATE FROM CONTROLLER 
 * CALLS VIEW UPDATES USING INVOKELATER SWING UTILS
 * 
 * 
 */
    @Override
    public void joined(BoardInfo boardInfo) {
    }

    @Override
    public void cellUpdate(Domain.CellUpdate edits) {
    }

    @Override
    public void cursorsUpdate(Domain.UserInfo cursor) {
        //usersCursors.put(cursor.nickname(), Color.decode(cursor.hexColor()));
    }

    @Override
    public void boardLeft(Boolean hasLeft) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "You have left the board."));
    }

    @Override
    public void newBoardCreated(String name) {
    }

    @Override
    public void notifyError(String errMsg, Optional<String> description) {
    }
    

}