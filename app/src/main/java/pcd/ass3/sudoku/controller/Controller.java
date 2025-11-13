package pcd.ass3.sudoku.controller;

import java.util.List;

import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Pos;
import pcd.ass3.sudoku.view.UpdateObserver;

public interface Controller {

    void setObserver(UpdateObserver obseeveObserver);

    void setUser(String nickname, String hexColor);

    /**
     * Sets the value of a sudoku cell.
     * @param cellPos cell position
     * @param value value to set
     */
    void setCellValue(Pos cellPos, int value);

    /**
     * Gets the list of available published boards.
     * @return list of published board names
     */
    List<BoardInfo> getPublishedBoards();

    /**
     * Creates a new sudoku board.
     * @param name name of the new board
     * @param size size of the board (e.g. 9 for 9x9)
     */
    void createNewBoard(String name, int size);

    /**
     * Selects a cell in the active board.
     * @param row row of the cell
     * @param col column of the cell
     */
    void selectCell(Pos cellPos);

    /**
     * Leaves the currently connected board.
     */
    void leaveBoard();

    /**
     * Joins a published board.
     * @param boardName name of the board to join
     */
    void joinToBoard(String boardName);

    /** Notify controller that initial board data are successfully loaded.*/
    void boardLoaded();
}