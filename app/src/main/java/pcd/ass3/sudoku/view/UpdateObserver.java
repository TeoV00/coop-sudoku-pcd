package pcd.ass3.sudoku.view;

import pcd.ass3.sudoku.Domain.BoardInfo;
import pcd.ass3.sudoku.Domain.CellUpdate;
import pcd.ass3.sudoku.Domain.UserInfo;

public interface UpdateObserver {
  public void joined(int[][] board);
  public void cellUpdate(CellUpdate edits);
  public void cursorsUpdate(UserInfo cursor);
  public void notifyErrors(String errMsg, Exception exc);
  public void boardLeft(Boolean hasLeft);
  public void newBoardCreated(BoardInfo data);
}