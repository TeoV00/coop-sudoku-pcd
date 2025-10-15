package pcd.ass3.sudoku;

import pcd.ass3.sudoku.domain.Messages;

public interface UpdateListener {
  public void joined(Messages.BoardState board);
  public void boardUpdate(Messages.CellUpdate edits);
  public void cursorsUpdate(Messages.UserInfo cursor);
  public void notifyErrors(String errMsg, Exception exc);
  public void boardLeft(Boolean hasLeft);
}