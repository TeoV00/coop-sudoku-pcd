package pcd.ass3.sudoku;

import pcd.ass3.sudoku.controller.ControllerImpl;
import pcd.ass3.sudoku.mom.DataDistributor;
import pcd.ass3.sudoku.mom.SharedDataListener;
import pcd.ass3.sudoku.mom.StreamRabbitDistributor;
import pcd.ass3.sudoku.view.SudokuBoardUI;

public class CooperativeSudoku {

    public static void main(String[] args) {
        DataDistributor distr = new StreamRabbitDistributor();//new FakeDataDistributor();
        var controller = new ControllerImpl(distr);
        SharedDataListener listener = controller;
        var gui = new SudokuBoardUI(controller);
        controller.setObserver(gui);
        javax.swing.SwingUtilities.invokeLater(() -> gui.setVisible(true));

        distr.init(listener);
    }
}
