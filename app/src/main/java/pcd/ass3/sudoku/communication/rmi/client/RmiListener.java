package pcd.ass3.sudoku.communication.rmi.client;
import java.rmi.Remote;
import java.rmi.RemoteException;

import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;
import pcd.ass3.sudoku.domain.Domain.UserInfo;


public interface RmiListener extends Remote {

    public void cellUpdated(CellUpdate edits) throws RemoteException;
    public void cursorsUpdated(UserInfo userInfo) throws RemoteException;
    public void boardRegistered(BoardInfo boardInfo) throws RemoteException;
    public void boardLeft(Boolean hasLeft) throws RemoteException;
    public void joined(BoardInfo boardInfo, int[][] currentState) throws RemoteException;
}