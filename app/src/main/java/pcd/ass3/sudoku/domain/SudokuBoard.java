package pcd.ass3.sudoku.domain;

import com.google.gson.Gson;

import pcd.ass3.sudoku.shared.Pair;

public class SudokuBoard {
    private final Pair<int[][], int[][]> boards;

    public SudokuBoard(int[][] riddle, int[][] complete) {
        this.boards = new Pair<>(riddle, complete);
    }

    public int[][] riddle() {
        return boards.x();
    }

    public int[][] complete() {
        return boards.y();
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static SudokuBoard fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, SudokuBoard.class);
    }
}