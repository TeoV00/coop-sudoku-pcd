package pcd.ass3.sudoku.domain;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import pcd.ass3.sudoku.utils.Pair;
import pcd.ass3.sudoku.utils.PairDeserializer;

public interface Messages {
  public enum DataType {
    BOARD_CREATION,
    CELL_UPDATE
  };
  public record UserInfo(String nickname, String hexColor, Pair cursorPos){};
  public record UserEdit(String nickname, DataType type, String jsonData){};
  public record CellUpdate (Pair cellPos, Integer cellValue){};
  
  public record BoardState(
    @JsonDeserialize(keyUsing = PairDeserializer.PairKeyDeserializer.class)
    Map<Pair, Integer> board
  ){};
}
