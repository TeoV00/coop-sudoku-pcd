package pcd.ass3.sudoku.communication;

public interface ErrorDistributorListener {
    /**
     * Received errors.
     */
    public void notifyErrors(String errMsg, Exception exc);
}