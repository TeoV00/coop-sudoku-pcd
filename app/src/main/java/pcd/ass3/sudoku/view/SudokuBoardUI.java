package pcd.ass3.sudoku.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import pcd.ass3.sudoku.controller.Controller;
import pcd.ass3.sudoku.domain.Messages;

public class SudokuBoardUI extends JFrame {
    
    private DefaultListModel<String> boardListModel;
    private Controller controller;
    private JList<String> boardList;
    private JPanel sudokuGridPanel;
    private JButton[][] cells;
    private JButton selectedCell = null;
    private Color selectedCellColor = new Color(173, 216, 230); // Azzurro chiaro
    
    public SudokuBoardUI(Controller controller) {
        this.controller = controller;
        setTitle("Sudoku Boards");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Layout principale
        setLayout(new BorderLayout());
        
        // Pannello sinistro (sidebar)
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);
        
        // Pannello centrale con griglia sudoku
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Pannello superiore con "BOARDS" e pulsante "+"
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
        
        JLabel boardsLabel = new JLabel("BOARDS");
        boardsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        boardsLabel.setBorder(new EmptyBorder(5, 5, 5, 10));
        
        JButton addButton = new JButton("+");
        addButton.setFont(new Font("Arial", Font.BOLD, 20));
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addNewBoard());
        
        headerPanel.add(boardsLabel, BorderLayout.CENTER);
        headerPanel.add(addButton, BorderLayout.EAST);
        
        // Lista delle board
        boardListModel = new DefaultListModel<>();
        for (Messages.BoardInfo boardInfo : controller.getPublishedBoards()) {
            boardListModel.addElement(boardInfo.createdBy());
        }
      
        boardList = new JList<>(boardListModel);
        boardList.setFont(new Font("Arial", Font.PLAIN, 14));
        boardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        boardList.setBorder(new EmptyBorder(10, 5, 10, 5));
        
        JScrollPane listScrollPane = new JScrollPane(boardList);
        listScrollPane.setBorder(null);
        
        // Pulsante JOIN
        JButton joinButton = new JButton("JOIN");
        joinButton.setFont(new Font("Arial", Font.BOLD, 14));
        joinButton.setFocusPainted(false);
        joinButton.addActionListener(e -> joinSelectedBoard());
        
        // Pannello ERRORS
        JPanel errorsPanel = new JPanel(new BorderLayout());
        errorsPanel.setBackground(Color.WHITE);
        errorsPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK));
        errorsPanel.setPreferredSize(new Dimension(200, 150));
        
        JLabel errorsLabel = new JLabel("ERRORS");
        errorsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        errorsLabel.setBorder(new EmptyBorder(10, 5, 10, 5));
        errorsPanel.add(errorsLabel, BorderLayout.NORTH);
        
        // Assemblaggio sidebar
        sidebar.add(headerPanel, BorderLayout.NORTH);
        
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBackground(Color.WHITE);
        middlePanel.add(listScrollPane, BorderLayout.CENTER);
        middlePanel.add(joinButton, BorderLayout.SOUTH);
        
        sidebar.add(middlePanel, BorderLayout.CENTER);
        sidebar.add(errorsPanel, BorderLayout.SOUTH);
        
        return sidebar;
    }
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        centerPanel.setBackground(Color.WHITE);
        
        // Titolo board
        JLabel boardNameLabel = new JLabel("<BOARD-NAME>", SwingConstants.CENTER);
        boardNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        boardNameLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        centerPanel.add(boardNameLabel, BorderLayout.NORTH);
        
        // Contenitore per griglia e controlli
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(Color.WHITE);
        
        // Griglia Sudoku 9x9
        int size = 6;
        sudokuGridPanel = new JPanel(new GridLayout(size, size));
        sudokuGridPanel.setPreferredSize(new Dimension(500, 500));
        cells = new JButton[size][size];
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                JButton cell = new JButton("");
                cell.setFont(new Font("Arial", Font.BOLD, 20));
                cell.setFocusPainted(false);
                
                // Bordi più spessi per delimitare i blocchi 3x3
                int top = (row % 3 == 0) ? 3 : 1;
                int left = (col % 3 == 0) ? 3 : 1;
                int bottom = (row == 8) ? 3 : 1;
                int right = (col == 8) ? 3 : 1;
                
                cell.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
                
                // Aggiungi listener per selezione cella
                final int cellRow = row;
                final int cellCol = col;
                cell.addActionListener((ActionEvent e) -> {
                    selectCell(cellRow, cellCol);
                });
                
                cells[row][col] = cell;
                sudokuGridPanel.add(cell);
            }
        }
        
        JPanel gridContainer = new JPanel(new GridBagLayout());
        gridContainer.setBackground(Color.WHITE);
        gridContainer.add(sudokuGridPanel);
        
        gamePanel.add(gridContainer, BorderLayout.CENTER);
        
        // Pannello con i numeri 1-9 + pulsante cancella
        JPanel numberPanel = createNumberPanel();
        gamePanel.add(numberPanel, BorderLayout.SOUTH);
        
        centerPanel.add(gamePanel, BorderLayout.CENTER);
        
        // Pannello bottom con pulsante LEAVE
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton leaveButton = new JButton("LEAVE");
        leaveButton.setFont(new Font("Arial", Font.BOLD, 14));
        leaveButton.setFocusPainted(false);
        leaveButton.addActionListener(e -> leaveBoard());
        
        JPanel leavePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        leavePanel.setBackground(Color.WHITE);
        leavePanel.add(leaveButton);
        
        bottomPanel.add(leavePanel, BorderLayout.EAST);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        return centerPanel;
    }
    
    private JPanel createNumberPanel() {
        JPanel numberPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        numberPanel.setBackground(Color.WHITE);
        
        // Pulsanti numerici da 1 a 9
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
    
    private void selectCell(int row, int col) {
        // Ripristina il colore della cella precedentemente selezionata
        if (selectedCell != null) {
            // Bordi più spessi per delimitare i blocchi 3x3
            int top = (row % 3 == 0) ? 3 : 1;
            int left = (col % 3 == 0) ? 3 : 1;
            int bottom = (row == 8) ? 3 : 1;
            int right = (col == 8) ? 3 : 1;
            selectedCell.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
        }
        
        // Seleziona la nuova cella
        selectedCell = cells[row][col];
        selectedCell.setBackground(Color.BLACK);
        // Imposta il bordo della cella selezionata con colore e spessore personalizzati
        selectedCell.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, selectedCellColor));
    }
    
    private void insertNumber(int number) {
        if (selectedCell != null) {
            selectedCell.setText(String.valueOf(number));
        } else {
            JOptionPane.showMessageDialog(this, "Seleziona prima una cella!");
        }
    }
    
    private void clearSelectedCell() {
        if (selectedCell != null) {
            selectedCell.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Seleziona prima una cella!");
        }
    }
    
    // Metodo per cambiare il colore di evidenziazione
    public void setSelectedCellColor(Color color) {
        this.selectedCellColor = color;
        if (selectedCell != null) {
            selectedCell.setBackground(Color.BLACK);
        }
    }
    
    private void addNewBoard() {
        String boardName = JOptionPane.showInputDialog(this, "Enter new board name:");
        if (boardName != null && !boardName.trim().isEmpty()) {
            boardListModel.addElement(boardName.toUpperCase());
        }
    }
    
    private void joinSelectedBoard() {
        String selectedBoard = boardList.getSelectedValue();
        if (selectedBoard != null) {
            controller.joinToBoard(selectedBoard);
            JOptionPane.showMessageDialog(this, "Joined board: " + selectedBoard);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a board first.");
        }
    }
    
    private void leaveBoard() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to leave this board?", 
            "Leave Board", 
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Logica per lasciare la board
            controller.leaveBoard();
           // JOptionPane.showMessageDialog(this, "You have left the board.");
        }
    }
    
}

