package pcd.ass3.sudoku;

import pcd.ass3.sudoku.controller.ControllerImpl;
import pcd.ass3.sudoku.mom.DataDistributor.JsonData;
import pcd.ass3.sudoku.mom.SharedDataListener;
import pcd.ass3.sudoku.mom.StreamRabbitDistributor;

public class CooperativeSudoku {

    public static void main(String[] args) {
        StreamRabbitDistributor distr = new StreamRabbitDistributor();
        SharedDataListener listener = new ControllerImpl(distr);
        distr.init(listener);
        distr.joinBoard("pippo", "board1");
        distr.updateCursor((JsonData)() -> 
            "{\"username\": \"pippo\",\"color\": \"ff645d\",\"pos\": {\"x\": 20, \"y\": 20}}");
        // new Thread(() -> {
        //     try {
        //         Thread.sleep(10000);
        //         distr.leaveBoard();
        //     } catch (InterruptedException e) {
        //         Thread.currentThread().interrupt();
        //     }
        // }).start();

        // ObjectMapper mapper = new ObjectMapper();
        // String jsonUpdate;
        // try {
        //     jsonUpdate = mapper.writeValueAsString(new CellUpdate(new Pos(3, 4), 5));
        //     distr.shareUpdate(new Messages.UserEdit("teo00", Messages.DataType.CELL_UPDATE, jsonUpdate));
        // } catch (JsonProcessingException ex) {
        //     System.err.println(ex);
        // }
    }
}

