package pcd.ass3.sudoku.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
import javax.swing.border.EmptyBorder;

import pcd.ass3.sudoku.Domain;
import pcd.ass3.sudoku.Domain.BoardInfo;
import pcd.ass3.sudoku.controller.Controller;
import pcd.ass3.sudoku.view.subviews.BoardPanel;
import pcd.ass3.sudoku.view.subviews.ErrorsPanel;

public class SudokuBoardUI extends JFrame implements UpdateObserver {
    
    private DefaultListModel<String> boardListModel;
    private final Controller controller;
    private JList<String> boardList;
    private ErrorListener errorsListener;
    private final List<UpdateObserver> subViews;

    public SudokuBoardUI(Controller controller) {
        this.subViews = new ArrayList<>();
        this.controller = controller;
        setTitle("Sudoku Boards");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // TODO: if any board has joined dotn show anything
        // FIX: here "jj" and color should be set after
        JPanel centerPanel = new BoardPanel(controller, "boardInfo.name()", 6, new Color(173, 216, 230));
        add(centerPanel, BorderLayout.CENTER);
        subViews.add((UpdateObserver) centerPanel);
        
        setVisible(true);
    }

    private void updateSubViews(Consumer<UpdateObserver> fun) {
        this.subViews.forEach(view -> fun.accept(view));
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
        boardListModel.clear();
        for (Domain.BoardInfo boardInfo : controller.getPublishedBoards()) {
            boardListModel.addElement(boardInfo.createdBy());
        }
      
        boardList = new JList<>(boardListModel);
        boardList.setFont(new Font("Arial", Font.PLAIN, 14));
        boardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        boardList.setBorder(new EmptyBorder(10, 5, 10, 5));
        
        JScrollPane listScrollPane = new JScrollPane(boardList);
        listScrollPane.setBorder(null);
        
        // JOIN Button
        JButton joinButton = new JButton("JOIN");
        joinButton.setFont(new Font("Arial", Font.BOLD, 14));
        joinButton.setFocusPainted(false);
        joinButton.addActionListener(e -> joinSelectedBoard());
        
        // ERRORS panel
        var errorsPanel = new ErrorsPanel();
        this.errorsListener = errorsPanel;
        
        //Sidebar composition
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBackground(Color.WHITE);
        middlePanel.add(listScrollPane, BorderLayout.CENTER);
        middlePanel.add(joinButton, BorderLayout.SOUTH);
        
        sidebar.add(headerPanel, BorderLayout.NORTH);
        sidebar.add(middlePanel, BorderLayout.CENTER);
        sidebar.add(errorsPanel, BorderLayout.SOUTH);
        
        return sidebar;
    }
    
    
    private void addNewBoard() {
        String boardName = JOptionPane.showInputDialog(this, "Enter new board name:");
        if (boardName != null && !boardName.trim().isEmpty()) {
            controller.createNewBoard(boardName, 6);
        }
    }
    
    private void joinSelectedBoard() {
        String selectedBoard = boardList.getSelectedValue();
        if (selectedBoard != null) {
            controller.joinToBoard(selectedBoard);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a board first.");
        }
    }

    /**
     * The below method are part of interface UpdateObserver.
     * Receives updates from Controller
     */
    @Override
    public void joined(BoardInfo boardInfo) {
        //todo update here this view
        System.out.println("joined");
    }

    @Override
    public void cellUpdate(Domain.CellUpdate edits) {
        updateSubViews(v -> {
            v.cellUpdate(edits);
        });
    }

    @Override
    public void cursorsUpdate(Domain.UserInfo cursor) {
        updateSubViews(v -> {
            v.cursorsUpdate(cursor);
        });
    }

    @Override
    public void notifyError(String errMsg, Optional<String> description) {
        this.errorsListener.notifyError(errMsg, description);
    }

    @Override
    public void boardLeft(Boolean hasLeft) {
        // TODO: clear board panel grid
        //JOptionPane.showMessageDialog(this, "Board left successfully");
        System.out.println("You have " + (hasLeft ? "sucessfully" : "NOT" ) + " left board");
    }

    @Override
    public void newBoardCreated(String name) {
        boardListModel.addElement(name);
    }
    
}

