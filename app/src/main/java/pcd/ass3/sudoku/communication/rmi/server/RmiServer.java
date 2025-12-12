
package pcd.ass3.sudoku.communication.rmi.server;

import java.rmi.Remote;
import java.util.List;

import pcd.ass3.sudoku.communication.rmi.client.RmiListener;
import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;
import pcd.ass3.sudoku.domain.Domain.UserInfo;

public interface  RmiServer extends Remote {

    public void registerListener(RmiListener listener, String boardName);
    public void stopListening(RmiListener listener, String boardName);
    public void registerBoard(BoardInfo boardInfo);
    public void shareUpdate(CellUpdate cellUpdate, String boardName);
    public void updateCursor(UserInfo userInfo, String boardName);
    public List<BoardInfo> existingBoards();

}