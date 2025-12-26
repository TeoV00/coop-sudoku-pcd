
package pcd.ass3.sudoku.communication.rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import pcd.ass3.sudoku.communication.rmi.client.RmiListener;
import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;
import pcd.ass3.sudoku.domain.Domain.UserInfo;

public interface  RmiServer extends Remote {

    /**
     * Register listener for given boardName board.
     * After registration listener start receiving updates.
     */
    public void registerListener(RmiListener listener, String boardName) throws RemoteException;
    /** Stop listening updates for given boardName board. */
    public void stopListening(RmiListener listener, String boardName) throws RemoteException;
    /** Register new board. */
    public void registerBoard(BoardInfo boardInfo) throws RemoteException;
    /** Share cellUpdate for a given boardName. */
    public void shareUpdate(CellUpdate cellUpdate, String boardName) throws RemoteException;
    /** Share userInfo for a given boardName. */
    public void updateCursor(UserInfo userInfo, String boardName) throws RemoteException;
    /** Request board info (riddle and last edits saved) for a given boardName board.*/
    public void requestBoardData(RmiListener requester, String boardName) throws RemoteException;
    /** Returns list of all BoardInfo published. */
    public List<BoardInfo> existingBoards() throws RemoteException;

}