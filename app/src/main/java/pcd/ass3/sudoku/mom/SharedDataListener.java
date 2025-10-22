package pcd.ass3.sudoku.mom;

import pcd.ass3.sudoku.mom.DataDistributor.JsonData;

public interface SharedDataListener {
  public void joined();
  public void boardUpdate(JsonData edits);
  public void cursorsUpdate(JsonData cursor);
  public void notifyErrors(String errMsg, Exception exc);
  public void boardLeft(Boolean hasLeft);
  public void newBoardCreated(JsonData data);
}