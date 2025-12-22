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
        listener.boardRegistered(boardInfo);
    }

    @Override
    public void boardLeft(Boolean hasLeft) throws RemoteException {
        listener.boardLeft(hasLeft);
    }

    @Override
    public void joined(Domain.BoardInfo boardInfo, int[][] currentState) throws RemoteException {
        listener.joined(boardInfo, currentState);
    }

}