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
     * Request to join boardName board.
     * Before start listening updates you must join the board.
     */
    void requestJoin(String boardName);

    /**
     * Start listening updates of <code>boardName</code> sudoku board.
     * 
     */
    void startUpdatesListening(String boardName);

    /**
     * Stop receiving updates of previously subscribed sudoku board otherwise do nothing.
     */
    void stopListening();

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
