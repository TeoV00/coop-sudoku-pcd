package pcd.ass3.sudoku;

import pcd.ass3.sudoku.controller.ControllerImpl;
import pcd.ass3.sudoku.mom.DataDistributor;
import pcd.ass3.sudoku.mom.DataDistributorListener;
import pcd.ass3.sudoku.mom.StreamRabbitDistributor;
import pcd.ass3.sudoku.view.SudokuBoardUI;

public class CooperativeSudoku {

    public static void main(String[] args) {
        DataDistributor distr = new StreamRabbitDistributor();
        var controller = new ControllerImpl(distr);
        DataDistributorListener listener = controller;
        var gui = new SudokuBoardUI(controller);
        controller.setObserver(gui);
        distr.init(listener);
    }
}
