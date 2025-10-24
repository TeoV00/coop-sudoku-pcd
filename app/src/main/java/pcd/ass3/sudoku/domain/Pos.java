package pcd.ass3.sudoku.domain;

import com.google.gson.Gson;

import pcd.ass3.sudoku.utils.Pair;

public class Pos {
  private final Pair<Integer, Integer> pos;

  public Pos(int row, int col) {
    this.pos = new Pair<>(row, col);
  }

  public int row() {
    return pos.x();
  }

  public int col() {
    return pos.y();
  }

  // Convert Pos to JSON string
  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  // Create Pos from JSON string
  public static Pos fromJson(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, Pos.class);
  }
}