package pcd.ass3.sudoku;

import pcd.ass3.sudoku.communication.DataDistributor;
import pcd.ass3.sudoku.communication.rmi.client.RmiClientDistributor;
import pcd.ass3.sudoku.controller.ControllerImpl;
import pcd.ass3.sudoku.view.SudokuBoardUI;

public class CooperativeSudoku {

    public static void main(String[] args) {
        DataDistributor distr = new RmiClientDistributor("RmiServer");// new StreamRabbitDistributor();
        //distr.setHost("192.168.0.2");
        var controller = new ControllerImpl(distr);
        var gui = new SudokuBoardUI(controller);
        controller.setObserver(gui);
        distr.init(controller);
    }
}
