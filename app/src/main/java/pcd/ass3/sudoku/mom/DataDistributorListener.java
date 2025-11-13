package pcd.ass3.sudoku.mom;

import pcd.ass3.sudoku.mom.DataDistributor.JsonData;

public interface DataDistributorListener {

    /**
     * Board has been joined. Response to <code>DataDistributor.subscribe</code> method.
     */
    public void joined();

    /**
     * New sudoku board updates has been made by players.
     */
    public void boardUpdate(JsonData edits);

    /**
     * New cursor updates has been made by player.
     */
    public void cursorsUpdate(JsonData cursor);

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
    public void boardRegistered(JsonData data);
}