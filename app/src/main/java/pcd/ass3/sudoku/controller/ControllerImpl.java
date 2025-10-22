package pcd.ass3.sudoku.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import pcd.ass3.sudoku.Domain;
import pcd.ass3.sudoku.mom.DataDistributor;
import pcd.ass3.sudoku.mom.SharedDataListener;
import pcd.ass3.sudoku.utils.Pos;
import pcd.ass3.sudoku.view.UpdateObserver;

public class ControllerImpl implements Controller, SharedDataListener {

    private final DataDistributor dataDistributor;
    private UpdateObserver observer;

    public ControllerImpl(DataDistributor dataDistributor) {
      this.dataDistributor = dataDistributor;
    }

    public void initDataSharing(){
      this.dataDistributor.init(this);
    };

    @Override
    public void joined(DataDistributor.JsonData boardData) {
      System.out.println("joined!!");
      System.out.println(boardData.getJsonString());
      int[][] board = null;
      //TODO here update view providing just initial board cells
      // other cells are updated by method boardUpdate()
      observer.joined(board);
    }

    @Override
    public void boardUpdate(DataDistributor.JsonData edits) {
        System.out.println(edits.getJsonString());
        //TODO: parse edits and get info
        observer.cellUpdate(null);
    }

    @Override
    public void cursorsUpdate(DataDistributor.JsonData cursor) {
        System.out.println(cursor.getJsonString());
        //TODO: parse user updates cursor and get info
        observer.cursorsUpdate(null);
    }

    @Override
    public void notifyErrors(String errMsg, Exception exc) {
      Optional<String> descr = Optional.empty();
      if (exc != null) {
        descr = Optional.ofNullable(exc.getMessage());
      }
      observer.notifyErrors(errMsg, descr);
    }

    @Override
    public void boardLeft(Boolean hasLeft) {
      System.out.println("You have " + (hasLeft ? "sucessfully" : "NOT" ) + " left board");
      observer.boardLeft(hasLeft);
    }

    @Override
    public void newBoardCreated(DataDistributor.JsonData data) {
      System.out.println("New board created --> " + data.getJsonString());
      //TODO: extract info of board in order to be shows as available board you can join
      observer.newBoardCreated(null);
    }

      // private Optional<String> toJson(Object obj) {
      //   Optional<String> data = Optional.empty();
      //   ObjectMapper mapper = new ObjectMapper();
      //   try {
      //     data = Optional.ofNullable(mapper.writeValueAsString(obj));
      //   } catch (JsonProcessingException exc) {
      //     this.updateListener.notifyErrors("Json parsing error", exc);
      //   }
      //   return data;
      // }

    @Override
    public void setCellValue(Pos cellPos, int value) {
      // TODO: Replace with actual JsonData creation logic
      DataDistributor.JsonData jsonData = () -> {
          return "";
      };
      this.dataDistributor.shareUpdate(jsonData);
    }

    @Override
    public List<Domain.BoardInfo> getPublishedBoards() {
      return this.dataDistributor.existingBoards().stream()
                .map(d -> new Domain.BoardInfo(Map.of(), "author", "board-name"))
                .toList();
    }

    @Override
    public void createNewBoard(String name, int size) {
      // TODO: Replace with actual JsonData creation logic
      DataDistributor.JsonData jsonData = () -> {
          return "";
      };
      //TODO before registering check if a board with same name exists
      this.dataDistributor.registerBoard(jsonData);
    }

    @Override
    public void selectCell(Pos cellPos) {
      // TODO: Replace with actual JsonData creation logic
      DataDistributor.JsonData jsonData = () -> {
          return "";
      };
      this.dataDistributor.updateCursor(jsonData);
    }

    @Override
    public void leaveBoard() {
      this.dataDistributor.unsubscribe();
    }

    @Override
    public void joinToBoard(String boardName) {
      // TODO: Replace with actual JsonData creation logic
      DataDistributor.JsonData jsonData = () -> {
          return "";
      };
      var usrName = "yee";
      this.dataDistributor.subscribe(usrName, boardName);
    }

    @Override
    public void setObserver(UpdateObserver observer) {
        this.observer = observer;
    }

}