package pcd.ass3.sudoku.communication;

import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;
import pcd.ass3.sudoku.domain.Domain.UserInfo;

public interface DataDistributorListener {

    /**
     * Board has been joined. Response to <code>DataDistributor.subscribe</code> method.
     */
    public void joined();

    /**
     * New sudoku board updates has been made by players.
     */
    public void cellUpdated(CellUpdate edits);

    /**
     * New cursor updates has been made by player.
     */
    public void cursorsUpdated(UserInfo userInfo);

    /**
     * Received errors.
     */
    public void notifyErrors(String errMsg, Exception exc);

    /**
     * Response to the <code>DataDistributor.unsubscribe()</code> request.
     */
    public void boardLeft(Boolean hasLeft);

    /**
     * Confirmation to the resquest <code>DataDistributor.registerBoard</code>
     */
    public void boardRegistered(BoardInfo boardInfo);
}