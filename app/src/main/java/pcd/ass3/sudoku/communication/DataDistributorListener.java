package pcd.ass3.sudoku.communication;

import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;
import pcd.ass3.sudoku.domain.Domain.UserInfo;

public interface DataDistributorListener extends ErrorDistributorListener {

    /**
     * Board has been joined. Response to <code>DataDistributor.subscribe</code> method.
     */
    public void joined(BoardInfo boardInfo, int[][] currentState);

    /**
     * New sudoku board updates has been made by players.
     */
    public void cellUpdated(CellUpdate edits);

    /**
     * New cursor updates has been made by player.
     */
    public void cursorsUpdated(UserInfo userInfo);

    /**
     * Response to the <code>DataDistributor.unsubscribe()</code> request.
     */
    public void boardLeft(Boolean hasLeft);

    /**
     * Confirmation to the resquest <code>DataDistributor.registerBoard</code>
     */
    public void boardRegistered(BoardInfo boardInfo);
}