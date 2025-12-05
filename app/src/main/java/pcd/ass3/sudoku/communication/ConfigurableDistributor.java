package pcd.ass3.sudoku.communication;

public interface ConfigurableDistributor {
    /**
     * Set host for connection setup. New host is used after the call of this method.
     * Do not affect the existing connection.
     */
    public void setHost(String host);
}