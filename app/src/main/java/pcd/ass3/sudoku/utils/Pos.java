package pcd.ass3.sudoku.utils;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = PosDeserializer.StdPairDeserializer.class)

public class Pos {
  private final Pair<Integer, Integer> pos;

  public Pos(int row,int col) {
    this.pos = new Pair<>(row, col);
  }

  public int row() {
    return pos.x();
  }

  public int col() {
    return pos.y();
  }

}