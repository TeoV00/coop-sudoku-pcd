package pcd.ass3.sudoku.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import pcd.ass3.sudoku.communication.DataDistributor;
import pcd.ass3.sudoku.communication.DataDistributorListener;
import pcd.ass3.sudoku.domain.Domain;
import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;
import pcd.ass3.sudoku.domain.Domain.UserInfo;
import pcd.ass3.sudoku.domain.Pos;
import pcd.ass3.sudoku.domain.SudokuBoard;
import pcd.ass3.sudoku.domain.SudokuGenerator;
import static pcd.ass3.sudoku.utils.ArrayUtils.extractMask;
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
    public void joined(BoardInfo boardInfo, int[][] state) {
        System.out.println("CONTROLLER: joined at " + System.currentTimeMillis());
        this.boardInfoJoined = Optional.ofNullable(boardInfo);
        this.localSolution = state;
        // is not so efficient
        observer.ifPresent(o -> o.joined(boardInfo, extractMask(boardInfo.riddle(), state)));
    }

    @Override
    public void cellUpdated(CellUpdate edits) {
        if (!this.isSolved) {
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
    public void cursorsUpdated(UserInfo userInfo) {
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
    public void boardRegistered(BoardInfo boardInfo) {
        observer.ifPresent(o -> o.newBoardCreated(boardInfo.name()));
    }

    /** 
     * Below there are method of requests from user to be published 
     * */

    @Override
    public void setCellValue(Pos cellPos, int value) {
        this.dataDistributor.shareUpdate(new CellUpdate(cellPos, value));
    }

    @Override
    public List<BoardInfo> getPublishedBoards() {
        return this.dataDistributor.existingBoards();
    }

    @Override
    public void createNewBoard(String name, int size) {
        if (boardInfoOf(name).isEmpty()) {
            SudokuBoard boards = SudokuGenerator.generate();
            var boardInfo = new BoardInfo(
                    boards.riddle(), 
                    boards.complete(), 
                    this.nickname.orElse("unknown"), 
                    name);
            this.dataDistributor.registerBoard(boardInfo);
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
            var userInfo = new Domain.UserInfo(
                    this.nickname.get(), 
                    this.userHexColor.get(), 
                    cellPos);
            this.dataDistributor.updateCursor(userInfo);
        }
    }

    @Override
    public void leaveBoard() {
        this.dataDistributor.stopListening();
        this.boardInfoJoined = Optional.empty();
    }

    @Override
    public void joinToBoard(String boardName) {
        boardInfoOf(boardName).ifPresent(info -> {
            this.dataDistributor.requestJoin(boardName);
        });
    }

    @Override
    public void boardLoaded() {
        this.observer.ifPresent(o -> o.notifyError("board loaded", Optional.empty()));
        this.boardInfoJoined.ifPresent(info -> this.dataDistributor.startUpdatesListening(info.name()));
    }

    @Override
    public void setObserver(UpdateObserver observer) {
        this.observer = Optional.ofNullable(observer);
    }

}