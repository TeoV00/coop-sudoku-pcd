
package pcd.ass3.sudoku.communication.rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import pcd.ass3.sudoku.communication.rmi.client.RmiListener;
import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;
import pcd.ass3.sudoku.domain.Domain.UserInfo;

public interface  RmiServer extends Remote {

    public void registerListener(RmiListener listener, String boardName) throws RemoteException;
    public void stopListening(RmiListener listener, String boardName) throws RemoteException;
    public void registerBoard(BoardInfo boardInfo) throws RemoteException;
    public void shareUpdate(CellUpdate cellUpdate, String boardName) throws RemoteException;
    public void updateCursor(UserInfo userInfo, String boardName) throws RemoteException;
    public List<BoardInfo> existingBoards() throws RemoteException;

}