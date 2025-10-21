package pcd.ass3.sudoku;

import java.util.Map;

import pcd.ass3.sudoku.utils.Pos;

public interface Domain {
  public record UserInfo(String nickname, String hexColor, Pos cursorPos){};
  
  public record UserEdit(String nickname, CellUpdate edits){};
  public record CellUpdate (Pos cellPos, Integer cellValue){};
  
  public record BoardInfo(
    Map<Pos, Integer> board,
    String createdBy
  ){};
}
