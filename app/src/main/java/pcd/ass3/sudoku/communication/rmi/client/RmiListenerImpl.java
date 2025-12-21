package pcd.ass3.sudoku.communication.rmi.client;

import java.rmi.RemoteException;

import pcd.ass3.sudoku.communication.DataDistributorListener;
import pcd.ass3.sudoku.domain.Domain;

public class RmiListenerImpl implements RmiListener {

    private final DataDistributorListener listener;

    public RmiListenerImpl(DataDistributorListener listener) {
        this.listener = listener;
    }

    @Override
    public void cellUpdated(Domain.CellUpdate edits) throws RemoteException {
        listener.cellUpdated(edits);
    }

    @Override
    public void cursorsUpdated(Domain.UserInfo userInfo) throws RemoteException {
        listener.cursorsUpdated(userInfo);
    }

    @Override
    public void boardRegistered(Domain.BoardInfo boardInfo) throws RemoteException {
        System.out.println("RmiListener: " +  boardInfo);
        listener.boardRegistered(boardInfo);
    }

}