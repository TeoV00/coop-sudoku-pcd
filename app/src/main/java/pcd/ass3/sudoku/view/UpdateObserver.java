package pcd.ass3.sudoku.view;

import pcd.ass3.sudoku.common.ErrorListener;
import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;
import pcd.ass3.sudoku.domain.Domain.UserInfo;

public interface UpdateObserver extends ErrorListener {
    public void joined(BoardInfo boardInfo);
    public void cellUpdate(CellUpdate edits);
    public void cursorsUpdate(UserInfo cursor);
    public void boardLeft(Boolean hasLeft);
    public void newBoardCreated(String name);
    public void boardSolved();
}