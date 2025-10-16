package pcd.ass3.sudoku;

public interface DataDistributor {

  public interface JsonData {
    String getJsoString();
    //TODO: here i should need other info about message
  }

  void init(SharedDataListener controller);
  void joinBoard(String nickname, String boardName);
  void leaveBoard();
  void shareUpdate(JsonData edits);
  void updateCursor(JsonData userInfo);
  JsonData existingBoards();
}
