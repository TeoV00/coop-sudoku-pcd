package pcd.ass3.sudoku.communication.rmi.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Optional;

import pcd.ass3.sudoku.communication.DataDistributor;
import pcd.ass3.sudoku.communication.DataDistributorListener;
import pcd.ass3.sudoku.communication.rmi.server.RmiServer;
import pcd.ass3.sudoku.domain.Domain;
import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;

public class RmiClientDistributor implements DataDistributor {

    private Optional<DataDistributorListener> listener;
    private final String serverName;
    private RmiServer server;
    private RmiListener remoteListener;

    public RmiClientDistributor(String serverName) {
        this.serverName = serverName;
        try {
            Registry reg = LocateRegistry.getRegistry();
            this.server = (RmiServer) reg.lookup(serverName);
        } catch (RemoteException | NotBoundException e) {
            System.err.println(e);
            //TODO: connection refused
            // this.listener.ifPresent(l -> l.notifyErrors("RMI Client init error: ", e));
        }
    }

    @Override
    public void init(DataDistributorListener listener) {
        this.listener = Optional.ofNullable(listener);
        RmiListener rmiListener = new RmiListenerImpl(listener);
        try {
            this.remoteListener = (RmiListener) UnicastRemoteObject.exportObject(rmiListener, 0);
        } catch (RemoteException ex) {
        }
        
    }

    @Override
    public void registerBoard(BoardInfo boardInfo) {
        try {
            this.server.registerBoard(boardInfo);
        } catch (RemoteException ex) {
            System.getLogger(RmiClientDistributor.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @Override
    public void subscribe(String boardName) {
        try {
            this.server.registerListener(remoteListener, boardName);
        } catch (RemoteException ex) {
            System.getLogger(RmiClientDistributor.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @Override
    public void unsubscribe() {
        //this.server.stopListening(remoteListener);
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
        try {
            return this.server.existingBoards();
        } catch (RemoteException ex) {
            System.getLogger(RmiClientDistributor.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return List.of();
    }
}
