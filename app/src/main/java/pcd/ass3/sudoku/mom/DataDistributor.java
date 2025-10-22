package pcd.ass3.sudoku.mom;

import java.util.List;

public interface DataDistributor {

  public interface JsonData {
    String getJsonString();
    //TODO: here i could carry/need other info about message
  }

  void init(SharedDataListener controller);
  void registerBoard(JsonData boarData);
  void subscribe(String nickname, String boardName);
  void unsubscribe();
  void shareUpdate(JsonData edits);
  void updateCursor(JsonData userInfo);
  List<JsonData> existingBoards();
}
