package pcd.ass3.sudoku.domain;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import pcd.ass3.sudoku.utils.Pos;
import pcd.ass3.sudoku.utils.PosDeserializer;

public interface Messages {
  public record UserInfo(String nickname, String hexColor, Pos cursorPos){};
  public record UserEdit(String nickname, CellUpdate edits){};
  public record CellUpdate (Pos cellPos, Integer cellValue){};
  
  public record BoardState(
    @JsonDeserialize(keyUsing = PosDeserializer.PosKeyDeserializer.class)
    Map<Pos, Integer> board
  ){};
}
