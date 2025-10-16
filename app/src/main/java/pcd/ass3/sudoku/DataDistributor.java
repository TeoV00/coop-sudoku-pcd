package pcd.ass3.sudoku;

import java.util.Map;

import pcd.ass3.sudoku.domain.Messages;
import pcd.ass3.sudoku.utils.Pos;

public interface DataDistributor {
  public void init(SharedDataListener controller);
  public void joinBoard(String nickname, String boardName, Map<Pos, Integer> initBoard);
  public void leaveBoard();
  public void shareUpdate(Messages.UserEdit edits);
  public void updateCursor(Messages.UserInfo userInfo);
}
