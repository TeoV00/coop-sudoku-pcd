package pcd.ass3.sudoku;


public class ControllerImpl implements SharedDataListener {

    private final DataDistributor dataDistributor;

    public ControllerImpl(DataDistributor dataDistributor) {
      this.dataDistributor = dataDistributor;
    }

    public void initDataSharing(){
      this.dataDistributor.init(this);
    };

    @Override
    public void joined(DataDistributor.JsonData board) {
      System.out.println("joined!!");
      System.out.println(board.getJsoString());
    }

    @Override
    public void boardUpdate(DataDistributor.JsonData edits) {
        System.out.println(edits.getJsoString());
    }

    @Override
    public void cursorsUpdate(DataDistributor.JsonData cursor) {
        System.out.println(cursor.getJsoString());
    }

    @Override
    public void notifyErrors(String errMsg, Exception exc) {
      System.out.println(errMsg + " --> " + exc.getMessage());
    }

    @Override
    public void boardLeft(Boolean hasLeft) {
      System.out.println("You have " + (hasLeft ? "sucessfully" : "NOT" ) + " left board");
    }


    // try {
      //   Messages.UserEdit recvEdits = mapper.readValue(msg, Messages.UserEdit.class);
      //   if (null == recvEdits.type()) {
      //       updateListener.notifyErrors("Message unknown", null);
      //   } else switch (recvEdits.type()) {
      //         case BOARD_CREATION -> {
      //             BoardState board = mapper.readValue(recvEdits.data(), Messages.BoardState.class);
      //             updateListener.joined(board);
      //       }
      //         case CELL_UPDATE -> {
      //             CellUpdate cell = mapper.readValue(recvEdits.data(), Messages.CellUpdate.class);
      //             updateListener.boardUpdate(cell);
      //       }
      //         default -> updateListener.notifyErrors("Message unknown", null);
      //     }
      // } catch (Exception exc) {
      //   this.updateListener.notifyErrors("Parsing error board updates", exc);
      // }
     
      //jsonBoard.ifPresent(bj -> shareUpdate(new Messages.UserEdit(nickname, Messages.DataType.BOARD_CREATION, bj)));

}