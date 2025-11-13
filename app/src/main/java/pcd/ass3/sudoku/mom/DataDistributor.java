package pcd.ass3.sudoku.mom;

import java.util.List;

public interface DataDistributor {
  /**
   * Data type acceppted by a DataDistributor
   * It provide data in json string format.
   */
  public interface JsonData {
    String getJsonString();
  }

  /**
   * Initialize setting <code>listener</code> to be updated on changes.
   */
  void init(DataDistributorListener listener);

  /**
   * Register new sudoku board providing <code>boardData</code> as JsonData.
   */
  void registerBoard(JsonData boardData);

  /**
   * Subscribe to <code>boardName</code> sudoku board updates.
   * 
   */
  void subscribe(String boardName);

  /**
   * Stop receiving updates of previously subscribed sudoku board.
   */
  void unsubscribe();

  /**
   * 
   * Share to other players cell update.
   */
  void shareUpdate(JsonData edits);

  /** Share user cursor info update. */
  void updateCursor(JsonData cursorData);

  /** Returns list of all existing boards. */
  List<JsonData> existingBoards();
}
