package pcd.ass3.sudoku;

import pcd.ass3.sudoku.DataDistributor.JsonData;

public interface SharedDataListener {
  public void joined(JsonData board);
  public void boardUpdate(JsonData edits);
  public void cursorsUpdate(JsonData cursor);
  public void notifyErrors(String errMsg, Exception exc);
  public void boardLeft(Boolean hasLeft);
}