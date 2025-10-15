package pcd.ass3.sudoku;

import pcd.ass3.sudoku.domain.Messages;


public class ControllerImpl implements UpdateListener {

    private final DataDistributor dataDistributor;

    public ControllerImpl(DataDistributor dataDistributor) {
      this.dataDistributor = dataDistributor;
    }

    public void initDataSharing(){
      this.dataDistributor.init(this);
    };

    @Override
    public void joined(Messages.BoardState board) {
      System.out.println("joined!!");
      System.out.println(board);
    }

    @Override
    public void boardUpdate(Messages.CellUpdate edits) {
        System.out.println(edits);
    }

    @Override
    public void cursorsUpdate(Messages.UserInfo cursor) {
        System.out.println(cursor);
    }

    @Override
    public void notifyErrors(String errMsg, Exception exc) {
      System.out.println(errMsg + " --> " + exc.getMessage());
    }

    @Override
    public void boardLeft(Boolean hasLeft) {
      System.out.println("You have " + (hasLeft ? "sucessfully" : "NOT" ) + " left board");
    }

}