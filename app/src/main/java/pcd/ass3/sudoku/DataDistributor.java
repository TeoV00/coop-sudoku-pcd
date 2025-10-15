package pcd.ass3.sudoku;

import java.util.Map;

import pcd.ass3.sudoku.domain.Messages;
import pcd.ass3.sudoku.utils.Pair;

public interface DataDistributor {
  public void init(UpdateListener controller);
  public void joinBoard(String nickname, String boardName, Map<Pair, Integer> initBoard);
  public void leaveBoard();
  public void shareUpdate(Messages.UserEdit edits);
  public void updateCursor(Messages.UserInfo userInfo);
}
