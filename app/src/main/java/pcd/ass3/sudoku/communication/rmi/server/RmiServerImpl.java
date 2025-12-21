package pcd.ass3.sudoku.communication.rmi.server;

import java.rmi.RemoteException;
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
        this.boards = new HashMap<>();
        this.boardState = new HashMap<>();
    }

    @Override
    public void registerListener(RmiListener listener, String boardName) throws RemoteException {
        var boardObs = this.boardObservers.getOrDefault(boardName, new ArrayList<>());
        boardObs.add(listener);
        System.out.println(this.boardObservers.entrySet());
        //TODO: check if listener is added 
    }

    @Override
    public void stopListening(RmiListener listener, String boardName) throws RemoteException {
        var obs = this.boardObservers.get(boardName);
        obs.remove(listener);
        //TODO: check if listener is removed 
    }

    @Override
    public void registerBoard(Domain.BoardInfo boardInfo) throws RemoteException {
        boolean exists = this.boards.containsKey(boardInfo.name());
        System.out.println(exists);
        if (!exists) {
            System.out.println("Inserting new board");
            this.boards.put(boardInfo.name(), boardInfo);
            this.boardState.put(boardInfo.name(), boardInfo.riddle());
            
            this.boardObservers.values().stream()
                .flatMap(l -> l.stream())
                .distinct()
                .forEach(rl -> {
                    try {
                        rl.boardRegistered(boardInfo);
                    } catch (RemoteException ex) {
                        System.err.println("Updating listeners: " + ex);
                    }
                });
        }
    }

    @Override
    public void shareUpdate(Domain.CellUpdate cellUpdate, String boardName) throws RemoteException {
        var state = this.boardState.get(boardName);
        var p = cellUpdate.cellPos();
        state[p.row()][p.col()] = cellUpdate.cellValue();
        this.boardObservers.get(boardName).forEach(o -> {
            try {
                o.cellUpdated(cellUpdate);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void updateCursor(Domain.UserInfo userInfo, String boardName) throws RemoteException {
        this.boardObservers.get(boardName).forEach(o -> {
            try {
                o.cursorsUpdated(userInfo);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<Domain.BoardInfo> existingBoards() throws RemoteException {
        return List.copyOf(this.boards.values());
    }

}