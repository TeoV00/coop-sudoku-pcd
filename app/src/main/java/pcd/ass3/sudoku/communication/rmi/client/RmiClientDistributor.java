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
import pcd.ass3.sudoku.communication.ErrorDistributorListener;
import pcd.ass3.sudoku.communication.rmi.server.RmiServer;
import pcd.ass3.sudoku.domain.Domain;
import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;

public class RmiClientDistributor implements DataDistributor {

    private Optional<String> subscribedBoard;
    private Optional<ErrorDistributorListener> errorListener;
    private RmiServer server;
    private RmiListener remoteListener;

    public RmiClientDistributor(String serverName) {
        this.subscribedBoard = Optional.empty();
        this.errorListener = Optional.empty();
        try {
            Registry reg = LocateRegistry.getRegistry();
            this.server = (RmiServer) reg.lookup(serverName);
        } catch (RemoteException | NotBoundException e) {
            System.err.println(e);
            // this.listener.ifPresent(l -> l.notifyErrors("RMI Client init error: ", e));
        }
    }

    @Override
    public void init(DataDistributorListener listener) {
        this.errorListener = Optional.of(listener);
        RmiListener rmiListener = new RmiListenerImpl(listener);
        try {
            this.remoteListener = (RmiListener) UnicastRemoteObject.exportObject(rmiListener, 0);
            this.server.registerListener(rmiListener, "GLOBAL");
        } catch (RemoteException ex) {
            System.err.println(ex);
        }
    }

    @Override
    public void registerBoard(BoardInfo boardInfo) {
        try {
            this.server.registerBoard(boardInfo);
        } catch (RemoteException ex) {
            errorListener.ifPresent(l -> l.notifyErrors("Register board error", ex));
        }
    }

    @Override
    public void subscribe(String boardName) {
        this.subscribedBoard = Optional.of(boardName);
        try {
            this.server.registerListener(remoteListener, boardName);
        } catch (RemoteException ex) {
            errorListener.ifPresent(l -> l.notifyErrors("Subscribing board", ex));
        }
    }

    @Override
    public void unsubscribe() {
        this.subscribedBoard.ifPresent(name -> {
            try {
                this.server.stopListening(remoteListener, name);
                this.subscribedBoard = Optional.empty();
            } catch (RemoteException ex) {
                errorListener.ifPresent(l -> l.notifyErrors("Un-subscribing board", ex));
            }
        });
    }

    @Override
    public void shareUpdate(CellUpdate cellUpdate) {
        this.subscribedBoard.ifPresent(name -> {
            try {
                this.server.shareUpdate(cellUpdate, name);
            } catch (RemoteException ex) {
                errorListener.ifPresent(l -> l.notifyErrors("Share cell update", ex));
            }
        });
    }

    @Override
    public void updateCursor(Domain.UserInfo userInfo) {
        this.subscribedBoard.ifPresent(name -> {
            try {
                this.server.updateCursor(userInfo, name);
            } catch (RemoteException ex) {
                errorListener.ifPresent(l -> l.notifyErrors("Update cursor", ex));
            }
        });
    }

    @Override
    public List<BoardInfo> existingBoards() {
        try {
            return this.server.existingBoards();
        } catch (RemoteException ex) {
            errorListener.ifPresent(l -> l.notifyErrors("Existing boards", ex));
        }
        return List.of();
    }
}
