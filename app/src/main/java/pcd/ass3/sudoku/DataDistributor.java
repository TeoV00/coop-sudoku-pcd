package pcd.ass3.sudoku;

import java.util.List;

public interface DataDistributor {

  public interface JsonData {
    String getJsonString();
    //TODO: here i could carry/need other info about message
  }

  void init(SharedDataListener controller);
  void joinBoard(String nickname, String boardName);
  void leaveBoard();
  void shareUpdate(JsonData edits);
  void updateCursor(JsonData userInfo);
  List<JsonData> existingBoards();
}
