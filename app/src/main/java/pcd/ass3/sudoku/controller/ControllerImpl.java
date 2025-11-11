package pcd.ass3.sudoku.controller;

import java.util.List;
import java.util.Optional;

import pcd.ass3.sudoku.domain.Domain;
import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;
import pcd.ass3.sudoku.domain.Pos;
import pcd.ass3.sudoku.domain.SudokuBoard;
import pcd.ass3.sudoku.domain.SudokuGenerator;
import pcd.ass3.sudoku.mom.DataDistributor;
import pcd.ass3.sudoku.mom.SharedDataListener;
import pcd.ass3.sudoku.view.UpdateObserver;

public class ControllerImpl implements Controller, SharedDataListener {

    private final DataDistributor dataDistributor;
    private UpdateObserver observer;
    private Optional<String> boardNameJoined;
    private Optional<BoardInfo> boardInfoJoined;
    private Optional<String> nickname;
    private Optional<String> userHexColor;

    public ControllerImpl(DataDistributor dataDistributor) {
      this.dataDistributor = dataDistributor;
      this.boardNameJoined = Optional.empty();
      this.nickname = Optional.empty();
      this.userHexColor = Optional.empty();
      this.dataDistributor.init(this);
    }

    @Override
    public void setUser(String nickname, String hexColor) {
        this.nickname = Optional.of(nickname);
        this.userHexColor = Optional.of(hexColor);
    }

    @Override
    public void joined() {
      // joined the stream of user updates
    }

    @Override
    public void boardUpdate(DataDistributor.JsonData jsonData) {
        var edits = Domain.CellUpdate.fromJson(jsonData.getJsonString());
        cacheEdits(edits);
        observer.cellUpdate(edits);
    }

    /**
     * Update local copy of boardInfo's riddle with users edits/attempts
     */
    private void cacheEdits(CellUpdate edits) {
      boardInfoJoined.ifPresent((boardInfo) -> {
        Pos p = edits.cellPos();
        boardInfo.riddle()[p.row()][p.col()] = edits.cellValue();
      });
    }

    @Override
    public void cursorsUpdate(DataDistributor.JsonData cursor) {
        System.out.println(cursor.getJsonString());
        var userInfo = Domain.UserInfo.fromJson(cursor.getJsonString());
        observer.cursorsUpdate(userInfo);
    }

    @Override
    public void notifyErrors(String errMsg, Exception exc) {
      Optional<String> descr = Optional.empty();
      if (exc != null) {
        descr = Optional.ofNullable(exc.getMessage());
      }
      observer.notifyError(errMsg, descr);
    }

    @Override
    public void boardLeft(Boolean hasLeft) {
      this.boardNameJoined = hasLeft ? Optional.empty() : boardNameJoined;
      observer.boardLeft(hasLeft);
    }

    @Override
    public void newBoardCreated(DataDistributor.JsonData data) {
      var boardData = BoardInfo.fromJson(data.getJsonString());
      observer.newBoardCreated(boardData.name());
    }

    /** 
     * Below there are method of requests from user to be published 
     * */

    @Override
    public void setCellValue(Pos cellPos, int value) {
      // if (this.boardInfoJoined.isPresent()) {
      //   var board = this.boardInfoJoined.get();
      //   var sol = board.solution();
      //   if (sol[cellPos.row()][cellPos.col()] != Integer.parseInt(value)) {
      //     System.out.println("valore errato");
      //     this.observer.notifyError("valore errato", Optional.empty());
      //   }
      // }

      DataDistributor.JsonData jsonData = () -> {
        return (new CellUpdate(cellPos, value)).toJson();
      };
      this.dataDistributor.shareUpdate(jsonData);
    }

    @Override
    public List<BoardInfo> getPublishedBoards() {

      var list = this.dataDistributor.existingBoards()
                                 .stream()
                                 .map(d -> BoardInfo.fromJson(d.getJsonString()))
                                 .toList();

      list.forEach(i -> System.out.println(i));

      return list;
    }

    @Override
    public void createNewBoard(String name, int size) {
      if (boardInfoOf(name).isEmpty()) {
        SudokuBoard boards = SudokuGenerator.generate();
        DataDistributor.JsonData jsonData = () -> {
              return new BoardInfo(boards.riddle(), boards.complete(), this.nickname.orElse("unknown"), name).toJson();
          };
        this.dataDistributor.registerBoard(jsonData);
      } else {
        this.observer.notifyError("Board called " + name + " already exists", Optional.empty());
      }
    }

    private Optional<BoardInfo> boardInfoOf(String name) {
      return this.getPublishedBoards().stream()
                                      .filter(i -> i.name().equals(name))
                                      .findFirst();  
    }

    @Override
    public void selectCell(Pos cellPos) {
        if (this.nickname.isPresent() && this.userHexColor.isPresent()) {
            DataDistributor.JsonData jsonData = () -> {
                return new Domain.UserInfo(
                    this.nickname.get(), 
                    this.userHexColor.get(), 
                    cellPos).toJson();
            };
            this.dataDistributor.updateCursor(jsonData);
        }
    }

    @Override
    public void leaveBoard() {
      this.dataDistributor.unsubscribe();
    }

    @Override
    public void joinToBoard(String boardName) {
      this.boardNameJoined = Optional.of(boardName);
      boardInfoOf(boardName).ifPresent(info -> {
        observer.joined(info);
        this.boardInfoJoined = Optional.of(info);
      });   
    }

    @Override
    public void boardLoaded() {
      this.boardNameJoined.ifPresent(name -> this.dataDistributor.subscribe(name));
    }

    @Override
    public void setObserver(UpdateObserver observer) {
        this.observer = observer;
    }

   

}