package pcd.ass3.sudoku.communication.rmi.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcd.ass3.sudoku.communication.rmi.client.RmiListener;
import pcd.ass3.sudoku.domain.Domain;

public class RmiServerImpl implements RmiServer {

    private final Map<String, List<RmiListener>> boardObservers;
    private Map<String, Domain.BoardInfo> boards;
    private Map<String, int[][]> boardState;

    public RmiServerImpl() {
        this.boardObservers = new HashMap<>();
    }

    @Override
    public void registerListener(RmiListener listener, String boardName) {
        var boardObs = this.boardObservers.getOrDefault(boardName, new ArrayList<>());
        boardObs.add(listener);
        //TODO: check if listener is added 
    }

    @Override
    public void stopListening(RmiListener listener, String boardName) {
        var obs = this.boardObservers.get(boardName);
        obs.remove(listener);
        //TODO: check if listener is removed 
        //this.boardObservers.put(boardName, );
    }

    @Override
    public void registerBoard(Domain.BoardInfo boardInfo) {
        boolean exists = this.boards.containsKey(boardInfo.name());
        if (!exists) {
            this.boards.put(boardInfo.name(), boardInfo);
            this.boardState.put(boardInfo.name(), boardInfo.riddle());
        }
        //TODO: get all listener registered and update them of new bopard published
    }

    @Override
    public void shareUpdate(Domain.CellUpdate cellUpdate, String boardName) {
        var state = this.boardState.get(boardName);
        var p = cellUpdate.cellPos();
        state[p.row()][p.col()] = cellUpdate.cellValue();
        this.boardObservers.get(boardName).forEach(o -> o.cellUpdated(cellUpdate));
    }

    @Override
    public void updateCursor(Domain.UserInfo userInfo, String boardName) {
        this.boardObservers.get(boardName).forEach(o -> o.cursorsUpdated(userInfo));
    }

    @Override
    public List<Domain.BoardInfo> existingBoards() {
        return List.copyOf(this.boards.values());
    }

}