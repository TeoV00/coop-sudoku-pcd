package pcd.ass3.sudoku.communication.rmi.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Optional;

import pcd.ass3.sudoku.communication.DataDistributor;
import pcd.ass3.sudoku.communication.DataDistributorListener;
import pcd.ass3.sudoku.communication.rmi.server.RmiServer;
import pcd.ass3.sudoku.domain.Domain;
import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;

public class RmiClient implements DataDistributor {

    private Optional<DataDistributorListener> listener;
    private final String serverName;
    private RmiServer server;
    private RmiListener updateListener;

    public RmiClient(String serverName) {
        this.serverName = serverName;
        try {
            Registry reg = LocateRegistry.getRegistry();
            this.server = (RmiServer) reg.lookup(serverName);
        } catch (RemoteException | NotBoundException e) {
            this.listener.ifPresent(l -> l.notifyErrors("RMI Client init error: ", e));
        }
        //this.updateListener = new RmiListener();
    }

    @Override
    public void init(DataDistributorListener listener) {
        this.listener = Optional.ofNullable(listener);
    }

    @Override
    public void registerBoard(BoardInfo boardInfo) {
        this.server.registerBoard(boardInfo);
    }

    @Override
    public void subscribe(String boardName) {
        //this.server.registerListener(RmiListener, boardName);
    }

    @Override
    public void unsubscribe() {
        //this.server.stopListening(RmiListener);
    }

    @Override
    public void shareUpdate(CellUpdate cellUpdate) {
        //this.server.shareUpdate(cellUpdate, );
    }

    @Override
    public void updateCursor(Domain.UserInfo userInfo) {
        //this.server.updateCursor(userInfo);
    }

    @Override
    public List<BoardInfo> existingBoards() {
        return this.server.existingBoards();
    }
}
