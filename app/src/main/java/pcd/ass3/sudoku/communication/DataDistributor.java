package pcd.ass3.sudoku.communication;

import java.util.List;

import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;
import pcd.ass3.sudoku.domain.Domain.UserInfo;

public interface DataDistributor {

    /**
     * Initialize setting <code>listener</code> to be updated on changes.
     */
    void init(DataDistributorListener listener);

    /**
     * Register new sudoku board providing <code>boardData</code> as JsonData.
     */
    void registerBoard(BoardInfo boardInfo);

    /**
     * Subscribe to <code>boardName</code> sudoku board updates.
     * 
     */
    void subscribe(String boardName);

    /**
     * Stop receiving updates of previously subscribed sudoku board.
     */
    void unsubscribe();

    /**
     * 
     * Share to other players cell update.
     */
    void shareUpdate(CellUpdate cellUpdate);

    /** Share user cursor info update. */
    void updateCursor(UserInfo userInfo);

    /** Returns list of all existing boards. */
    List<BoardInfo> existingBoards();
}
