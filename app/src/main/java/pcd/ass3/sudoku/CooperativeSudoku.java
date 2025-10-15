package pcd.ass3.sudoku;

import java.util.Map;

import pcd.ass3.sudoku.domain.Messages;
import pcd.ass3.sudoku.utils.Pos;

public class CooperativeSudoku {

    public static void main(String[] args) {
        StreamRabbitDistributor distr = new StreamRabbitDistributor();
        UpdateListener listener = new ControllerImpl(distr);
        distr.init(listener);
        distr.joinBoard("pippo", "board1", Map.of(new Pos(3,4), 5));
        distr.updateCursor(new Messages.UserInfo("pippo", "ff645d", new Pos(20, 20)));
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                distr.leaveBoard();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        // ObjectMapper mapper = new ObjectMapper();
        // String jsonUpdate;
        // try {
        //     jsonUpdate = mapper.writeValueAsString(new CellUpdate(new Pair(3, 4), 5));
        //     distr.shareUpdate(new Messages.UserEdit("teo00", "cell-update", jsonUpdate));
        // } catch (JsonProcessingException ex) {
        //     System.err.println(ex);
        // }
    }
}

