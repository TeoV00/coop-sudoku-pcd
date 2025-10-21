package pcd.ass3.sudoku;

import pcd.ass3.sudoku.controller.ControllerImpl;
import pcd.ass3.sudoku.mom.DataDistributor;
import pcd.ass3.sudoku.mom.DataDistributor.JsonData;
import pcd.ass3.sudoku.mom.FakeDataDistributor;
import pcd.ass3.sudoku.mom.SharedDataListener;
import pcd.ass3.sudoku.view.SudokuBoardUI;

public class CooperativeSudoku {

    public static void main(String[] args) {
        DataDistributor distr = new FakeDataDistributor();//new StreamRabbitDistributor();
        var controller = new ControllerImpl(distr);
        SharedDataListener listener = controller;
        distr.init(listener);
        distr.joinBoard("pippo", "board1");
        distr.updateCursor((JsonData)() -> 
            "{\"username\": \"pippo\",\"color\": \"ff645d\",\"pos\": {\"x\": 20, \"y\": 20}}");
        var gui = new SudokuBoardUI(controller);
        javax.swing.SwingUtilities.invokeLater(() -> gui.setVisible(true));
        
    }
}
