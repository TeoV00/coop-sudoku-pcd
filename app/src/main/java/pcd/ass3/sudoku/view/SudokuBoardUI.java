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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import pcd.ass3.sudoku.shared.ErrorListener;
import pcd.ass3.sudoku.controller.Controller;
import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;
import pcd.ass3.sudoku.domain.Domain.UserInfo;
import pcd.ass3.sudoku.view.subviews.BoardPanel;
import pcd.ass3.sudoku.view.subviews.ErrorsPanel;
import pcd.ass3.sudoku.view.subviews.NoBoardPanel;

public class SudokuBoardUI extends JFrame implements UpdateObserver {

    private final int BOARD_SIZE = 9;
    
    private DefaultListModel<String> boardListModel;
    private final Controller controller;
    private JList<String> boardList;
    private JButton joinButton;
    private ErrorListener errorsListener;
    private final List<UpdateObserver> subViews;
    private JPanel centralPanel;

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

        this.centralPanel = new NoBoardPanel();
        add(centralPanel, BorderLayout.CENTER);

        setVisible(false);

        var usrSets = new UserConfigUI(controller, () -> {
            this.setVisible(true);
        });
        usrSets.setLocationRelativeTo(this);
        usrSets.setAlwaysOnTop(true);
        usrSets.setVisible(true);

    }

    private void updateSubViews(Consumer<UpdateObserver> fun) {
        runAndValidate(() -> this.subViews.forEach(view -> fun.accept(view)));
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setBorder(new EmptyBorder(10, 10, 10, 10));
        
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

        boardListModel = new DefaultListModel<>();
        boardList = new JList<>(boardListModel);
        boardList.setFont(new Font("Arial", Font.PLAIN, 14));
        boardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        boardList.setBorder(new EmptyBorder(10, 5, 10, 5));
        
        JScrollPane listScrollPane = new JScrollPane(boardList);
        listScrollPane.setBorder(null);
        
        // JOIN Button
        this.joinButton = new JButton("JOIN");
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
            controller.createNewBoard(boardName, BOARD_SIZE);
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
        runAndValidate(() -> {
            boardList.setEnabled(false);
            joinButton.setEnabled(false);
            remove(this.centralPanel);
            this.centralPanel = new BoardPanel(controller, boardInfo.name(), BOARD_SIZE, new Color(173, 216, 230));
            add(centralPanel, BorderLayout.CENTER);
            subViews.add((UpdateObserver) centralPanel);
            updateSubViews(v -> {
                v.joined(boardInfo);
            });
        });
    }

    @Override
    public void cellUpdate(CellUpdate edits) {
        updateSubViews(v -> {
            v.cellUpdate(edits);
        });
    }

    @Override
    public void cursorsUpdate(UserInfo cursor) {
        updateSubViews(v -> {
            v.cursorsUpdate(cursor);
        });
    }

    @Override
    public void notifyError(String errMsg, Optional<String> description) {
        runAndValidate(() -> this.errorsListener.notifyError(errMsg, description));
    }

    @Override
    public void boardLeft(Boolean hasLeft) {
        runAndValidate(() -> {
            boardList.setEnabled(true);
            joinButton.setEnabled(true);
            remove(this.centralPanel);
            this.centralPanel = new NoBoardPanel();
            add(centralPanel, BorderLayout.CENTER);
        });
    }

    @Override
    public void newBoardCreated(String name) {
        runAndValidate(() -> boardListModel.addElement(name));
    }

    public void runAndValidate(Runnable doRun) {
        SwingUtilities.invokeLater(() -> {
            doRun.run();
            validate();
        });
    }

    @Override
    public void boardSolved() {
        runAndValidate(() -> {
            JOptionPane.showMessageDialog(this, "Board solved !!");
            updateSubViews(v -> v.boardSolved());
        });
    }
}

