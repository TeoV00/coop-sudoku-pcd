package pcd.ass3.sudoku.communication.rmi.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pcd.ass3.sudoku.communication.rmi.client.RmiListener;
import pcd.ass3.sudoku.domain.Domain;

public class RmiServerImpl implements RmiServer {

    /**
     * Name used to share to all clients updates about boards published;
     * I used same mechanism used by board updates notification.
     */
    public final String GLOBAL_UPDATES_TOPIC = "GLOBAL";
    private final Map<String, List<RmiListener>> boardObservers;
    private final Map<String, Domain.BoardInfo> boards;
    private final Map<String, int[][]> boardState;

    public RmiServerImpl() {
        this.boardObservers = new ConcurrentHashMap<>();
        this.boards = new ConcurrentHashMap<>();
        this.boardState = new ConcurrentHashMap<>();
    }

    @Override
    public void registerListener(RmiListener listener, String boardName) throws RemoteException {
        var boardObs = this.boardObservers.getOrDefault(boardName, Collections.synchronizedList(new ArrayList<>()));
        boardObs.add(listener);
        this.boardObservers.put(boardName, boardObs);
    }

    @Override
    public void stopListening(RmiListener listener, String boardName) throws RemoteException {
        var boardObs = this.boardObservers.get(boardName);
        boardObs.remove(listener);
        this.boardObservers.put(boardName, boardObs);
        listener.boardLeft(true);
    }

    @Override
    public void registerBoard(Domain.BoardInfo boardInfo) throws RemoteException {
        boolean exists = this.boards.containsKey(boardInfo.name());
        if (!exists) {
            this.boards.put(boardInfo.name(), boardInfo);
            this.boardState.put(boardInfo.name(), boardInfo.riddle());
            this.boardObservers
                .get(GLOBAL_UPDATES_TOPIC)
                .forEach(rl -> {
                    try {
                        rl.boardRegistered(boardInfo);
                    } catch (RemoteException ex) {
                        System.err.println("SERVER-ERROR: Updating listeners: " + ex);
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

    @Override
    public void requestBoardData(RmiListener requester, String boardName) throws RemoteException {
        requester.joined(boards.get(boardName), boardState.get(boardName));
    }
}