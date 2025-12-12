
package pcd.ass3.sudoku.communication.rmi.server;

import java.util.List;

import pcd.ass3.sudoku.communication.rmi.client.RmiListener;
import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;
import pcd.ass3.sudoku.domain.Domain.UserInfo;

public interface  RmiServer {

    public void registerListener(RmiListener listener, String boardName);
    public void stopListening(RmiListener listener);
    public void registerBoard(BoardInfo boardInfo);
    public void shareUpdate(CellUpdate cellUpdate);
    public void updateCursor(UserInfo userInfo);
    public List<BoardInfo> existingBoards();

}