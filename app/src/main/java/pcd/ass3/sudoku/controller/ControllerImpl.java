package pcd.ass3.sudoku.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import pcd.ass3.sudoku.domain.Domain;
import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;
import pcd.ass3.sudoku.domain.Pos;
import pcd.ass3.sudoku.domain.SudokuBoard;
import pcd.ass3.sudoku.domain.SudokuGenerator;
import pcd.ass3.sudoku.mom.DataDistributor;
import pcd.ass3.sudoku.mom.DataDistributorListener;
import pcd.ass3.sudoku.view.UpdateObserver;

public class ControllerImpl implements Controller, DataDistributorListener {

    private final DataDistributor dataDistributor;
    private Optional<UpdateObserver> observer;
    private Optional<BoardInfo> boardInfoJoined;
    private int[][] localSolution;
    private boolean isSolved;
    private Optional<String> nickname;
    private Optional<String> userHexColor;

    public ControllerImpl(DataDistributor dataDistributor) {
        this.dataDistributor = dataDistributor;
        this.observer = Optional.empty();
        this.nickname = Optional.empty();
        this.userHexColor = Optional.empty();
        this.isSolved = false;
    }

    @Override
    public void setUser(String nickname, String hexColor) {
        this.nickname = Optional.of(nickname);
        this.userHexColor = Optional.of(hexColor);
    }

    @Override
    public void joined() {}

    @Override
    public void cellUpdated(DataDistributor.JsonData jsonData) {
        if (!this.isSolved) {
            var edits = Domain.CellUpdate.fromJson(jsonData.getJsonString());
            cacheEdits(edits);
            checkBoardSolution();
            observer.ifPresent(o -> o.cellUpdate(edits));
        }
    }

    private void checkBoardSolution() {
        this.isSolved = this.boardInfoJoined
            .map(info -> Arrays.deepEquals(localSolution, info.solution()))
            .orElse(false);

        if (isSolved) {
            observer.ifPresent(o -> o.boardSolved());
            this.isSolved = true;
        }
    }

    /**
     * Update local copy of boardInfo's riddle with users edits/attempts
     */
    private void cacheEdits(CellUpdate edits) {
        if (localSolution != null) {
            var cellPos = edits.cellPos();
            var value = edits.cellValue();
            localSolution[cellPos.row()][cellPos.col()] = value;
        }
    }

    @Override
    public void cursorsUpdated(DataDistributor.JsonData cursor) {
        var userInfo = Domain.UserInfo.fromJson(cursor.getJsonString());
        observer.ifPresent(o -> o.cursorsUpdate(userInfo));
    }

    @Override
    public void notifyErrors(String errMsg, Exception exc) {
        Optional<String> descr = Optional.empty();
        if (exc != null) {
            descr = Optional.ofNullable(exc.getMessage());
        }
        System.out.println(errMsg + exc);
        if (observer.isPresent()) {
            observer.get().notifyError(errMsg, descr);
        }
    }

    @Override
    public void boardLeft(Boolean hasLeft) {
        if (hasLeft) {
            this.boardInfoJoined = Optional.empty();
            this.localSolution = null;
            this.isSolved = false;
        }
        observer.ifPresent(o -> o.boardLeft(hasLeft));
    }

    @Override
    public void boardRegistered(DataDistributor.JsonData data) {
        var boardData = BoardInfo.fromJson(data.getJsonString());
        observer.ifPresent(o -> o.newBoardCreated(boardData.name()));
    }

    /** 
     * Below there are method of requests from user to be published 
     * */

    @Override
    public void setCellValue(Pos cellPos, int value) {

        DataDistributor.JsonData jsonData = () -> {
            return (new CellUpdate(cellPos, value)).toJson();
        };
        this.dataDistributor.shareUpdate(jsonData);
    }

    @Override
    public List<BoardInfo> getPublishedBoards() {
        return this.dataDistributor.existingBoards()
                                    .stream()
                                    .map(d -> BoardInfo.fromJson(d.getJsonString()))
                                    .toList();
    }

    @Override
    public void createNewBoard(String name, int size) {
        if (boardInfoOf(name).isEmpty()) {
            SudokuBoard boards = SudokuGenerator.generate();
            DataDistributor.JsonData jsonData = () -> {
                return new BoardInfo(
                    boards.riddle(), 
                    boards.complete(), 
                    this.nickname.orElse("unknown"), 
                    name).toJson();
            };
            this.dataDistributor.registerBoard(jsonData);
        } else {
            observer.ifPresent(o -> o.notifyError("Board called " + name + " already exists", Optional.empty()));
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
        this.boardInfoJoined = Optional.empty();
    }

    @Override
    public void joinToBoard(String boardName) {
        boardInfoOf(boardName).ifPresent(info -> {
            this.boardInfoJoined = Optional.of(info);
            this.localSolution = info.riddle();
            observer.ifPresent(o -> o.joined(info));
        });
    }

    @Override
    public void boardLoaded() {
        this.boardInfoJoined.ifPresent(info -> this.dataDistributor.subscribe(info.name()));
    }

    @Override
    public void setObserver(UpdateObserver observer) {
        this.observer = Optional.ofNullable(observer);
    }

}