package pcd.ass3.sudoku.mom;

import java.util.List;

public interface DataDistributor {

  public interface JsonData {
    String getJsonString();
  }

  void init(SharedDataListener controller);
  void registerBoard(JsonData boarData);
  void subscribe(String boardName);
  void unsubscribe();
  void shareUpdate(JsonData edits);
  void updateCursor(JsonData userInfo);
  List<JsonData> existingBoards();
}
